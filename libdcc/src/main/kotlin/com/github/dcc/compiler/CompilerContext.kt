package com.github.dcc.compiler

import com.github.dcc.parser.DecafLexer
import com.github.dcc.parser.DecafParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.BufferedTokenStream
import java.io.File

class CompilerContext(
    val tokenStream: BufferedTokenStream,
) {

    constructor(file: File) : this(
        BufferedTokenStream(
            DecafLexer(
                ANTLRInputStream(
                    file.inputStream()
                )
            )
        )
    )

    val parser: DecafParser get() {
        tokenStream.reset()
        return DecafParser(tokenStream)
    }

    fun reset(): Unit = tokenStream.reset()
}
