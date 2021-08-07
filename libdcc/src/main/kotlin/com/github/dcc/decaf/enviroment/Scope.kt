package com.github.dcc.decaf.enviroment

sealed class Scope(
    open val parent: Scope
) {
    object Global : Scope(Global)

    data class Local(
        val name: String,
        override val parent: Scope = Global
    ): Scope(parent)

}


fun Scope.child(name: String): Scope = Scope.Local(name, this)