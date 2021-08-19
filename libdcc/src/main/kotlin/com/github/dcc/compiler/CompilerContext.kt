package com.github.dcc.compiler

import com.github.dcc.parser.DecafLexer
import com.github.dcc.parser.DecafParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

class CompilerContext(
    val file: File,
) {

    val inputStream = ANTLRInputStream(file.inputStream())
    val tokenStream = CommonTokenStream(DecafLexer(inputStream))

    val parser: DecafParser get() {
        tokenStream.reset()
        return DecafParser(tokenStream)
    }

    fun reset(): Unit = tokenStream.reset()
}
