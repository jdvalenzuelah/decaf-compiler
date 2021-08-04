package com.github.dcc.parser

import com.github.dcc.decaf.types.Type

fun DecafParser.Var_typeContext.resolveType(): Type {
    return when {
        INT() != null -> Type.Int
        CHAR() != null -> Type.Char
        BOOLEAN() != null -> Type.Boolean
        VOID() != null -> Type.Void
        STRUCT() != null -> Type.Struct(ID().text)
        else -> Type.Any
    }
}

fun DecafParser.Method_typeContext.resolveType(): Type {
    return when {
        INT() != null -> Type.Int
        CHAR() != null -> Type.Char
        BOOLEAN() != null -> Type.Boolean
        VOID() != null -> Type.Void
        else -> Type.Any
    }
}

fun DecafParser.StatementContext.resolveType(): Type {
    return when {
        if_expr() != null -> if_expr().resolveType()
        while_expr() != null -> while_expr().resolveType()
        return_expr() != null -> return_expr().resolveType()
        method_call() != null -> method_call().resolveType()
        block() != null -> method_call().resolveType()
        assignment() != null -> assignment().resolveType()
        expression() != null -> expression().resolveType()
        else -> Type.Any
    }
}

fun DecafParser.BlockContext.resolveType(): Type  {
    val statements = statement()

    if(statements.isEmpty())
        return Type.Void

    val resolvedTypes = statements.map { it.resolveType() }
        .filterNot { it is Type.Void || it is Type.Any }

    if(resolvedTypes.isEmpty())
        return Type.Void

    //TODO: Complete it
    return Type.Any
}

//TODO: Expand
fun DecafParser.ExpressionContext.resolveType(): Type = Type.Any //will need symbol table

//TODO: Expand
fun DecafParser.EqualityContext.resolveType(): Type = Type.Any //will need symbol table and define ops

//TODO: Expand
fun DecafParser.LocationContext.resolveType(): Type = Type.Void //always void

fun DecafParser.LiteralContext.resolveType(): Type {
    return when {
        BOOL_LITERAL() != null -> Type.Boolean
        INT_LITERAL() != null -> Type.Int
        CHAR_LITERAL() != null -> Type.Char
        else -> Type.Any
    }
}

fun DecafParser.Var_declContext.resolveType(): Type = array_decl()?.resolveType() ?: prop_decl().resolveType()

fun DecafParser.Method_declContext.resolveType(): Type = method_type().resolveType()

fun DecafParser.Array_declContext.resolveType(): Type = var_type().resolveType()

fun DecafParser.Prop_declContext.resolveType(): Type = var_type().resolveType()

fun DecafParser.If_exprContext.resolveType(): Type = Type.Void // always void?

fun DecafParser.While_exprContext.resolveType(): Type = Type.Void // always void

fun DecafParser.Return_exprContext.resolveType(): Type = Type.Any //will need symbol table

fun DecafParser.Method_callContext.resolveType(): Type = Type.Any //will need symbol table

fun DecafParser.AssignmentContext.resolveType(): Type = Type.Void // always void