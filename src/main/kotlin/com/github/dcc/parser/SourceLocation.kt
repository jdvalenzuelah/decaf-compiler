package com.github.dcc.parser

import org.antlr.v4.runtime.Token

data class SourceLocation(
    val start: Token,
    val stop: Token,
)
