package com.github.dcc.compiler.semanticAnalysis

import com.github.dcc.compiler.Error.SemanticError
import com.github.dcc.compiler.context.*
import com.github.dcc.compiler.resolvers.ContextualTypeResolver
import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.enviroment.lineageAsString
import com.github.dcc.decaf.enviroment.methodScope
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
        val returns = block.statements.filterIsInstance<Context.StatementContext.Return>()
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
        program.methods.map { methodContext ->
            when(methodContext.declaration.type) {
                is Type.Void -> methodContext.block?.let(checkNotReturnsOrEmptyReturns) ?: Validated.Valid
                else -> {
                    val typeResolver = ContextualTypeResolver(
                        program.symbols,
                        program.structs.map { it.declaration },
                        methodScope(methodContext.declaration.name)
                    )
                    if (methodContext.block != null)
                        checkMethodReturnType(methodContext.declaration.type, typeResolver)(methodContext.block)
                    else
                        methodContext.semanticError("method ${methodContext.declaration.name} return type ${methodContext.declaration.type} has no body")
                } //check return type
            }
        }.zip()
    }

    private val checkMethodCallsAnExistingMethod = SemanticRule { program ->
        program.methods.map { method ->

            val typeResolver = ContextualTypeResolver(
                program.symbols,
                program.structs.map { it.declaration },
                methodScope(method.declaration.name)
            )

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
        program.methods.map { method ->
            val typeResolver = ContextualTypeResolver(
                program.symbols,
                program.structs.map { it.declaration },
                methodScope(method.declaration.name)
            )

            method.block?.locations()
                ?.mapNotNull { if(it is Context.LocationContext.Array) it else null }
                ?.map {
                    val expressionType = typeResolver.visitExpression(it.arrayLocation.expression)
                    if(expressionType !is Type.Int)
                        it.semanticError("array index access expression must be ${Type.Int} but is $expressionType")
                    else Validated.Valid
                }?.zip()
                ?: Validated.Valid
        }.zip()
    }

    private val checkAssignmentType = SemanticRule { program ->
        program.methods.map { method ->

            val typeResolver = ContextualTypeResolver(
                program.symbols,
                program.structs.map { it.declaration },
                methodScope(method.declaration.name)
            )

            method.block?.statements
                ?.filterIsInstance<Context.StatementContext.Assignment>()
                ?.map { assignment ->
                    val locationType = typeResolver.visitLocation(assignment.assignmentContext.location)
                    val expressionType = typeResolver.visitExpression(assignment.assignmentContext.expression)

                    if(locationType != expressionType)
                        assignment.semanticError("type mismatch expected $locationType but got $expressionType")
                    else Validated.Valid
                }?.zip()
                ?: Validated.Valid
        }.zip()
    }

}