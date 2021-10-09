package com.github.dcc.compiler.symbols.types

import com.github.dcc.compiler.resolvers.StaticTypeResolver
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.symbols.TypeStore
import com.github.dcc.decaf.types.Type
import com.github.dcc.parser.DecafBaseVisitor
import com.github.dcc.parser.DecafParser
import org.tinylog.kotlin.Logger

internal class ParameterResolver : DecafBaseVisitor<Declaration>() {

    private val typeResolver = StaticTypeResolver()

    override fun visitVar_decl(ctx: DecafParser.Var_declContext): Declaration.Parameter {
        return ctx.array_decl()?.let(::visitArray_decl) ?: ctx.prop_decl()!!.let(::visitProp_decl)
    }

    override fun visitProp_decl(ctx: DecafParser.Prop_declContext): Declaration.Parameter {
        return Declaration.Parameter(
            name = ctx.ID().text,
            type = typeResolver.visitProp_decl(ctx) ?: Type.Nothing,
            context = ctx
        )
    }

    override fun visitArray_decl(ctx: DecafParser.Array_declContext): Declaration.Parameter {
        return Declaration.Parameter(
            name = ctx.ID().text,
            type = typeResolver.visitArray_decl(ctx) ?: Type.Nothing,
            context = ctx
        )
    }

    override fun visitParameter(ctx: DecafParser.ParameterContext): Declaration.Parameter {
        return ctx.simple_param()?.let(::visitSimple_param)
            ?: ctx.array_param()!!.let(::visitArray_param)
    }

    override fun visitSimple_param(ctx: DecafParser.Simple_paramContext): Declaration.Parameter {
        return Declaration.Parameter(
            name = ctx.ID().text,
            type = typeResolver.visitSimple_param(ctx),
            context = ctx
        )
    }

    override fun visitArray_param(ctx: DecafParser.Array_paramContext): Declaration.Parameter {
        return Declaration.Parameter(
            name = ctx.ID().text,
            type = typeResolver.visitArray_param(ctx),
            context = ctx
        )
    }
}


class TypeStoreBuilder private constructor() : DecafBaseVisitor<Unit>() {

    private val types = mutableListOf<Declaration.Struct>()
    private val declarationResolver = ParameterResolver()


    companion object {
        operator fun invoke(program: DecafParser.ProgramContext): TypeStore {
            return TypeStoreBuilder()
                .apply {
                    program.struct_decl().forEach(::visitStruct_decl)
                }
                .types
        }
    }

    override fun visitStruct_decl(ctx: DecafParser.Struct_declContext) {
        Logger.info("Visiting struct decl: resolving struct")
        val name = ctx.ID().text
        val type = Declaration.Struct(
            name = name,
            context = ctx,
            type = Type.Struct(name),
            properties = ctx.var_decl().map(declarationResolver::visitVar_decl)
        )
        Logger.info("Resolved struct $type")
        types.add(type)
    }

}