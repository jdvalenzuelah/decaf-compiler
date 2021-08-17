package com.github.dcc.decaf.enviroment

import java.util.*

sealed class Scope(
    open val parent: Scope
) {
    object Global : Scope(Global) {
        override fun toString(): String = this::class.simpleName ?: super.toString()
    }

    data class Local(
        val name: String,
        override val parent: Scope = Global
    ): Scope(parent) {
        override fun toString(): String = name
    }

}

fun Scope.lineage(): LinkedList<Scope> =
    if(this == Scope.Global)
        LinkedList<Scope>().apply { addFirst(this@lineage) }
    else {
        parent.lineage()
            .apply { addLast(this@lineage) }
    }

fun Scope.lineageAsString(): String = this.lineage().joinToString(separator = "@") { it.toString() }

fun Scope.child(name: String): Scope = Scope.Local(name, this)