package com.github.dcc.decaf.literals

import com.github.dcc.decaf.types.Type

sealed class Literal(val type: Type) {
    data class Boolean(val value: kotlin.Boolean): Literal(Type.Boolean)
    data class Int(val value: kotlin.Int): Literal(Type.Int)
    data class Char(val value: kotlin.String): Literal(Type.Char)
}
