package com.github.dcc.compiler.semanticAnalysis

import com.github.dcc.compiler.Error.SemanticError
import com.github.dcc.compiler.context.*
import com.github.dcc.compiler.resolvers.ContextualTypeResolver
import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.enviroment.contains
import com.github.dcc.decaf.enviroment.lineageAsString
import com.github.dcc.decaf.enviroment.methodScope
import com.github.dcc.decaf.operators.*
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.symbols.findBySignatureOrNull
import com.github.dcc.decaf.symbols.signature
import com.github.dcc.decaf.types.Type
import com.github.dcc.parser.DecafBaseVisitor
import com.github.validation.*

/* TODO: Improve error messages */
class SemanticAnalysis private constructor() : DecafBaseVisitor<Validated<SemanticError>>() {

    companion object {
        operator fun invoke(ctx: Context.ProgramContext): Validated<SemanticError> = SemanticAnalysis().analyze(ctx)
    }

    fun analyze(ctx: Context.ProgramContext): Validated<SemanticError> = validated(ctx) {
        +exactlyOneMainMethod
        +mainMethodDoesNotHaveAnyParams
        +arraySizeGreaterThanZero
        +noDuplicatedVariablesInSameScope
        +noDuplicatedMethodsBySignature
        +uniqueStructName
        +checkMethodsReturnTypes
        +checkMethodCallsAnExistingMethod
        +checkArrayIndexAccess
        +checkAssignmentType
        +checkIfAndWhileBooleanExpression
        +checkNonVoidMethodCallsOnExpressions
        +checkArithOpType
        +checkCondOpType
        +checkNegateType
        +checkLocationWasDeclaredBeforeUsage
        +checkRelOperatorsAreInt
        +checkEqOperatorsArePrimitive
    }

    private val checkArraySize = Validation<Context.VariableContext, SemanticError> {

        when {
            it.declaration.type !is Type.Array || it.declaration.type.size > 0 -> Validated.Valid
            else -> it.semanticError("Invalid array size ${it.declaration.type.size} for variable ${it.declaration.name} at ${it.declaration.scope.lineageAsString()}")
        }
    }

    private val arraySizeGreaterThanZero = SemanticRule { program ->
        program.variables.map { checkArraySize(it) }.zip()
    }

    private val exactlyOneMainMethod = SemanticRule { program ->
        val mainMethods = program.methods.filter { it.declaration.name == "main" }

        if(mainMethods.size == 1)
            Validated.Valid
        else
            mainMethods.map { it.semanticError("Must contain exactly one main method") }
                .zip()

    }

    private val mainMethodDoesNotHaveAnyParams = SemanticRule { program ->
        val mainMethods = program.methods.filter { it.declaration.name == "main" }
            .filter { it.declaration.parameters.isNotEmpty() }

        if(mainMethods.isEmpty())
            Validated.Valid
        else
            mainMethods.map { it.semanticError("main method should not have any parameters") }
                .zip()
    }

    private val noDuplicatedVariablesInSameScope = SemanticRule { program ->
        val checkNoDuplicatedVariables = Validation<Map.Entry<Scope, Collection<Context.VariableContext>>, SemanticError> { (_, variables) ->
            variables.groupBy { it.declaration.name }
                .flatMap { if(it.value.size > 1) it.value else emptyList() }
                .map { it.semanticError("variable ${it.declaration.name} declared more than once") }
                .zip()
        }

        program.allVariables()
            .groupBy { it.declaration.scope }
            .map { checkNoDuplicatedVariables(it) }
            .zip()
    }

    private val noDuplicatedMethodsBySignature = SemanticRule { program ->
        program.methods.groupBy { it.declaration.signature() }
            .map { (signature, methods) ->
                if(methods.size > 1)
                    methods.map {
                        it.semanticError("clashing signature $signature",)
                    }
                        .zip()
                else
                    Validated.Valid
            }
            .zip()
    }

