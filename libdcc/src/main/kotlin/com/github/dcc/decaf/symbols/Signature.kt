package com.github.dcc.decaf.symbols

import com.github.dcc.decaf.types.Type

data class Signature(
    val name: String,
    val parameters: List<Type>
)


fun Declaration.Method.signature(): Signature = Signature(
    name = name,
    parameters = parameters.map { it.type }
)