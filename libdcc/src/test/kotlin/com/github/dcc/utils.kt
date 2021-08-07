package com.github.dcc

import com.github.dcc.parser.DecafLexer
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream

const val EOF = "EOF"

fun Any.getResourceAsStream(file: String) = this::class.java.getResourceAsStream(file)

fun Any.getResourceAsANTLRInputStream(file: String) = ANTLRInputStream(getResourceAsStream(file))

fun Any.getResourceAsDecafLexer(file: String) =
    DecafLexer(getResourceAsANTLRInputStream(file))

fun Any.getResourceAsDecafTokenStream(file: String) = CommonTokenStream(getResourceAsDecafLexer(file))

fun DecafLexer.tokens(): List<String> = allTokens.map {
    if(it.type == -1)
        EOF
    else vocabulary.getSymbolicName(it.type)
}