package com.github.dcc.compiler.symbols

import com.github.dcc.compiler.symbols.methods.MethodStoreBuilder
import com.github.dcc.compiler.symbols.types.TypeStoreBuilder
import com.github.dcc.compiler.symbols.variables.SymbolTable
import com.github.dcc.compiler.symbols.variables.SymbolTableBuilder
import com.github.dcc.decaf.symbols.MethodStore
import com.github.dcc.decaf.symbols.TypeStore
import com.github.dcc.parser.DecafParser

data class ProgramSymbols(
    val symbolTable: SymbolTable,
    val types: TypeStore,
    val methods: MethodStore,
) {

    companion object {
        fun of(program: DecafParser.ProgramContext): ProgramSymbols {

            return ProgramSymbols(
                symbolTable = SymbolTableBuilder(program),
                types = TypeStoreBuilder(program),
                methods = MethodStoreBuilder(program)
            )

        }
    }

}