    private val uniqueStructName = SemanticRule { program ->
        val structNames = program.structs.groupBy { it.declaration.name }
        structNames.map { (name, struct) ->
            if(struct.size > 1)
                struct.map {
                    it.semanticError("struct name already defined $name")
                }.zip()
            else Validated.Valid
        }.zip()
    }

    private val checkNotReturnsOrEmptyReturns = Validation<Context.BlockContext, SemanticError> { block ->
        val returns = block.statements.filterIsInstance<Context.StatementContext.Return>()

        if(returns.isEmpty())
            Validated.Valid
        else
            returns.mapNotNull {
                if(it.returnContext.expression != null)
                    it.semanticError("return expression for void methods must be empty")
                else null
            }.zip()
    }

    private fun checkMethodReturnType(type: Type, typeResolver: ContextualTypeResolver) =  Validation<Context.BlockContext, SemanticError> { block ->
        val returns = block.statements
            .flatMap {
                when(it) {
                    is Context.StatementContext.If -> it.ifContext.ifBlockContext.block.statements + (it.ifContext.elseBlock?.block?.statements.orEmpty())
                    is Context.StatementContext.While -> it.whileContext.block.statements
                    is Context.StatementContext.Block -> it.blockContext.statements
                    else -> listOf(it)
                }
            }
            .filterIsInstance<Context.StatementContext.Return>()
            .map {
                if(it.returnContext.expression == null)
                    it.semanticError("empty return expression expected expression type $type")
                else {
                    val returnType = typeResolver.visitExpression(it.returnContext.expression)
                    if(returnType == type) Validated.Valid
                    else
                        it.semanticError("mismatch return type expected $type got $returnType")
                }

            }
        if(returns.isEmpty())
            block.semanticError("No return expression for method type $type")
        else
            returns.zip()
    }

    private val checkMethodsReturnTypes = SemanticRule { program ->
        program.mapMethods { typeResolver, method ->
            when(method.declaration.type) {
                is Type.Void -> method.block?.let(checkNotReturnsOrEmptyReturns) ?: Validated.Valid
                else -> {
                    if (method.block != null)
                        checkMethodReturnType(method.declaration.type, typeResolver)(method.block)
                    else
                        method.semanticError("method ${method.declaration.name} return type ${method.declaration.type} has no body")
                } //check return type
            }
        }.zip()
    }

    private val checkMethodCallsAnExistingMethod = SemanticRule { program ->
        program.mapMethods { typeResolver, method ->
            method.block?.methodCalls()
                ?.map {
                    val signature = typeResolver.getMethodCallSignature(it)
                    if(program.symbols.findBySignatureOrNull(signature) == null) {
                        it.semanticError("trying to call a method not found signature $signature")
                    } else Validated.Valid
                }?.zip()
                ?: Validated.Valid
        }.zip()
    }

    private val checkArrayIndexAccess = SemanticRule { program ->

        program.mapMethods { typeResolver, method ->

            method.block?.locations()
                ?.mapNotNull { if(it is Context.LocationContext.Array) it else null }
                ?.map {
                    val varType = typeResolver.resolveVariableType(it.arrayLocation.id)
                    val expressionType = typeResolver.visitExpression(it.arrayLocation.expression)

                    val accessRes = if(expressionType !is Type.Int)
                        it.semanticError("array index access expression must be ${Type.Int} but is $expressionType")
                    else Validated.Valid

                    val varTypeRes = if(varType !is Type.ArrayUnknownSize && varType !is Type.Array) {
                        it.semanticError("cannot use index access for variable ${it.arrayLocation.id} of type $varType")
                    } else Validated.Valid

                    accessRes then varTypeRes
                }?.zip()
                ?: Validated.Valid
        }.zip()
    }

