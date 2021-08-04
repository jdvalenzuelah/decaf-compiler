package com.github.dcc.decaf.enviroment

/*
 Supported envs defined in decaf spec
*/
sealed class Scope {
    object Global : Scope()

    data class Local(
        val id: String //TODO: Define how to identify local scopes
    ): Scope()
}
