package com.github.dcc.decaf.types

import org.antlr.v4.runtime.ParserRuleContext

sealed class Type {

    object Nothing : Type()
    object Void : Type()
    object Boolean : Type()
    object Int : Type()
    object Char : Type()

    data class Struct(
        val name: String,
    ): Type() {
        override fun toString(): String = name
    }

    data class Array(
        val size: kotlin.Int,
        val type: Type,
    ): Type() {
        override fun toString(): String = "${type}[$size]"
    }

    data class ArrayUnknownSize(
        val type: Type,
    ): Type() {
        override fun toString(): String = "${type}[]"
    }

    override fun toString(): String = this::class.simpleName ?: super.toString()

}
