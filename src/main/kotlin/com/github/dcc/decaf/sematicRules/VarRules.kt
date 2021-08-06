package com.github.dcc.decaf.sematicRules

import com.github.dcc.decaf.symbols.Symbol
import com.github.dcc.decaf.symbols.SymbolTable
import com.github.dcc.decaf.types.Type
import com.github.rules.*

object VarRules {

    val arraySizeGreaterThanZero: IRule<SymbolTable, *, SemanticError> = rule { symbols ->
        val gtZeroRule = rule<Symbol, Any?, SemanticError> {
            val type = it.type
            if(it !is Symbol.Variable || (type !is Type.Array || type.size > 0))
                valid()
            else
                error(SemanticError("Array size must be greater than 0", it.location))
        }

        symbols.values.zip(gtZeroRule)
    }

}