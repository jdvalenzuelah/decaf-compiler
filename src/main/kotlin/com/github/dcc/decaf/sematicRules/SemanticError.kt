package com.github.dcc.decaf.sematicRules

import com.github.dcc.parser.SourceLocation

data class SemanticError(
    val message: String,
    val sourceLocation: SourceLocation?
)