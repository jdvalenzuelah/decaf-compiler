package com.github.dcc.compiler.resolvers

import com.github.dcc.compiler.symbols.variables.SymbolTable
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.symbols.MethodStore
import com.github.dcc.decaf.symbols.TypeStore
import com.github.dcc.decaf.types.Type
import com.github.dcc.parser.DecafBaseVisitor
import com.github.dcc.parser.DecafParser

internal class LocationTypeResolver(
    private val symbols: SymbolTable,
    private val structs: TypeStore,
    private val structContext: Type.Struct? = null,
): DecafBaseVisitor<Type>() {

    override fun visitLocation(ctx: DecafParser.LocationContext): Type {
        val locationType = visitVar_location(ctx.var_location())
        return if(ctx.sub_location() != null) {
            if(locationType is Type.Struct) {
                LocationTypeResolver(symbols, structs, locationType)
                    .visitLocation(ctx.sub_location().location())
            } else Type.Nothing
        } else locationType
    }

    override fun visitVar_location(ctx: DecafParser.Var_locationContext): Type {
        return when {
            ctx.location_array() != null -> visitLocation_array(ctx.location_array())
            ctx.ID() != null -> {
                if(structContext == null)
                    symbols.symbolBottomToTop(ctx.ID().text)?.type ?: Type.Nothing
                else {
                    structs.firstOrNull { it.name == structContext.name }
                        ?.properties?.firstOrNull { it.name == ctx.ID().text }?.type
                        ?: Type.Nothing
                }
            }
            else -> Type.Nothing
        }
    }

    override fun visitLocation_array(ctx: DecafParser.Location_arrayContext): Type {
        val location = if(structContext == null)
            symbols.symbolBottomToTop(ctx.ID().text)?.type ?: Type.Nothing
        else {
            structs.firstOrNull { it.name == structContext.name }
                ?.properties?.firstOrNull { it.name == ctx.ID().text }?.type
                ?: Type.Nothing
        }
        return if(location is Type.Array) location.type else location
    }

}

class ContextualTypeResolver(
    private val symbols: SymbolTable,
    private val methods: MethodStore,
    private val structs: TypeStore,
): DecafBaseVisitor<Type>() {

    private val typeResolver = StaticTypeResolver()

    override fun visitExpression(ctx: DecafParser.ExpressionContext): Type {
        return when {
            ctx.method_call() != null -> visitMethod_call(ctx.method_call())
            ctx.location() != null -> visitLocation(ctx.location())
            ctx.literal() != null -> visitLiteral(ctx.literal())
            ctx.arith_op_sub() != null || ctx.arith_op_mul() != null -> Type.Int
            ctx.rel_op() != null || ctx.cond_op() != null || ctx.eq_op() != null -> Type.Boolean
            else -> visitExpression(ctx.expression().first())
        }
    }

    override fun visitMethod_call(ctx: DecafParser.Method_callContext): Type {
        val callSignature = Declaration.Method.Signature(
            name = ctx.ID().text,
            parametersType = ctx.arg().map { visitExpression(it.expression()).noSize() }
        )

        return methods.firstOrNull { it.signature == callSignature }
            ?.type ?: Type.Nothing
    }

    override fun visitLocation(ctx: DecafParser.LocationContext): Type {
        return LocationTypeResolver(symbols, structs, null)
            .visitLocation(ctx)
    }

    override fun visitLocation_array(ctx: DecafParser.Location_arrayContext): Type {
        return LocationTypeResolver(symbols, structs, null)
            .visitLocation_array(ctx)
    }

    override fun visitLiteral(ctx: DecafParser.LiteralContext): Type {
        return typeResolver.visitLiteral(ctx) ?: Type.Nothing
    }

    override fun visitArg(ctx: DecafParser.ArgContext): Type {
        return visitExpression(ctx.expression())
    }

}