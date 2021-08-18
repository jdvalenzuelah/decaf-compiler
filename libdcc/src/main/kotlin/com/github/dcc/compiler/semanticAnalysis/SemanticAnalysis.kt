package com.github.dcc.compiler.semanticAnalysis

import com.github.dcc.compiler.context.Context
import com.github.dcc.compiler.context.allVariables
import com.github.dcc.compiler.context.symbols
import com.github.dcc.compiler.resolvers.ContextualTypeResolver
import com.github.dcc.compiler.resolvers.DeclarationResolver
import com.github.dcc.compiler.resolvers.StaticTypeResolver
import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.enviroment.lineageAsString
import com.github.dcc.decaf.semanticRules.GeneralDeclarationRules
import com.github.dcc.decaf.semanticRules.GlobalScopeRules
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.symbols.signature
import com.github.dcc.decaf.types.Type
import com.github.dcc.parser.DecafBaseVisitor
import com.github.dcc.parser.DecafParser
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
    }

    private val checkArraySize = Validation<Context.VariableContext, SemanticError> {
        when {
            it.declaration.type !is Type.Array || it.declaration.type.size > 0 -> Validated.Valid
            else -> Validated.Invalid(
                SemanticError("Invalid array size ${it.declaration.type.size} for variable ${it.declaration.name} at ${it.declaration.scope.lineageAsString()}", it.parserContext, 0)
            )
        }
    }

    private val arraySizeGreaterThanZero = Validation<Context.ProgramContext, SemanticError> { program ->
        program.variables.map { checkArraySize(it) }.zip()
    }

    private val exactlyOneMainMethod = Validation<Context.ProgramContext, SemanticError> { program ->
        val mainMethods = program.methods.filter { it.declaration.name == "main" }

        if(mainMethods.size == 1)
            Validated.Valid
        else
            mainMethods.map { Validated.Invalid(SemanticError("Must contain exactly one main method", it.parserContext, 0)) }
                .zip()

    }

    private val mainMethodDoesNotHaveAnyParams = Validation<Context.ProgramContext, SemanticError> { program ->
        val mainMethods = program.methods.filter { it.declaration.name == "main" }
            .filter { it.declaration.parameters.isNotEmpty() }

        if(mainMethods.isEmpty())
            Validated.Valid
        else
            mainMethods.map { Validated.Invalid(SemanticError("main method should not have any parameters", it.parserContext, 0)) }
                .zip()
    }

    private val noDuplicatedVariablesInSameScope = Validation<Context.ProgramContext, SemanticError> { program ->
        val checkNoDuplicatedVariables = Validation<Map.Entry<Scope, Collection<Context.VariableContext>>, SemanticError> { (_, variables) ->
            variables.groupBy { it.declaration.name }
                .flatMap { if(it.value.size > 1) it.value else emptyList() }
                .map { Validated.Invalid(SemanticError("variable ${it.declaration.name} declared more than once", it.parserContext, 0)) }
                .zip()
        }

        program.allVariables()
            .groupBy { it.declaration.scope }
            .map { checkNoDuplicatedVariables(it) }
            .zip()
    }

    private val noDuplicatedMethodsBySignature = Validation<Context.ProgramContext, SemanticError> { program ->
        program.methods.groupBy { it.declaration.signature() }
            .map { (signature, methods) ->
                if(methods.size > 1)
                    methods.map {
                        Validated.Invalid(SemanticError("clashing signature $signature", it.parserContext, 0))
                    }
                        .zip()
                else
                    Validated.Valid
            }
            .zip()
    }

    private val uniqueStructName = Validation<Context.ProgramContext, SemanticError> { program ->
        val structNames = program.structs.groupBy { it.declaration.name }
        structNames.map { (name, struct) ->
            if(struct.size > 1)
                struct.map {
                    Validated.Invalid(SemanticError("struct name already defined $name", it.parserContext, 0))
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
                    Validated.Invalid(SemanticError("return expression for void methods must be empty", it.parserContext, 0))
                else null
            }.zip()
    }

    private fun checkMethodReturnType(type: Type, typeResolver: ContextualTypeResolver) =  Validation<Context.BlockContext, SemanticError> { block ->
        val returns = block.statements.filterIsInstance<Context.StatementContext.Return>()
            .map {
                if(it.returnContext.expression == null)
                    Validated.Invalid(SemanticError("empty return expression", it.parserContext, 0))
                else {
                    val returnType = typeResolver.visitExpression(it.returnContext.expression)
                    if(returnType == type) Validated.Valid
                    else
                        Validated.Invalid(SemanticError("mismatch return type expected $type got $returnType", it.parserContext, 0))
                }

            }
        if(returns.isEmpty())
            Validated.Invalid(SemanticError("No return expression for method type $type", block.parserContext, 0))
        else
            returns.zip()
    }

    private val checkMethodsReturnTypes = Validation<Context.ProgramContext, SemanticError> { program ->
        program.methods.map { methodContext ->
            when(methodContext.declaration.type) {
                is Type.Void -> methodContext.block?.let(checkNotReturnsOrEmptyReturns) ?: Validated.Valid
                else -> {
                    val typeResolver = ContextualTypeResolver(
                        program.symbols,
                        program.structs.map { it.declaration },
                        methodContext.declaration.scope
                    )
                    if (methodContext.block != null)
                        checkMethodReturnType(methodContext.declaration.type, typeResolver)(methodContext.block)
                    else
                        Validated.Invalid(SemanticError("method ${methodContext.declaration.name} return type ${methodContext.declaration.type} has no body", methodContext.parserContext, 0))
                } //check return type
            }
        }.zip()
    }

}