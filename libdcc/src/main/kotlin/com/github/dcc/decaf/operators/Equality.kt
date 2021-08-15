package com.github.dcc.decaf.operators

enum class Equality(override val op: String): Operator {
    NOT_EQUAL("!="),
    EQUAL_TO("==");

    companion object {
        fun valueOfOrNull(op: String): Equality? = values().firstOrNull { it.op == op }
    }
}