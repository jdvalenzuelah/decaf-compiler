package com.github.dcc.compiler

import com.github.dcc.compiler.backend.Dumpable
import com.github.dcc.compiler.backend.Target
import com.github.dcc.compiler.backend.codegen.jasmin.JasminGenerator
import com.github.dcc.compiler.ir.Program
import com.github.dcc.compiler.ir.ProgramTransform
import com.github.dcc.compiler.semanticAnalysis.SemanticAnalysis
import com.github.dcc.compiler.symbols.ProgramSymbols
import com.github.dcc.compiler.syntaxAnalysis.SyntaxErrorListener
import com.github.dcc.parser.DecafLexer
import com.github.dcc.parser.DecafParser
import com.github.validation.Validated
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import java.io.InputStream

class Compiler(
    input: InputStream,
    private val target: Target,
) {

    constructor(file: File, target: Target = Target.JASMIN) : this(file.inputStream(), target)
    constructor(code: String, target: Target = Target.JASMIN): this(code.byteInputStream(), target)

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

    fun reset(): Unit = tokenStream.reset()

    val symbols by lazy {
        ProgramSymbols.of(parser.program())
    }


    sealed class CompilationResult {
        data class SyntaxError(val errors: Iterable<Validated.Invalid<Error>>) : CompilationResult()

        data class SemanticError(val errors: Iterable<Validated.Invalid<Error>>): CompilationResult()

        data class Success(
            val compiledSource: Dumpable,
            val ir: Program,
        ) : CompilationResult()

    }

    private fun compile(ir: Program): Dumpable {
        return when(target) {
            Target.JASMIN -> JasminGenerator.compile(ir)
            Target.JAVA8 -> JasminGenerator.compile(ir)
        }
    }

    fun compileSource(): CompilationResult {
        val syntaxErrors = syntaxErrorListener.errors()

        if(syntaxErrors is Validated.Invalid) {
            return CompilationResult.SyntaxError(syntaxErrors)
        }

        val semanticAnalysis = SemanticAnalysis.invoke(symbols, parser.program())

        if(semanticAnalysis is Validated.Invalid) {
            return CompilationResult.SemanticError(semanticAnalysis)
        }

        symbols.symbolTable.resetScope()
        val ir = ProgramTransform(
            parser.program(),
            symbols,
        )

        val compiled = compile(ir)

        return CompilationResult.Success(compiled, ir)
    }

}