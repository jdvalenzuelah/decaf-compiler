package com.github.dcc.decaf.symbols

import com.github.dcc.decaf.utils.StoreTable

/*
 Store table for all symbols
*/
typealias SymbolTable = StoreTable<Symbol>

fun emptySymbolTable(): SymbolTable = SymbolTable()

fun symbolTableOf(vararg symbols: Pair<String, Symbol>) = SymbolTable().apply { putAll(symbols) }
