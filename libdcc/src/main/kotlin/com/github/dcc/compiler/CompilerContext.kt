package com.github.dcc.compiler

import com.github.dcc.compiler.resolvers.ProgramContextResolver
import com.github.dcc.compiler.semanticAnalysis.SemanticAnalysis
import com.github.dcc.compiler.syntaxAnalysis.SyntaxErrorListener
import com.github.dcc.parser.DecafLexer
import com.github.dcc.parser.DecafParser
import com.github.validation.Validated
import com.github.validation.then
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

    private val syntaxErrorListener = SyntaxErrorListener()

    private val inputStream = ANTLRInputStream(input)
    private val lexer = DecafLexer(inputStream).apply {
        removeErrorListeners()
        addErrorListener(syntaxErrorListener)
    }
    private val tokenStream = CommonTokenStream(lexer)

    val parser: DecafParser get() {
        tokenStream.reset()
        return DecafParser(tokenStream).apply {
            removeErrorListeners()
            addErrorListener(syntaxErrorListener)
        }
    }

    val programContext by lazy {
        ProgramContextResolver.resolve(this)
    }

    fun reset(): Unit = tokenStream.reset()



    fun compileSource(): CompilationResult {
        val syntaxErrors = syntaxErrorListener.errors()
        val semanticAnalysis = SemanticAnalysis(programContext)
        return  semanticAnalysis then syntaxErrors
    }

}
