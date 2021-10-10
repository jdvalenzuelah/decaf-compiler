package com.github.dcc.compiler.semanticAnalysis

import com.github.dcc.compiler.Error
import com.github.dcc.compiler.resolvers.ContextualTypeResolver
import com.github.dcc.compiler.symbols.ProgramSymbols
import com.github.dcc.compiler.symbols.variables.SymbolTable
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.symbols.MethodStore
import com.github.dcc.decaf.symbols.TypeStore
import com.github.dcc.decaf.types.Type
import com.github.dcc.parser.DecafBaseVisitor
import com.github.dcc.parser.DecafParser
import com.github.validation.Validated
import com.github.validation.Validation
import com.github.validation.then
import com.github.validation.zip
import org.antlr.v4.runtime.tree.ParseTree
import org.tinylog.kotlin.Logger


object SemanticRules {

    private const val mainMethod = "main"

    object MethodSymbolsRules : Validation<MethodStore, Error> {

        override fun invoke(methods: MethodStore): Validated<Error> {
            return oneMainMethod()
                .then(mainMethodNoParams())
                .then(uniqueMethodsBySignature())
                .invoke(methods)
        }

        private fun oneMainMethod() = Validation<MethodStore, Error> { methods ->
            val mains = methods
                .filter { it.name == mainMethod }

            require(mains.size == 1) {
                mains.semanticError { "Program should contain exactly one main method" }
            }
        }

        private fun mainMethodNoParams() = Validation<MethodStore, Error> { methods ->
            val mains = methods
                .filter { it.name == mainMethod && it.parameters.isNotEmpty() }

            require(mains.isEmpty()) {
                mains.semanticError { "main method should contain no parameters" }
            }
        }

        private fun uniqueMethodsBySignature() = Validation<MethodStore, Error> { methods ->
            val repeated = methods
                .groupBy { it.signature }
                .filter { it.value.size > 1 }
                .flatMap { it.value }

            require(repeated.isEmpty()) {
                repeated.semanticError { "More than one method declared with same signature ${it.signature}" }
            }
        }
    }

    object StructSymbolsRules : Validation<TypeStore, Error> {

        override fun invoke(structs: TypeStore): Validated<Error> {
            return uniqueStructName()
                .invoke(structs)
        }

        private fun uniqueStructName() = Validation<TypeStore, Error> { structs ->
            val repeatedStructs = structs.groupBy { it.name }
                .filter { it.value.size > 1 }
                .flatMap { it.value }

            require(repeatedStructs.isEmpty()) {
                repeatedStructs.semanticError { "redeclaration of struct ${it.name}" }
            }
        }

    }

    object VariableSymbolRules : Validation<SymbolTable, Error> {

        override fun invoke(symbolTable: SymbolTable): Validated<Error> {
            return arraySizeGreaterThanZero()
                .then(noDuplicatedNamesPerScope())
                .invoke(symbolTable)
        }

        private fun arraySizeGreaterThanZero() = Validation<SymbolTable, Error> { symbolTable ->
            val invalidSize = symbolTable.allSymbols()
                .filter { it.type is Type.Array && 0 >= it.type.size }

            require(invalidSize.isEmpty()) {
                invalidSize.semanticError {
                    "array size of variable ${it.name} must be greater than 0"
                }
            }
        }

        private fun noDuplicatedNamesPerScope() = Validation<SymbolTable, Error> { symbolTable ->
            val duplicatedPerScope = symbolTable.allSymbols()
                .groupBy { it.scope }
                .flatMap { (_, symbols) ->
                    symbols.groupBy { it.name }
                        .filter { it.value.size > 1 }
                        .values
                }.flatten()

            require(duplicatedPerScope.isEmpty()) {
                duplicatedPerScope.semanticError {
                    "redeclaration of variable ${it.name} at same scope"
                }
            }
        }

    }

    val symbolsRules = Validation<ProgramSymbols, Error> {
        MethodSymbolsRules(it.methods) then StructSymbolsRules(it.types) then VariableSymbolRules(it.symbolTable)
    }

