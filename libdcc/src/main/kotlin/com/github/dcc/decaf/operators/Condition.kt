package com.github.dcc.decaf.operators

enum class Condition(override val op: String): Operator {

    AND("&&"),
    OR("||");

    companion object {
        fun valueOfOrNull(op: String): Condition? = values().firstOrNull { it.op == op }
    }
}