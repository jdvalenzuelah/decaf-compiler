package com.github.dcc.compiler.resolvers

import com.github.dcc.decaf.symbols.Symbol
import com.github.dcc.decaf.types.Type
import com.github.dcc.parser.DecafBaseVisitor
import com.github.dcc.parser.DecafParser

/*
    Resolve a decaf type from a DecafParserContext without info from symbol/type table
*/
class StaticTypeResolver : DecafBaseVisitor<Type>() {

    override fun visitVar_decl(ctx: DecafParser.Var_declContext?): Type? {
        return ctx?.array_decl()?.let(::visitArray_decl)
            ?: ctx?.prop_decl()?.let(::visitProp_decl)
    }

    override fun visitMethod_decl(ctx: DecafParser.Method_declContext?): Type? {
        return visitMethod_type(ctx?.method_type())
    }

    override fun visitProp_decl(ctx: DecafParser.Prop_declContext?): Type? {
        return visitVar_type(ctx?.var_type())
    }

    override fun visitArray_decl(ctx: DecafParser.Array_declContext?): Type? {
        return Type.Array(
            size = ctx!!.INT_LITERAL().text.toInt(),
            type = visitVar_type(ctx.var_type())!!
        )
    }

    override fun visitLiteral(ctx: DecafParser.LiteralContext?): Type? {
        return when {
            ctx?.BOOL_LITERAL() != null -> Type.Boolean
            ctx?.INT_LITERAL() != null -> Type.Int
            ctx?.CHAR_LITERAL() != null -> Type.Char
            else -> null
        }
    }

    override fun visitMethod_type(ctx: DecafParser.Method_typeContext?): Type? {
        return when {
            ctx?.INT() != null -> Type.Int
            ctx?.CHAR() != null -> Type.Char
            ctx?.BOOLEAN() != null -> Type.Boolean
            ctx?.VOID() != null -> Type.Void
            else -> null
        }
    }

    override fun visitVar_type(ctx: DecafParser.Var_typeContext?): Type? {
        return when {
            ctx?.INT() != null -> Type.Int
            ctx?.CHAR() != null -> Type.Char
            ctx?.BOOLEAN() != null -> Type.Boolean
            ctx?.VOID() != null -> Type.Void
            ctx?.STRUCT() != null -> Type.Struct(ctx.ID().text)
            else -> null
        }
    }

    override fun visitParameter(ctx: DecafParser.ParameterContext?): Type {
        return ctx?.simple_param()?.let(::visitSimple_param)
            ?: ctx?.array_param()!!.let(::visitArray_param)
    }

    override fun visitSimple_param(ctx: DecafParser.Simple_paramContext?): Type {
        return visitParameter_type(ctx?.parameter_type())!!
    }

    override fun visitArray_param(ctx: DecafParser.Array_paramContext?): Type {
        return Type.ArrayUnknownSize(visitParameter_type(ctx?.parameter_type())!!)
    }

    override fun visitParameter_type(ctx: DecafParser.Parameter_typeContext?): Type? {
        return when {
            ctx?.INT() != null -> Type.Int
            ctx?.CHAR() != null -> Type.Char
            ctx?.BOOLEAN() != null -> Type.Boolean
            else -> null
        }
    }

    override fun visitStruct_decl(ctx: DecafParser.Struct_declContext?): Type {
        return Type.Struct(name = ctx!!.ID().text)
    }

}