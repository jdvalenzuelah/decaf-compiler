package com.github.dcc.compiler.ir

import com.github.dcc.compiler.ir.tac.Instruction
import com.github.dcc.compiler.symbols.ProgramSymbols
import com.github.dcc.compiler.symbols.variables.SymbolTable
import java.lang.StringBuilder

data class Program(
    val symbols: ProgramSymbols,
    val methods: Collection<Method>
) {

    companion object {
        fun empty() = Program(
            ProgramSymbols(SymbolTable(emptyList()),emptyList(),emptyList()),
            emptyList()
        )
    }

    data class Method(
        val index: Int,
        val body: Instruction.Instructions,
    ) {
        override fun toString(): String = StringBuilder()
            .appendLine(".method $index")
            .appendLine(body.toString())
            .appendLine(".method end")
            .toString()
    }

    override fun toString(): String {
        return methods.joinToString(separator = "\n") { it.toString() }
    }

}