package com.github.dcc.compiler

import com.github.dcc.compiler.resolvers.ProgramContextResolver
import com.github.dcc.compiler.semanticAnalysis.SemanticAnalysis
import com.github.dcc.parser.DecafLexer
import com.github.dcc.parser.DecafParser
import com.github.validation.Validated
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import java.io.InputStream

typealias CompilationResult = Validated<Error>

class CompilerContext(
    val input: InputStream,
) {

    constructor(file: File) : this(file.inputStream())
    constructor(code: String): this(code.byteInputStream())

    val inputStream = ANTLRInputStream(input)
    val tokenStream = CommonTokenStream(DecafLexer(inputStream))

    val parser: DecafParser get() {
        tokenStream.reset()
        return DecafParser(tokenStream)
    }

    val programContext by lazy {
        ProgramContextResolver.resolve(this)
    }

    fun reset(): Unit = tokenStream.reset()



    fun compileSource(): CompilationResult {
        return SemanticAnalysis(programContext)
    }

}
