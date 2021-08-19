package com.github.dcc.compiler.context

import com.github.dcc.decaf.symbols.SymbolStore
import com.github.dcc.decaf.symbols.TypeStore

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

val Context.ProgramContext.symbols : SymbolStore
    get() = allVariables().map { it.declaration } + methods.map { it.declaration }

val Context.ProgramContext.types : TypeStore
    get() = structs.map { it.declaration }


fun Context.PrimaryContext.methodCalls(): List<Context.MethodCallContext> {
    return when(this) {
        is Context.PrimaryContext.Expression -> expression.methodCalls()
        is Context.PrimaryContext.SymbolPri -> emptyList()
    }
}

fun Context.UnaryContext.methodCalls(): List<Context.MethodCallContext> {
    return when(this) {
        is Context.UnaryContext.Operation -> unary.methodCalls()
        is Context.UnaryContext.Primary -> primary.methodCalls()
    }
}

fun Context.FactorContext.methodCalls(): List<Context.MethodCallContext> {
    return unary.methodCalls() + operations.flatMap { it.unary.methodCalls() }
}

fun Context.TermContext.methodCalls(): List<Context.MethodCallContext> {
    return factor.methodCalls() + operations.flatMap { it.factor.methodCalls() }
}

fun Context.ComparisonContext.methodCalls(): List<Context.MethodCallContext> {
    return term.methodCalls() + operations.flatMap { it.term.methodCalls() }
}

fun Context.EqualityContext.methodCalls(): List<Context.MethodCallContext> {
    return comparison.methodCalls() + eqOperations.flatMap { it.comparison.methodCalls() } +
            condOperations.flatMap { it.comparison.methodCalls() }
}

fun Context.ExpressionContext.methodCalls(): List<Context.MethodCallContext> {
    return when(this) {
        is Context.ExpressionContext.MethodCall -> listOf(this.methodCallContext)
        is Context.ExpressionContext.Equality -> emptyList()
        is Context.ExpressionContext.Location -> emptyList()
        is Context.ExpressionContext.Literal -> emptyList()
    }
}

fun Context.BlockContext.methodCalls(): List<Context.MethodCallContext> {
    return statements.flatMap { statement ->
        when(statement) {
            is Context.StatementContext.MethodCall -> listOf(statement.methodCallContext)
            is Context.StatementContext.If -> statement.ifContext.ifBlockContext.block.methodCalls() + (statement.ifContext.elseBlock?.block?.methodCalls() ?: emptyList())
            is Context.StatementContext.While -> statement.whileContext.block.methodCalls()
            is Context.StatementContext.Block -> statement.blockContext.methodCalls()
            is Context.StatementContext.Assignment -> statement.assignmentContext.expression.methodCalls()
            is Context.StatementContext.Expression -> statement.expression.methodCalls()
            is Context.StatementContext.Return -> statement.returnContext.expression?.methodCalls() ?: emptyList()
        }
    }
}