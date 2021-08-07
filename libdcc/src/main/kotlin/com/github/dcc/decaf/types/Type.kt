package com.github.dcc.decaf.types

import org.antlr.v4.runtime.ParserRuleContext

sealed class Type {

    object Void : Type()
    object Boolean : Type()
    object Int : Type()
    object Char : Type()

    data class Struct(
        val name: String,
    ): Type()

    data class Array(
        val size: kotlin.Int,
        val type: Type,
        val context: ParserRuleContext,
    ): Type() {
    }

    data class ArrayUnknownSize(
        val type: Type,
        val context: ParserRuleContext,
    ): Type()

}