    internal class MethodBlocksRules(
        private val symbols: SymbolTable,
        private val methods: MethodStore,
        private val structs: TypeStore,
        private val expectedReturn: Type
    ) : DecafBaseVisitor<Validated<Error>>() {

        private val contextualTypeResolver = ContextualTypeResolver(symbols, methods, structs)

        override fun visitMethod_decl(ctx: DecafParser.Method_declContext): Validated<Error> {
            val name = ctx.method_sign().ID()
            Logger.info("Applying semantic rules to code block of method $name")
            return visitBlock(ctx.block())
        }

        override fun visitBlock(ctx: DecafParser.BlockContext): Validated<Error> {
            Logger.info("Visiting code block")
            return ctx.statement().map(::visitStatement).zip()
        }

        override fun visitStatement(ctx: DecafParser.StatementContext): Validated<Error> {
            Logger.info("Visiting statements from code block")
            return when {
                ctx.if_expr() != null -> visitIf_expr(ctx.if_expr())
                ctx.while_expr() != null -> visitWhile_expr(ctx.while_expr())
                ctx.block() != null -> visitBlock(ctx.block())
                ctx.return_expr() != null -> visitReturn_expr(ctx.return_expr())
                ctx.method_call() != null -> visitMethod_call(ctx.method_call())
                ctx.assignment() != null -> visitAssignment(ctx.assignment())
                ctx.expression() != null -> visitExpression(ctx.expression())
                else -> Validated.Valid
            }
        }

        override fun visitIf_expr(ctx: DecafParser.If_exprContext): Validated<Error> {
            val exprType = contextualTypeResolver.visitExpression(ctx.if_block().expression())

            return check(exprType is Type.Boolean) {
                semanticError("if condition must be boolean but got $exprType", ctx)
            }
                .then(visitIf_block(ctx.if_block()))
                .then(ctx.else_block()?.let(::visitElse_block) ?: Validated.Valid)
                .then(visitExpression(ctx.if_block().expression()))
        }

        override fun visitIf_block(ctx: DecafParser.If_blockContext): Validated<Error> {
            val ifBlockScope = symbols.getNextChildScope("if")
            return MethodBlocksRules(ifBlockScope, methods, structs, expectedReturn).visitBlock(ctx.block())
        }

        override fun visitElse_block(ctx: DecafParser.Else_blockContext): Validated<Error> {
            val elseBlockScope = symbols.getNextChildScope("else")
            return MethodBlocksRules(elseBlockScope, methods, structs, expectedReturn).visitBlock(ctx.block())
        }

        override fun visitWhile_expr(ctx: DecafParser.While_exprContext): Validated<Error> {
            val exprType = contextualTypeResolver.visitExpression(ctx.expression())
            val whileScope = symbols.getNextChildScope("while")

            return check(exprType is Type.Boolean) {
                semanticError("while loop condition must be boolean but got $exprType", ctx)
            }
                .then(MethodBlocksRules(whileScope, methods, structs, expectedReturn).visitBlock(ctx.block()))
                .then(visitExpression(ctx.expression()))
        }

        override fun visitReturn_expr(ctx: DecafParser.Return_exprContext): Validated<Error> {
            val expressionType = if(ctx.expression() != null)
                contextualTypeResolver.visitExpression(ctx.expression())
            else Type.Void

            return check(expressionType == expectedReturn) {
                semanticError("method expected return type $expectedReturn but got $expressionType", ctx)
            }
                .then(ctx.expression()?.let(::visitExpression) ?: Validated.Valid)
        }

        override fun visitMethod_call(ctx: DecafParser.Method_callContext): Validated<Error> {
            val callSignature = Declaration.Method.Signature(
                name = ctx.ID().text,
                parametersType = ctx.arg().map { contextualTypeResolver.visitArg(it).noSize() }
            )

            val method = methods.firstOrNull { it.signature == callSignature }

            return check(method != null) {
                semanticError("attempted to call an undefined method signature $callSignature", ctx)
            }
                .then(ctx.arg().map { visitExpression(it.expression()) }.zip())
        }

        override fun visitAssignment(ctx: DecafParser.AssignmentContext): Validated<Error> {
            val locationType = contextualTypeResolver.visitLocation(ctx.location())
            val valueType = contextualTypeResolver.visitExpression(ctx.expression())
            return check(locationType == valueType) {
                semanticError("assignment error expected $locationType but got $valueType", ctx)
            }
                .then(visitLocation(ctx.location()))
                .then(visitExpression(ctx.expression()))
        }

        override fun visitExpression(ctx: DecafParser.ExpressionContext): Validated<Error> {
            return when {
                ctx.method_call() != null -> {
                    val type = contextualTypeResolver.visitMethod_call(ctx.method_call())
                    check(type !is Type.Void) {
                        semanticError("method calls on expressions must return a value", ctx)
                    }.then(visitMethod_call(ctx.method_call()))
                }
                ctx.location() != null -> visitLocation(ctx.location())
                ctx.literal() != null -> visitLiteral(ctx.literal())
                ctx.arith_op_mul() != null || ctx.arith_op_sub() != null || ctx.rel_op() != null -> {
                    kotlin.check(ctx.expression().size == 2){ "more than operand! ${ctx.text}" }
                    val operandTypes = ctx.expression().map { contextualTypeResolver.visitExpression(it) }
                    check(operandTypes.all { it is Type.Int }) {
                        semanticError("expected types Int for operator ${ctx.text} but got $operandTypes", ctx)
                    }
                }
                ctx.eq_op() != null -> {
                    kotlin.check(ctx.expression().size == 2){ "more than operand! ${ctx.text}" }
                    val operandTypes = ctx.expression().map { contextualTypeResolver.visitExpression(it) }

                    val primitiveCheck = check(operandTypes.all { it.isPrimitive }) {
                        semanticError("unsupported operation ${ctx.eq_op().text} for types $operandTypes", ctx)
                    }

                    val sameType = check(operandTypes.distinct().size == 1) {
                        semanticError("unsupported operation ${ctx.eq_op().text} between types $operandTypes", ctx)
                    }

                    primitiveCheck then sameType
                }
                ctx.unary_op() != null -> {
                    if(ctx.unary_op().EXCL() != null) {
                        val operandType = contextualTypeResolver.visitExpression(ctx.expression().first())
                        check(operandType is Type.Boolean) {
                            semanticError("operation ${ctx.unary_op().EXCL().text} only supported for type Boolean but got $operandType", ctx)
                        }
                    } else Validated.Valid
                }
                else -> visitExpression(ctx.expression().first())
            } then ctx.expression().map(::visitExpression).zip()
        }

        private class ArrayAccessCheck(private val symbols: SymbolTable, private val structs: TypeStore, private val structContext: Type.Struct? = null, ) : DecafBaseVisitor<Validated<Error>>() {

            override fun visitLocation(ctx: DecafParser.LocationContext): Validated<Error> {
                val locationCheck = visitVar_location(ctx.var_location())

                val subCheck = if(ctx.sub_location() != null) {
                    val varName = ctx.var_location()?.ID()?.text ?: ctx.var_location()?.location_array()?.ID()?.text ?: ""
                    val type = getVarTypeByIdOnly(varName).let {
                        if(ctx.var_location().location_array() != null) {
                            when(it) {
                                is Type.Array  -> it.type
                                is Type.ArrayUnknownSize -> it.type
                                else -> it
                            }
                        } else it
                    }

                    if(type is Type.Struct) {
                        ArrayAccessCheck(symbols, structs, type)
                            .visitLocation(ctx.sub_location().location())
                    } else check(false) {
                        semanticError("tried to access property of non struct variable $varName of type $type", ctx)
                    }

                } else Validated.Valid

                return locationCheck then subCheck
            }

            override fun visitVar_location(ctx: DecafParser.Var_locationContext): Validated<Error> {
                return when {
                    ctx.location_array() != null -> visitLocation_array(ctx.location_array())
                    else -> Validated.Valid
                }
            }

            override fun visitLocation_array(ctx: DecafParser.Location_arrayContext): Validated<Error> {
                val varName = ctx.ID().text
                val varType = getVarTypeByIdOnly(varName)

                return check(varType is Type.Array || varType is Type.ArrayUnknownSize) {
                    semanticError("array index used on variable $varName of type $varType but can only be used with arrays", ctx)
                }
            }

            private fun getVarTypeByIdOnly(varName: String): Type {
                return if(structContext == null) {
                    symbols.symbolBottomToTop(varName)?.type ?: Type.Nothing
                } else {
                    structs.firstOrNull { it.name == structContext.name }
                        ?.properties?.firstOrNull { it.name == varName }?.type
                        ?: Type.Nothing
                }
            }

        }

        override fun visitLocation(ctx: DecafParser.LocationContext): Validated<Error> {
            val locationType = contextualTypeResolver.visitLocation(ctx)
            return check(locationType !is Type.Nothing) {
                semanticError("tried to access an undefined variables ${ctx.text}", ctx)
            }
                .then(visitVar_location(ctx.var_location()))
                .then(ArrayAccessCheck(symbols, structs).visitLocation(ctx))
        }

        override fun visitVar_location(ctx: DecafParser.Var_locationContext): Validated<Error> {
            return when {
                ctx.ID() != null -> {
                    val declaredLine = symbols.symbolBottomToTop(ctx.ID().text)
                        ?.context?.start?.line ?: Int.MAX_VALUE
                    check(ctx.start.line > declaredLine) {
                        semanticError("tried to access variable before declaration", ctx)
                    }
                }
                ctx.location_array() != null -> visitLocation_array(ctx.location_array())
                else -> Validated.Valid
            }
        }

        override fun visitLocation_array(ctx: DecafParser.Location_arrayContext): Validated<Error> {
            val type = symbols.symbolBottomToTop(ctx.ID().text)?.type
            val isArray = check(type is Type.Array || type is Type.ArrayUnknownSize) {
                semanticError("array index access on non array variable of type $type ${ctx.text}", ctx)
            }

            val expressionType = contextualTypeResolver.visitExpression(ctx.expression())

            val expresionIsInt = check(expressionType is Type.Int) {
                semanticError("array index must be of type ${Type.Int} but got $expressionType", ctx)
            }

            return isArray then expresionIsInt
        }

        override fun visitLiteral(ctx: DecafParser.LiteralContext?): Validated<Error> {
            return Validated.Valid
        }

    }

}