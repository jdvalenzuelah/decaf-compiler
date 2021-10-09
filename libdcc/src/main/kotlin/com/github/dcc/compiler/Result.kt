package com.github.dcc.compiler

import org.antlr.v4.runtime.ParserRuleContext

sealed class Result

sealed class Error(
    open val context: ParserRuleContext?
) : Result() {

    data class SyntaxError(
        val message: String
    ): Error(null)

    data class SemanticError(
        val message: String,
        override val context: ParserRuleContext
    ): Error(null)

}