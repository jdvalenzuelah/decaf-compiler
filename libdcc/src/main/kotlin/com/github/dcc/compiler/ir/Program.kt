package com.github.dcc.compiler.ir

import com.github.dcc.compiler.ir.tac.Instruction
import com.github.dcc.compiler.symbols.ProgramSymbols
import java.lang.StringBuilder

data class Program(
    val symbols: ProgramSymbols,
    val methods: Collection<Method>
) {

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