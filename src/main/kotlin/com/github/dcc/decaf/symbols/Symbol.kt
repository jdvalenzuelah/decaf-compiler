package com.github.dcc.decaf.symbols

import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.types.Type
import com.github.dcc.parser.SourceLocation
import java.util.*

/*
 Supported symbols by decaf spec
*/
sealed class Symbol(
    open val name: String,
    open val scope: Scope,
    open val type: Type,
    open val location: SourceLocation,
) {

    val id by lazy { UUID.randomUUID().toString() }

    data class Variable(
        override val name: String,
        override val scope: Scope,
        override val type: Type,
        override val location: SourceLocation,
    ) : Symbol(name, scope, type, location)

    data class Method(
        override val name: String,
        override val scope: Scope,
        override val type: Type,
        override val location: SourceLocation,
        val parameters: List<Symbol>,
    ) : Symbol(name, scope, type, location)

}