    private val checkAssignmentType = SemanticRule { program ->
        program.mapMethods { typeResolver, method ->
            method.block?.statements
                ?.filterIsInstance<Context.StatementContext.Assignment>()
                ?.map { assignment ->
                    val locationType = typeResolver.visitLocation(assignment.assignmentContext.location)
                    val expressionType = typeResolver.visitExpression(assignment.assignmentContext.expression)

                    val locationTypeToEval =
                        if(assignment.assignmentContext.location is Context.LocationContext.Array && locationType is Type.Array) {
                            locationType.type
                        } else locationType

                    val expressionTypeToEval =
                    if(assignment.assignmentContext.location is Context.LocationContext.Array && expressionType is Type.Array) {
                        expressionType.type
                    } else expressionType

                    if(locationTypeToEval != expressionTypeToEval)
                        assignment.semanticError("type mismatch expected $locationTypeToEval but got $expressionTypeToEval")
                    else Validated.Valid
                }?.zip()
                ?: Validated.Valid
        }.zip()
    }

    private val checkIfAndWhileBooleanExpression = SemanticRule { program ->
        fun checkIsBoolean(typeResolver: ContextualTypeResolver) = Validation<Context.ExpressionContext, SemanticError> { expr ->
            val exprType = typeResolver.visitExpression(expr)
            if(exprType is Type.Boolean)
                Validated.Valid
            else
                expr.semanticError("expected ${Type.Boolean} expression but got $exprType")
        }
        program.mapMethods { typeResolver, method ->
            method.block?.statements
                ?.map { statement ->
                    when(statement) {
                        is Context.StatementContext.If -> checkIsBoolean(typeResolver)(statement.ifContext.ifBlockContext.expression)
                        is Context.StatementContext.While -> checkIsBoolean(typeResolver)(statement.whileContext.expression)
                        else -> Validated.Valid
                    }
                }?.zip()
                ?: Validated.Valid
        }.zip()
    }

    private val checkNonVoidMethodCallsOnExpressions = SemanticRule { program ->
        program.mapMethods { typeResolver, method ->
            method.expressions().map { expr ->
                val type = typeResolver.visitExpression(expr)
                if(type is Type.Void)
                    expr.semanticError("methods used in expressions must have a return type!")
                else Validated.Valid
            }.zip()
        }.zip()
    }

    private val checkArithOpType = SemanticRule { program ->
        program.mapMethods { typeResolver, method ->
            method.block?.expressions()?.filterIsInstance<Context.ExpressionContext.Equality>()
                ?.flatMap { it.equalityContext.terms() }
                ?.flatMap {
                    if(it.operations.isNotEmpty() || it.factor.operations.isNotEmpty())
                        it.factors()
                    else emptyList()
                }?.flatMap { it.unary() }
                ?.map { unary ->
                    val type = typeResolver.visitUnary(unary)
                    if(type !is Type.Int) {
                        unary.semanticError("operator type for operations ${stringValues<Arithmetic>()} must be of type ${Type.Int} but got $type")
                    } else
                        Validated.Valid
                }?.zip()
                ?: Validated.Valid
        }.zip()
    }

    private val checkCondOpType = SemanticRule { program ->
        program.mapMethods { typeResolver, method ->
            method.block?.expressions()?.filterIsInstance<Context.ExpressionContext.Equality>()
                ?.filter { it.equalityContext.condOperations.isNotEmpty() }
                ?.map { it.equalityContext }
                ?.flatMap { listOf(it.comparison) + it.condOperations.map { op -> op.comparison } }
                ?.map { comp ->
                    val type = typeResolver.visitComparison(comp)
                    if(type !is Type.Boolean)
                        comp.semanticError("operator type for operations ${stringValues<Condition>()} must be of type ${Type.Boolean} but got $type")
                    else Validated.Valid
                }?.zip()
                ?: Validated.Valid
        }.zip()
    }

    private val checkNegateType = SemanticRule { program ->
        program.mapMethods { typeResolver, method ->
            method.block?.expressions()?.filterIsInstance<Context.ExpressionContext.Equality>()
                ?.flatMap { it.equalityContext.terms() }
                ?.flatMap { it.factors() }
                ?.flatMap { it.unary() }
                ?.map { unary ->
                    if(unary is Context.UnaryContext.Operation && unary.operator == Unary.EXCL) {
                        val type = typeResolver.visitUnary(unary)
                        if(type !is Type.Boolean)
                            unary.semanticError("operator type for operations ${Unary.EXCL.op} must be of type ${Type.Boolean} but got $type")
                        else Validated.Valid
                    } else Validated.Valid
                }?.zip()
                ?: Validated.Valid
        }.zip()
    }

