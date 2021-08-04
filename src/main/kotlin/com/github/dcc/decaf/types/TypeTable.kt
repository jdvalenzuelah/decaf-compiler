package com.github.dcc.decaf.types

import com.github.dcc.decaf.utils.StoreTable

/*
 Store table for custom types (struct decl)
*/
typealias TypeTable = StoreTable<Type.StructDecl>

fun emptyTypeTable(): TypeTable = TypeTable()

fun typeTableOf(vararg types: Pair<String, Type.StructDecl>): TypeTable = TypeTable().apply { putAll(types) }