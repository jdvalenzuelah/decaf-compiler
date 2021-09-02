package com.github.dcc.compiler.resolvers

import com.github.dcc.compiler.context.Context.*
import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.enviroment.contains
import com.github.dcc.decaf.symbols.*
import com.github.dcc.decaf.types.Type

/* pseudo visitor */
internal class ContextualTypeResolver(
    symbols: SymbolStore,
    val types: TypeStore,
    val scope: Scope,
) {

    val symbols: SymbolStore = symbols.filter { it.scope == scope || it.scope is Scope.Global }

    init {
        require(symbols.all { it is Declaration.Variable || it is Declaration.Method }) {
            "Symbols can only be either variables or methods!"
        }
    }

    fun visitExpression(ctx: ExpressionContext): Type {
        return when(ctx) {
            is ExpressionContext.Literal -> visitLiteral(ctx.literalContext)
            is ExpressionContext.MethodCall -> visitMethodCall(ctx.methodCallContext)
            is ExpressionContext.Location -> visitLocation(ctx.locationContext)
            is ExpressionContext.Equality -> visitEquality(ctx.equalityContext)
        }
    }

    fun getMethodCallSignature(ctx: MethodCallContext): Signature = Signature(
        name = ctx.name,
        parameters = ctx.args.map(::visitArgument)
    )

    fun visitMethodCall(ctx: MethodCallContext): Type {
        val callSignature = getMethodCallSignature(ctx)
        return symbols.findBySignatureOrNull(callSignature)?.type ?: Type.Nothing
    }

    fun visitArgument(ctx: ArgContext): Type = visitExpression(ctx.expression)

    //TODO: Nested locations are failing
    fun visitLocation(ctx: LocationContext): Type = resolveVariableType(ctx, null)

    fun visitLocationArray(ctx: LocationArrayContext): Type  {
        return when(val type = resolveVariableType(ctx.id)) {
            is Type.Array -> type.type
            is Type.ArrayUnknownSize -> type.type
            else -> type
        }

    }

    fun visitEquality(ctx: EqualityContext): Type {
        /* cond and eq op result is boolean, operand types must be checked by semantic analysis */
        return if(ctx.condOperations.isEmpty() && ctx.eqOperations.isEmpty())
            visitComparison(ctx.comparison)
        else
            Type.Boolean
    }

    fun visitComparison(ctx: ComparisonContext): Type {
        /* boolean op result is boolean, operand types must be checked by semantic analysis */
        return if(ctx.operations.isEmpty()) visitTerm(ctx.term) else Type.Boolean
    }

    fun visitTerm(ctx: TermContext): Type {
        /* SubAddContext operations do not change type,
           might check operators type but that might be a semantic analysis task */
        return visitFactor(ctx.factor)
    }

    fun visitFactor(ctx: FactorContext): Type {
        /* MulDivContext operations do not change type,
           might check operators type but that might be a semantic analysis task */
        return visitUnary(ctx.unary)
    }

    fun visitUnary(ctx: UnaryContext): Type {
        return when(ctx) {
            is UnaryContext.Primary -> visitPrimary(ctx.primary)
            is UnaryContext.Operation -> visitUnary(ctx.unary) // unary op does not change type
        }
    }

    fun visitPrimary(ctx: PrimaryContext): Type {
        return when(ctx) {
            is PrimaryContext.Expression -> visitExpression(ctx.expression)
            is PrimaryContext.SymbolPri -> visitSymbolPri(ctx.symbolPriContext)
        }
    }

    fun visitSymbolPri(ctx: SymbolPriContext): Type {
        return when(ctx) {
            is SymbolPriContext.Literal -> visitLiteral(ctx.literal)
            is SymbolPriContext.Location -> visitLocation(ctx.location)
            is SymbolPriContext.MethodCall -> visitMethodCall(ctx.methodCall)
        }
    }

    fun visitLiteral(ctx: LiteralContext): Type = ctx.literal.type

    fun resolveVariableType(name: String): Type {
        return symbols.filter { it is Declaration.Variable && it.name == name && it.scope == scope }
            .ifEmpty {symbols.filter { it is Declaration.Variable && it.name == name && scope.contains(it.scope) } }
            .firstOrNull()?.type
            ?: Type.Nothing
    }

    private fun resolveVariableType(ctx: LocationContext, structContext: Declaration.Struct?): Type {
        val varName = when(ctx) {
            is LocationContext.Simple -> ctx.id
            is LocationContext.Array -> ctx.arrayLocation.id
        }

        val varType = if(structContext == null) {
            when(ctx) {
                is LocationContext.Simple -> resolveVariableType(ctx.id)
                is LocationContext.Array -> visitLocationArray(ctx.arrayLocation)
            }
        } else {
            structContext.properties.firstOrNull { it.name == varName }
                ?.type
                ?: Type.Nothing
        }

        val pureType = when(varType) {
            is Type.Array -> varType.type
            is Type.ArrayUnknownSize -> varType.type
            else -> varType
        }

        return when {
            pureType is Type.Struct && ctx.subLocation != null -> {
                val struct = types.firstOrNull { it.type == pureType }
                resolveVariableType(ctx.subLocation!!.location, struct)
            }
            ctx.subLocation != null -> resolveVariableType(ctx.subLocation!!.location, null)
            else -> pureType
        }
    }
}