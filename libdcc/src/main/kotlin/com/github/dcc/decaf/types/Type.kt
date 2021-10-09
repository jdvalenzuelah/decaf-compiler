package com.github.dcc.decaf.types

sealed class Type(
    val isPrimitive: kotlin.Boolean = false
) {
    
    object Nothing : Type()
    object Void : Type()
    object Boolean : Type(true)
    object Int : Type(true)
    object Char : Type(true)

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

    fun noSize() = if(this is Array) ArrayUnknownSize(type) else this

}
