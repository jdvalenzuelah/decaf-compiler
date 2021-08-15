package com.github.dcc.decaf.symbols

import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.types.Type
import org.antlr.v4.runtime.ParserRuleContext

sealed class Declaration(
    open val name: String,
    open val type: Type,
    open val scope: Scope,
    open val context: ParserRuleContext, //TODO: Remove
) {

    data class Variable(
        override val name: String,
        override val type: Type,
        override val scope: Scope,
        override val context: ParserRuleContext,
    ) : Declaration(name, type, scope, context)

    data class Method(
        override val name: String,
        override val type: Type,
        override val scope: Scope,
        val parameters: List<Variable>,
        override val context: ParserRuleContext,
    ) : Declaration(name, type, scope, context)

    data class Struct(
        override val name: String,
        override val type: Type,
        override val context: ParserRuleContext,
        override val scope: Scope,
        val properties: List<Variable>,
    ) : Declaration(name, type, scope, context)

}
