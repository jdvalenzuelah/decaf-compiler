package com.github.dcc.compiler.resolvers

import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.symbols.*
import com.github.dcc.decaf.types.Type
import com.github.dcc.parser.*

/*
    Resolve Symbol (declared vars and methods) Table from a DecafParser.ProgramContext
*/
internal class DeclarationResolver(
    private val typeResolver: StaticTypeResolver,
    private var currentScope: Scope = Scope.Global
) : DecafBaseVisitor<Declaration>() {

    override fun visitVar_decl(ctx: DecafParser.Var_declContext?): Declaration.Variable {
        return ctx?.array_decl()?.let(::visitArray_decl)
            ?: ctx?.prop_decl()!!.let(::visitProp_decl)
    }

    override fun visitProp_decl(ctx: DecafParser.Prop_declContext?): Declaration.Variable {
        return Declaration.Variable(
            name = ctx!!.ID()!!.text,
            scope = currentScope,
            type = typeResolver.visitProp_decl(ctx)!!,
            context = ctx,
        )
    }

    override fun visitArray_decl(ctx: DecafParser.Array_declContext?): Declaration.Variable {
        return  Declaration.Variable(
            name = ctx!!.ID()!!.text,
            scope = currentScope,
            type = typeResolver.visitArray_decl(ctx)!!,
            context = ctx,
        )
    }

    override fun visitMethod_decl(ctx: DecafParser.Method_declContext): Declaration.Method {
        return visitMethod_sign(ctx.method_sign())
    }

    override fun visitMethod_sign(ctx: DecafParser.Method_signContext?): Declaration.Method {
        return Declaration.Method(
            name = ctx!!.ID()!!.text,
            scope = currentScope,
            type = typeResolver.visitMethod_sign(ctx)!!,
            parameters = ctx.parameter().map(::visitParameter),
            context = ctx,
        )
    }

    override fun visitParameter(ctx: DecafParser.ParameterContext?): Declaration.Variable {
        return ctx?.array_param()?.let(::visitArray_param)
            ?: visitSimple_param(ctx?.simple_param())
    }

    override fun visitSimple_param(ctx: DecafParser.Simple_paramContext?): Declaration.Variable {
        return Declaration.Variable(
            name = ctx!!.ID().text,
            scope = currentScope,
            type = typeResolver.visitSimple_param(ctx),
            context = ctx,
        )
    }

    override fun visitArray_param(ctx: DecafParser.Array_paramContext?): Declaration.Variable {
        return Declaration.Variable(
            name = ctx!!.ID().text,
            scope = currentScope,
            type = typeResolver.visitArray_param(ctx),
            context = ctx,
        )
    }

    override fun visitStruct_decl(ctx: DecafParser.Struct_declContext?): Declaration.Struct {
        return Declaration.Struct(
            name = ctx!!.ID().text,
            type = Type.Struct(ctx.ID().text),
            scope = currentScope,
            context = ctx,
            properties = ctx.var_decl().map(::visitVar_decl)
        )
    }

}