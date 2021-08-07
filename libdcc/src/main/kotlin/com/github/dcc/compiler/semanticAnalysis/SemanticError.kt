package com.github.dcc.compiler.semanticAnalysis

import org.antlr.v4.runtime.ParserRuleContext

data class SemanticError(
    val message: String,
    val context: ParserRuleContext,
    val errorStartChar: Int,
)