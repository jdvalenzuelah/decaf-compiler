package com.github.dcc.compiler.resolvers

import com.github.dcc.compiler.context.Context
import com.github.dcc.compiler.context.Context.*
import com.github.dcc.decaf.enviroment.Scope
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

    //TODO: extract this
    fun getMethodCallSignature(ctx: MethodCallContext): Signature = Signature(
        name = ctx.name,
        parameters = ctx.args.map(::visitArgument)
    )

    fun visitMethodCall(ctx: MethodCallContext): Type {
        val callSignature = getMethodCallSignature(ctx)

        return symbols.findBySignatureOrNull(callSignature)?.type ?: Type.Nothing
    }

    fun visitArgument(ctx: ArgContext): Type = visitExpression(ctx.expression)

    fun visitLocation(ctx: LocationContext): Type {
        val (varType, varName) = when(ctx) {
            is LocationContext.Simple -> resolveVariableType(ctx.id) to ctx.id
            is LocationContext.Array -> visitLocationArray(ctx.arrayLocation) to ctx.arrayLocation.id
        }

        return if(varType is Type.Struct && ctx.subLocation != null)
            resolveSubLocationType(varName, ctx.subLocation!!)
        else
            varType
    }

    fun visitLocationArray(ctx: LocationArrayContext): Type  = resolveVariableType(ctx.id)

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
        }
    }

    fun visitLiteral(ctx: LiteralContext): Type = ctx.literal.type

    private fun resolveVariableType(name: String): Type {
        return symbols.firstOrNull { it is Declaration.Variable && it.name == name }
            ?.type
            ?: Type.Nothing
    }

    private fun resolveSubLocationType(parent: String, subProp: SubLocationContext): Type {
        val parentType = resolveVariableType(parent)

        val varName = when(subProp.location) {
            is LocationContext.Simple -> subProp.location.id
            is LocationContext.Array -> subProp.location.arrayLocation.id
        }
        val sub = subProp.location.subLocation

         return when {
             parentType is Type.Struct &&  sub == null -> {
                 val struct = types.first { it.name == parentType.name }
                 struct.properties.first { it.name == varName }.type
             }
             parentType is Type.Struct && sub != null -> resolveSubLocationType(varName, sub)
             else -> parentType
         }
    }
}