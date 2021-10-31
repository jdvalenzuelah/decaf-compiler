package com.github.dcc.decaf.symbols

import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.enviroment.contains
import com.github.dcc.decaf.types.Type

typealias SequentialStore<T> = Collection<T>

typealias SymbolStore = SequentialStore<Declaration.Variable>

typealias TypeStore = SequentialStore<Declaration.Struct>

typealias MethodStore = SequentialStore<Declaration.Method>