package com.github.dcc.decaf.symbols

import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.enviroment.contains
import com.github.dcc.decaf.types.Type

typealias SequentialStore<T> = Collection<T>

typealias SymbolStore = SequentialStore<Declaration.Variable>

typealias TypeStore = SequentialStore<Declaration.Struct>

typealias MethodStore = SequentialStore<Declaration.Method>

fun SymbolStore.findBySignatureOrNull(_signature: Signature): Declaration.Method? {
    val parameters = _signature.parameters
        .map { if(it is Type.Array) Type.ArrayUnknownSize(it.type) else it } // ignore array size
    val signature = _signature.copy(parameters = parameters)
    return filterIsInstance<Declaration.Method>().firstOrNull { it.signature() == signature }
}