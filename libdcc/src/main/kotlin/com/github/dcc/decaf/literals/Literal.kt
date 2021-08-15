package com.github.dcc.decaf.literals

sealed class Literal {
    data class Boolean(val value: kotlin.Boolean): Literal()
    data class Int(val value: kotlin.Int): Literal()
    data class Char(val value: kotlin.String): Literal()
}
