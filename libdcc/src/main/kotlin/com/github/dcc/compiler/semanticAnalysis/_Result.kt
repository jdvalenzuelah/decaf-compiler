package com.github.dcc.compiler.semanticAnalysis

import com.github.dcc.compiler.Error
import com.github.dcc.decaf.symbols.Declaration
import com.github.validation.Validated
import com.github.validation.zip
import org.antlr.v4.runtime.ParserRuleContext

fun semanticError(msg: String, ctx: ParserRuleContext): Error.SemanticError = Error.SemanticError(msg, ctx)

fun check(test: Boolean, error: () -> Error.SemanticError) = if(test) Validated.Valid else Validated.Invalid(error())

fun require(test: Boolean, error: () -> Validated<Error>): Validated<Error> = if(test) Validated.Valid else error()

fun <T> Iterable<T>.semanticError(msg: (T) -> String):  Validated<Error.SemanticError> where T : Declaration = map {
    Validated.Invalid(Error.SemanticError(msg(it), it.context))
}.zip()