    private val checkLocationWasDeclaredBeforeUsage = SemanticRule { program ->
        val variables = program.allVariables()
        program.mapMethods { _, method ->
            val allLocations = method.block?.locations().orEmpty()
            val scope = Scope.methodScope(method.declaration.name)
            allLocations.map {
                val id = when(it) {
                    is Context.LocationContext.Array -> it.arrayLocation.id
                    is Context.LocationContext.Simple -> it.id
                }
                val variable = variables.findVariable(id, scope)
                if(variable == null)
                    it.semanticError("use of undeclared variable $id")
                else {
                    val declaredLine = variable.parserContext?.start?.line ?: 0
                    val usage = it.parserContext.start.line
                    if(declaredLine > usage) it.semanticError("variable $id used before declaration") else Validated.Valid
                }
            }.zip()
        }.zip()
    }

    private val checkRelOperatorsAreInt = SemanticRule { program ->
        program.mapMethods { typeResolver, method ->
            method.block?.expressions()?.filterIsInstance<Context.ExpressionContext.Equality>()
                ?.flatMap { it.equalityContext.comparisson() }
                ?.filter { it.operations.isNotEmpty() }
                ?.map {  comparison ->
                    val applicableOperations = comparison.operations.filter {
                        it.operator is Context.BoolOpContext.RelOp
                    }
                    val terms = listOf(comparison.term) +  applicableOperations.map { it.term }
                    terms.map {
                        val type = typeResolver.visitTerm(it)
                        validate(type is Type.Int) {
                            it.semanticError("operator type for operations ${stringValues<Comparison>()} must be of type ${Type.Int} but got $type")
                        }
                    }.zip()
                }.orEmpty()
                .zip()
        }.zip()
    }

    private val checkEqOperatorsArePrimitive = SemanticRule { program ->
        program.mapMethods { typeResolver, method ->
            method.block?.expressions()?.filterIsInstance<Context.ExpressionContext.Equality>()
                ?.flatMap { it.equalityContext.comparisson() }
                ?.filter { it.operations.isNotEmpty() }
                ?.map {  comparison ->
                    val applicableOperations = comparison.operations.filter {
                        it.operator is Context.BoolOpContext.EqOp
                    }
                    val terms = listOf(comparison.term) +  applicableOperations.map { it.term }
                    terms.map {
                        val type = typeResolver.visitTerm(it)
                        validate(type.isPrimitive) {
                            it.semanticError("operator type for operations ${stringValues<Equality>()} must be of either type ${listOf(Type.Char, Type.Int, Type.Boolean)} but got $type")
                        }
                    }.zip() then terms.validateAll(
                        { typeResolver.visitTerm(it) == typeResolver.visitTerm(comparison.term) },
                        { it.semanticError("operation ${stringValues<Equality>()} between types ${typeResolver.visitTerm(it)} and ${ typeResolver.visitTerm(comparison.term)} not applicable") }
                    )


                }.orEmpty()
                .zip()
        }.zip()
    }


}

private inline fun <reified T> stringValues() where T: Enum<T>, T : Operator = enumValues<T>().joinToString { it.op }

internal fun <O> Context.ProgramContext.mapMethods(op: (ContextualTypeResolver, Context.MethodContext) -> O ): List<O> {
    return methods.map { method ->
        val typeResolver = ContextualTypeResolver(
            symbols,
            structs.map { it.declaration },
            Scope.methodScope(method.declaration.name)
        )
        op(typeResolver, method)
    }
}

fun List<Context.VariableContext>.findVariable(name: String, scope: Scope): Context.VariableContext? {
    return filter { it.declaration.name == name && it.declaration.scope == scope }
        .ifEmpty { filter { it.declaration.name == name && it.declaration.scope.contains(scope) } }
        .firstOrNull()
}