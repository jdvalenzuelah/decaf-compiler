package com.github.dcc.decaf.sematicRules

import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.symbols.Symbol
import com.github.dcc.decaf.symbols.SymbolTable
import com.github.rules.*


//TODO: Add tests
object ScopeRules {

    fun symbolRules() = methodContainsJustVariableParameters
        .next(singleMainDefinition)
        .next(mainDefinitionShouldHaveNoParams)

    val methodContainsJustVariableParameters: IRule<SymbolTable, *, SemanticError> = rule { symbols ->
        val methods = symbols.mapNotNull { (_, symbol) ->
            if(symbol is Symbol.Method) symbol else null
        }

        if(methods.all { method -> method.parameters.all { it is Symbol.Variable } }) {
            valid()
        } else {
            error(SemanticError("Method should only contain variable parameters", null))
        }

    }

    val singleMainDefinition: IRule<SymbolTable, *, SemanticError> = rule { symbols ->
        val globalScopeMain = symbols.filter { (_, symbol) ->
            symbol is Symbol.Method && symbol.scope == Scope.Global && symbol.name == "main"
        }

        when {
            globalScopeMain.isEmpty() -> error(SemanticError("Program does not contain main definition", null))
            globalScopeMain.size == 1 -> valid()
            else -> error(SemanticError("Program must contain one main definition", globalScopeMain.values.first().location))
        }
    }

    val mainDefinitionShouldHaveNoParams: IRule<SymbolTable, *, SemanticError> = rule { symbols ->
        val globalScopeMain = symbols.filter { (_, symbol) ->
            symbol is Symbol.Method && symbol.scope == Scope.Global && symbol.name == "main" && symbol.parameters.isNotEmpty()
        }

        if(globalScopeMain.isNotEmpty())
            error(SemanticError("Main definition must contain no parameters!", globalScopeMain.entries.first().value.location))
        else
            valid()

    }

}