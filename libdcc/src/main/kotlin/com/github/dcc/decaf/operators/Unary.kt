package com.github.dcc.decaf.operators

enum class Unary(override val op: String): Operator {

    SUB("-"),
    EXCL("!");

    companion object {
        fun valueOfOrNull(op: String): Unary? = values().firstOrNull { it.op == op }
    }

}