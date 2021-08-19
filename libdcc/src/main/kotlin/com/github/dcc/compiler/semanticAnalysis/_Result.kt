package com.github.dcc.compiler.semanticAnalysis

import com.github.dcc.compiler.Error
import com.github.dcc.compiler.context.Context
import com.github.validation.Validated

fun Context.semanticError(message: String) = Validated.Invalid(
    Error.SemanticError(message, this)
)