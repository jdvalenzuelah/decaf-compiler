package com.github.dcc.decaf.sematicRules

import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.symbols.Symbol
import com.github.dcc.decaf.symbols.SymbolTable
import com.github.rules.rule
import com.github.rules.valid
import com.github.rules.error

object GlobalScopeRules {

    val singleMainDefinition = rule<SymbolTable> { symbols ->
        val globalScopeMain = symbols.filter { (_, symbol) ->
            //TODO: Check args
            symbol is Symbol.Method && symbol.scope == Scope.Global && symbol.name == "main"
        }

        if(globalScopeMain.size == 1)
            valid()
        else
            error(SemanticError("Program must contain a single main definition"))
    }

}