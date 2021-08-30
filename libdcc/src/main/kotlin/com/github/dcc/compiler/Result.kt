package com.github.dcc.compiler

import com.github.dcc.compiler.context.Context

sealed class Result

sealed class Error(
    open val context: Context?
) : Result() {

    data class SyntaxError(
        val message: String
    ): Error(null)

    data class SemanticError(
        val message: String,
        override val context: Context
    ): Error(context)

}