package com.github.dcc.decaf.utils

/*
 Storage for type and symbol table
*/
open class StoreTable<T> : MutableMap<String, T> by HashMap()