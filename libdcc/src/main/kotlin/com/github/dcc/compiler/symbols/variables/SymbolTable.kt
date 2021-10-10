package com.github.dcc.compiler.symbols.variables

import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.enviroment.child
import com.github.dcc.decaf.enviroment.lineage
import com.github.dcc.decaf.enviroment.lineageAsString
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.symbols.SymbolStore

class SymbolTable(
    val symbols: SymbolStore,
    val scope: Scope = Scope.Global,
    val parent: SymbolTable? = null,
    val child: MutableCollection<SymbolTable> = mutableListOf()
) {

    private fun getEncodedLabel(label: String, index: Int) = "$label$$index"

    fun childScope(label: String) = scope.child(getEncodedLabel(label, child.size))

    fun addNewScope(label: String, symbols: SymbolStore): SymbolTable {
        val new = SymbolTable(
            scope = childScope(label),
            symbols = symbols,
            parent = this
        )
        child.add(new)
        return new
    }

    fun allSymbols(): SymbolStore = symbols + child.flatMap { it.allSymbols() }

    private var currentScopeIndex = 0

    fun resetScope() {
        currentScopeIndex = 0
        child.forEach(SymbolTable::resetScope)
    }

    fun getNextChildScope(label: String): SymbolTable {
        val encoded = getEncodedLabel(label, currentScopeIndex)
        val found = child.first { it.scope is Scope.Local && it.scope.name == encoded }
        currentScopeIndex++
        return found
    }

    fun symbolBottomToTop(name: String): Declaration.Variable? {
        return symbols.firstOrNull { it.name == name }
            ?: parent?.symbolBottomToTop(name)
    }

    fun symbolIndexBottomToTOp(name: String): Int = symbols.indexOfFirst { it.name == name }
}
