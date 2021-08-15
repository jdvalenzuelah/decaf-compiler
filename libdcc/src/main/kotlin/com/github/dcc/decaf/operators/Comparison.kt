package com.github.dcc.decaf.operators

enum class Comparison(override val op: String): Operator {
    GT(">"),
    LT("<"),
    GTE(">="),
    LTE("<=");

    companion object {
        fun valueOfOrNull(op: String): Comparison? = values().firstOrNull { it.op == op }
    }
}