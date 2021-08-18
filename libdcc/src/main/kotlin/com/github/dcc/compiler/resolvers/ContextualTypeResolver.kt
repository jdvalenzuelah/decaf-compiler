package com.github.dcc.compiler.resolvers

import com.github.dcc.compiler.context.Context
import com.github.dcc.compiler.context.Context.*
import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.symbols.Signature
import com.github.dcc.decaf.symbols.signature
import com.github.dcc.decaf.types.Type

/* pseudo visitor */
internal class ContextualTypeResolver(
    symbols: List<Declaration>,
    val types: List<Declaration.Struct>,
    val scope: Scope,
) {

    val symbols: List<Declaration> = symbols.filter { it.scope == scope || it.scope is Scope.Global }

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

    fun visitMethodCall(ctx: MethodCallContext): Type {
        val callSignature = Signature(
            name = ctx.name,
            parameters = ctx.args.map(::visitArgument)
        )

        return resolveMethodBySignature(callSignature).type
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
        println(ctx)
        return Type.Nothing
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

    private fun resolveMethodBySignature(signature: Signature): Declaration.Method {
        return symbols.first { it is Declaration.Method && it.signature() == signature } as Declaration.Method
    }
}