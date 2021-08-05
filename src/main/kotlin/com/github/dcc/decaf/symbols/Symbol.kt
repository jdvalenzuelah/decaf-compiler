package com.github.dcc.decaf.symbols

import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.types.Type
import java.util.*

/*
 Supported symbols by decaf spec
*/
sealed class Symbol(
    open val name: String,
    open val scope: Scope,
    open val type: Type
) {

    val id by lazy { UUID.randomUUID().toString() }

    data class Variable(
        override val name: String,
        override val scope: Scope,
        override val type: Type,
    ) : Symbol(name, scope, type)

    data class Method(
        override val name: String,
        override val scope: Scope,
        override val type: Type,
        val signature: String //TODO: Define signature
    ) : Symbol(name, scope, type)

}
