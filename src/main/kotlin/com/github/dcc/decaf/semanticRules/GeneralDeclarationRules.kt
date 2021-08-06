package com.github.dcc.decaf.semanticRules

import com.github.dcc.compiler.semanticAnalysis.SemanticError
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.types.Type
import com.github.validation.Validated.Valid
import com.github.validation.Validated.Invalid
import com.github.validation.Validation
import com.github.validation.zip

object GeneralDeclarationRules {

    val arraySizeMustBeGreaterThanZero = Validation<Collection<Declaration>, SemanticError> { symbols ->
        val checkArraySize = Validation<Declaration.Variable, SemanticError> { variable ->
            when(variable.type) {
                is Type.Array -> if(variable.type.size > 0) Valid else {
                    Invalid(
                        SemanticError(
                            "Array size must me greater than 0",
                            variable.context,
                            variable.type.context.start.charPositionInLine
                        )
                    )
                }
                else -> Valid
            }
        }

        symbols.mapNotNull { if(it is Declaration.Variable) checkArraySize(it) else null }.zip()
    }

}