package com.github.dcc.compiler.ir

import com.github.dcc.compiler.ir.tac.productions.ProgramProduction
import com.github.dcc.compiler.ir.tac.transforms.MethodTransform
import com.github.dcc.compiler.symbols.ProgramSymbols
import com.github.dcc.parser.DecafParser

object ProgramTransform {

    operator fun invoke(
        program: DecafParser.ProgramContext,
        symbols: ProgramSymbols,
    ): Program {
        val decafProgram = ProgramProduction(
            symbols.symbolTable,
            symbols.methods,
            symbols.types
        ).visitProgram(program)

        val methodTransform = MethodTransform(symbols.symbolTable, symbols.methods, symbols.types)

        return Program(
            symbols = symbols,
            methods = decafProgram.methods.map { methodTransform.transform(it) }
        )
    }

}