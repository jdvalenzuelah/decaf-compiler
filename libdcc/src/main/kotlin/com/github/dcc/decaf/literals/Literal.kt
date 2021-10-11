package com.github.dcc.decaf.literals

import com.github.dcc.decaf.types.Type

sealed class Literal(val type: Type) {
    data class Boolean(val value: kotlin.Boolean): Literal(Type.Boolean) {
        override fun toString(): String = value.toString()
    }
    data class Int(val value: kotlin.Int): Literal(Type.Int){
        override fun toString(): String = value.toString()
    }
    data class Char(val value: String): Literal(Type.Char){
        override fun toString(): String = value.toString()
    }
}
