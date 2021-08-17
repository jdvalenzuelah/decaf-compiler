package com.github.dcc.compiler.context

import com.github.dcc.decaf.symbols.Declaration

fun Context.ProgramContext.allVariables() = variables + methods
    .flatMap { method -> method.block?.allVariables() ?: emptyList() }

fun Context.BlockContext.allVariables(): List<Context.VariableContext> = variables + statements.flatMap {
    when(val exp = it.expression) {
        is Context.IfExpressionContext -> {
            exp.ifBlockContext.block.allVariables() + (exp.elseBlock?.block?.allVariables() ?: emptyList())
        }
        is Context.WhileContext -> exp.block.allVariables()
        is Context.BlockContext -> exp.allVariables()
        is Context.ExpressionContext,
        is Context.AssignmentContext,
        is Context.MethodCallContext,
        is Context.ReturnContext -> emptyList()
        else -> emptyList()
    }
}

val Context.ProgramContext.symbols : List<Declaration>
    get() = allVariables().map { it.declaration } + methods.map { it.declaration } + structs.map { it.declaration }

val Context.ProgramContext.types : List<Declaration.Struct>
    get() = structs.map { it.declaration }