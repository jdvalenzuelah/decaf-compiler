package com.github.dcc.compiler.context

import com.github.dcc.decaf.symbols.SymbolStore
import com.github.dcc.decaf.symbols.TypeStore
import kotlin.math.exp

fun Context.ProgramContext.allVariables() = variables + methods
    .flatMap { method -> method.block?.allVariables() ?: emptyList() }

fun Context.BlockContext.allVariables(): List<Context.VariableContext> = variables + statements.flatMap {
    when(it) {
        is Context.StatementContext.If -> {
            it.ifContext.ifBlockContext.block.allVariables() +
                    (it.ifContext.elseBlock?.block?.allVariables() ?: emptyList())
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
            is Context.StatementContext.If -> statement.ifContext.ifBlockContext.block.methodCalls() +
                    (statement.ifContext.elseBlock?.block?.methodCalls() ?: emptyList())
            is Context.StatementContext.While -> statement.whileContext.block.methodCalls()
            is Context.StatementContext.Block -> statement.blockContext.methodCalls()
            is Context.StatementContext.Assignment -> statement.assignmentContext.expression.methodCalls()
            is Context.StatementContext.Expression -> statement.expression.methodCalls()
            is Context.StatementContext.Return -> statement.returnContext.expression?.methodCalls() ?: emptyList()
        }
    }
}

fun Context.BlockContext.locations(): List<Context.LocationContext> {
    return statements.flatMap { statement ->
        when(statement) {
            is Context.StatementContext.MethodCall -> statement.methodCallContext.locations()
            is Context.StatementContext.If -> statement.ifContext.ifBlockContext.block.locations() +
                    (statement.ifContext.elseBlock?.block?.locations() ?: emptyList())
            is Context.StatementContext.While -> statement.whileContext.block.locations()
            is Context.StatementContext.Block -> statement.blockContext.locations()
            is Context.StatementContext.Assignment -> statement.assignmentContext.expression.locations()
            is Context.StatementContext.Expression -> statement.expression.locations()
            is Context.StatementContext.Return -> statement.returnContext.expression?.locations() ?: emptyList()
        }
    }
}

fun Context.MethodCallContext.locations(): List<Context.LocationContext> {
    return args.flatMap { it.expression.locations() }
}

fun Context.ExpressionContext.locations(): List<Context.LocationContext> {
    return when(this) {
        is Context.ExpressionContext.Location -> listOf(locationContext) +
                listOfNotNull(locationContext.subLocation?.location)
        is Context.ExpressionContext.Equality -> equalityContext.locations()
        is Context.ExpressionContext.MethodCall -> methodCallContext.locations()
        is Context.ExpressionContext.Literal -> emptyList()
    }
}

fun Context.EqualityContext.locations(): List<Context.LocationContext> {
    return comparison.locations() + eqOperations.flatMap { it.locations() } + condOperations.flatMap { it.locations() }
}

fun Context.ComparisonContext.locations(): List<Context.LocationContext> {
    return term.locations() + operations.flatMap { it.locations() }
}

fun Context.TermContext.locations(): List<Context.LocationContext> {
    return factor.locations() + operations.flatMap { it.locations() }
}

fun Context.FactorContext.locations(): List<Context.LocationContext> {
    return unary.locations() + operations.flatMap { it.locations() }
}

fun Context.UnaryContext.locations(): List<Context.LocationContext> {
    return when(this) {
        is Context.UnaryContext.Primary -> primary.locations()
        is Context.UnaryContext.Operation -> unary.locations()
    }
}

fun Context.MulDivContext.locations(): List<Context.LocationContext> = unary.locations()

fun Context.PrimaryContext.locations(): List<Context.LocationContext> {
    return when(this) {
        is Context.PrimaryContext.SymbolPri -> symbolPriContext.locations()
        is Context.PrimaryContext.Expression -> expression.locations()
    }
}

fun Context.SymbolPriContext.locations(): List<Context.LocationContext> {
    return when(this) {
        is Context.SymbolPriContext.Literal -> emptyList()
        is Context.SymbolPriContext.Location -> listOf(location)
    }
}

fun Context.SubAddContext.locations(): List<Context.LocationContext> = factor.locations()

fun Context.BooleanOperationContext.locations(): List<Context.LocationContext> = term.locations()

fun Context.EqOperationContext.locations(): List<Context.LocationContext> = comparison.locations()

fun Context.CondOperationContext.locations(): List<Context.LocationContext> = comparison.locations()

fun Context.ProgramContext.expressions(): List<Context.ExpressionContext> = methods.flatMap { it.expressions() }

fun Context.MethodContext.expressions(): List<Context.ExpressionContext> = block?.expressions() ?: emptyList()

fun Context.BlockContext.expressions(): List<Context.ExpressionContext> = statements.flatMap { it.expressions() }

fun Context.StatementContext.expressions(): List<Context.ExpressionContext> {
    return when(this) {
        is Context.StatementContext.Expression -> listOf(expression)
        is Context.StatementContext.While -> whileContext.expressions()
        is Context.StatementContext.Block -> blockContext.expressions()
        is Context.StatementContext.Assignment -> listOf(assignmentContext.expression)
        is Context.StatementContext.Return -> listOfNotNull(returnContext.expression)
        is Context.StatementContext.MethodCall -> methodCallContext.expressions()
        is Context.StatementContext.If -> ifContext.expressions()
    }
}

fun Context.WhileContext.expressions(): List<Context.ExpressionContext> = listOf(expression) + block.expressions()

fun Context.MethodCallContext.expressions(): List<Context.ExpressionContext> = args.flatMap { it.expressions() }

fun Context.ArgContext.expressions(): List<Context.ExpressionContext> = listOfNotNull(expression)

fun Context.IfExpressionContext.expressions(): List<Context.ExpressionContext> {
    return listOf(ifBlockContext.expression) + ifBlockContext.block.expressions() +
            (elseBlock?.block?.expressions() ?: emptyList())
}

fun Context.EqualityContext.terms(): List<Context.TermContext> = comparison.terms() +
        eqOperations.flatMap { it.terms() } + condOperations.flatMap { it.terms() }

fun Context.ComparisonContext.terms(): List<Context.TermContext> = listOf(term) + operations.flatMap { it.terms() }

fun Context.BooleanOperationContext.terms(): List<Context.TermContext> = listOf(term)

fun Context.EqOperationContext.terms(): List<Context.TermContext> = comparison.terms()

fun Context.CondOperationContext.terms(): List<Context.TermContext> =  comparison.terms()

fun Context.TermContext.factors(): List<Context.FactorContext>  = listOf(factor) + operations.map { it.factor }

fun Context.FactorContext.unary(): List<Context.UnaryContext> = listOf(unary) + operations.map { it.unary }