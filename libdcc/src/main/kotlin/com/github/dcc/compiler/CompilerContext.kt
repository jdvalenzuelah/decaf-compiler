package com.github.dcc.compiler

import com.github.dcc.parser.DecafParser
import org.antlr.v4.runtime.BufferedTokenStream

data class CompilerContext(
    val tokenStream: BufferedTokenStream,
) {
    val parser: DecafParser get() {
        tokenStream.reset()
        return DecafParser(tokenStream)
    }
}
