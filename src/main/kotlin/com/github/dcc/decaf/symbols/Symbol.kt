package com.github.dcc.decaf.symbols

import com.github.dcc.decaf.DecafElement
import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.types.Type

/*
 Supported symbols by decaf spec
*/
sealed class Symbol(
    val name: String,
    val scope: Scope,
    val type: Type
) : DecafElement() {

    class Variable(
        name: String,
        scope: Scope,
        type: Type,
    ) : Symbol(name, scope, type)

    class Method(
        name: String,
        scope: Scope,
        type: Type,
        val signature: String //TODO: Define signature
    ) : Symbol(name, scope, type)

    override fun toString(): String {
        return "Symbol(name=$name, type=$type)"
    }

}
