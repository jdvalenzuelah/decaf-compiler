package com.github.dcc.decaf.semanticRules

import com.github.dcc.compiler.semanticAnalysis.SemanticError
import com.github.dcc.decaf.symbols.Declaration
import com.github.validation.Validated.Valid
import com.github.validation.Validated.Invalid
import com.github.validation.Validation
import com.github.validation.zip

object GlobalScopeRules {

    private const val mainName = "main"

    private fun Collection<Declaration>.mains() : Collection<Declaration.Method> = mapNotNull {
        if(it is Declaration.Method && it.name == mainName) it else null
    }

    val containsJustOneMainMethod = Validation<Collection<Declaration>, SemanticError> { symbols ->
        val mainDefinitions = symbols.mains()
        when {
            mainDefinitions.isEmpty() -> Invalid(
                SemanticError("Program must contain one main function", symbols.first().context, symbols.first().context.start.charPositionInLine)
            )
            mainDefinitions.size > 1 -> mainDefinitions.map {
                Invalid(SemanticError("Program must contain exactly one main function", it.context, it.context.start.charPositionInLine))
            }.zip()
            else -> Valid
        }
    }

    val mainMethodHasNoParameters = Validation<Collection<Declaration>, SemanticError> { symbols ->
        val mainDefinitions = symbols.mains().filter { it.parameters.isNotEmpty() }

        when {
            mainDefinitions.isEmpty() -> Valid
            else -> mainDefinitions.map {
                val startIndex = it.parameters.minByOrNull {  p -> p.context.start.charPositionInLine }
                    ?.context?.start?.charPositionInLine ?: 0

                Invalid(SemanticError("main function must have no parameters", it.context, startIndex))
            }.zip()
        }

    }



}

