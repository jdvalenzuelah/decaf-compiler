package com.github.dcc.compiler.semanticAnalysis

import com.github.dcc.compiler.resolvers.SymbolTableResolver
import com.github.dcc.decaf.sematicRules.GlobalScopeRules
import com.github.dcc.decaf.sematicRules.SemanticError
import com.github.dcc.parser.DecafParser
import com.github.rules.Result
import org.antlr.v4.runtime.CommonTokenStream

class SemanticAnalysis(
    private val tokenStream: CommonTokenStream, //TODO: Define a parser ds scope
    private val parser: DecafParser,
) {

    private val symbolTable = withParser { SymbolTableResolver(it) }

    private fun <T> withParser(block: (DecafParser) -> T ): T {
        tokenStream.reset()
        return block(parser)
    }

    fun analyze(): Result<*, SemanticError> {
        return GlobalScopeRules.symbolRules()
            .eval(symbolTable)
    }

}