package com.github.dcc.decaf.symbols

import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.types.Type
import org.antlr.v4.runtime.ParserRuleContext

sealed class Declaration(
    open val name: String,
    open val type: Type,
    open val context: ParserRuleContext, //TODO: Remove
) {

    data class Variable(
        override val name: String,
        override val type: Type,
        val scope: Scope,
        override val context: ParserRuleContext,
    ) : Declaration(name, type, context)

    data class Method(
        override val name: String,
        override val type: Type,
        val parameters: List<Parameter>,
        override val context: ParserRuleContext,
    ) : Declaration(name, type, context) {

        data class Signature(
            val name: String,
            val parametersType: List<Type>
        )

        val signature: Signature
        get() = Signature(
            name = name,
            parametersType = parameters.map { it.type }
        )

    }

    data class Struct(
        override val name: String,
        override val type: Type,
        override val context: ParserRuleContext,
        val properties: List<Parameter>,
    ) : Declaration(name, type, context)

    data class Parameter(
        override val name: String,
        override val type: Type,
        override val context: ParserRuleContext,
    ) : Declaration(name, type, context)

}
