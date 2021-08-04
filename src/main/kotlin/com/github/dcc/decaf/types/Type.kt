package com.github.dcc.decaf.types

import com.github.dcc.decaf.symbols.Symbol

/*
 Types supported by decaf spec
*/
sealed class Type {

    override fun toString(): String = this::class.simpleName ?: super.toString()

    object Any : Type() // wildcard

    object Void : Type()

    object Int : Type()

    object Char : Type()

    object Boolean : Type()

    data class StructDecl(
        val name: String,
        val args: Collection<Symbol.Variable>
    ) : Type() {
        init { require(args.all { it.type !is StructDecl }) }
    }

    data class Struct(
        val name: String,
    ) : Type()

    data class Array(
        val size: kotlin.Int,
        val type: Type
    ): Type() {
        init { require(type !is StructDecl) }
    }

    data class ArrayUnknownSize(
        val type: Type,
    ) : Type() {
        init { require(type !is StructDecl) }
    }
}
