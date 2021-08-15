package com.github.dcc.decaf.operators

enum class Arithmetic(override val op: String) : Operator {

    SUB("-"),
    ADD("+"),
    MUl("*"),
    DIV("/");

    companion object {
        fun valueOfOrNull(op: String): Arithmetic? = values().firstOrNull { it.op == op }
    }

}