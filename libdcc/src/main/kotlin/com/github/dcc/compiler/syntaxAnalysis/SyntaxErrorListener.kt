package com.github.dcc.compiler.syntaxAnalysis

import com.github.dcc.compiler.Error
import com.github.validation.Validated
import com.github.validation.zip
import org.antlr.v4.runtime.*

class SyntaxErrorListener : ANTLRErrorListener by BaseErrorListener() {

    private val errors = mutableListOf<Validated.Invalid<Error>>()

    internal fun errors(): Validated<Error> {
        return errors.zip()
    }

    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String?,
        e: RecognitionException?
    ) {
        val errMsg = "line $line:$charPositionInLine $msg"
        errors.add(Validated.Invalid(Error.SyntaxError(errMsg)))
    }
}