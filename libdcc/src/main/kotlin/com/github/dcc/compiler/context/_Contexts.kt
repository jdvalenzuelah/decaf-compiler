package com.github.dcc.compiler.context

import com.github.dcc.decaf.symbols.Declaration

fun Context.ProgramContext.allVariables() = variables + methods
    .flatMap { method -> method.block?.allVariables() ?: emptyList() }

fun Context.BlockContext.allVariables(): List<Context.VariableContext> = variables + statements.flatMap {
    when(it) {
        is Context.StatementContext.If -> {
            it.ifContext.ifBlockContext.block.allVariables() + (it.ifContext.elseBlock?.block?.allVariables() ?: emptyList())
        }
        is Context.StatementContext.While -> it.whileContext.block.allVariables()
        is Context.StatementContext.Block -> it.blockContext.allVariables()
        is Context.StatementContext.Expression,
        is Context.StatementContext.Assignment,
        is Context.StatementContext.MethodCall,
        is Context.StatementContext.Return -> emptyList()
    }
}

val Context.ProgramContext.symbols : List<Declaration>
    get() = allVariables().map { it.declaration } + methods.map { it.declaration }

val Context.ProgramContext.types : List<Declaration.Struct>
    get() = structs.map { it.declaration }