package com.github.dcc.compiler.semanticAnalysis

import com.github.dcc.compiler.Error
import com.github.dcc.compiler.context.Context
import com.github.validation.Validated
import com.github.validation.Validation

typealias SemanticRule = Validation<Context.ProgramContext, Error.SemanticError>

fun Context.semanticError(message: String) = Validated.Invalid(
    Error.SemanticError(message, this)
)