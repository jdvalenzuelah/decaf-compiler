package com.github.dcc.compiler.ir.tac.productions

import com.github.dcc.compiler.ir.decaf.DecafStatement
import com.github.dcc.compiler.ir.decaf.LabeledBlock
import com.github.dcc.compiler.symbols.variables.SymbolTable
import com.github.dcc.decaf.enviroment.lineageAsString
import com.github.dcc.decaf.symbols.MethodStore
import com.github.dcc.decaf.symbols.TypeStore
import com.github.dcc.parser.DecafBaseVisitor
import com.github.dcc.parser.DecafParser

class StatementProduction(private val symbols: SymbolTable, private val methods: MethodStore, private val structs: TypeStore): DecafBaseVisitor<DecafStatement>() {

    private val currentScope = ExpressionProduction(symbols, methods, structs)

    override fun visitStatement(ctx: DecafParser.StatementContext): DecafStatement {
        return when {
            ctx.if_expr() != null -> visitIf_expr(ctx.if_expr())
            ctx.while_expr() != null -> visitWhile_expr(ctx.while_expr())
            ctx.return_expr() != null -> visitReturn_expr(ctx.return_expr())
            ctx.method_call() != null -> visitMethod_call(ctx.method_call())
            ctx.block() != null -> visitBlock(ctx.block())
            ctx.assignment() != null -> visitAssignment(ctx.assignment())
            else -> DecafStatement.Expression(currentScope.visitExpression(ctx.expression()))
        }
    }

    override fun visitIf_expr(ctx: DecafParser.If_exprContext): DecafStatement.If {
        val condition = currentScope.visitExpression(ctx.if_block().expression())

        val ifScope = symbols.getNextChildScope("if")
        val ifScopeExpressions = StatementProduction(ifScope, methods, structs)
        val ifBlock = LabeledBlock(
            label = ifScope.scope.lineageAsString(),
            statements = DecafStatement.Block(
                ctx.if_block().block().statement().map { ifScopeExpressions.visitStatement(it) }
            ),
            scope = ifScope
        )

        val elseBlock = if(ctx.else_block() != null) {
            val elseScope = symbols.getNextChildScope("else")
            val elseScopeExpressions = StatementProduction(elseScope, methods, structs)
            LabeledBlock(
                label = elseScope.scope.lineageAsString(),
                statements = DecafStatement.Block(
                    ctx.else_block().block().statement().map { elseScopeExpressions.visitStatement(it) }
                ),
                scope = elseScope
            )
        } else null

        return DecafStatement.If(
            condition = condition,
            ifBlock = ifBlock,
            elseBlock = elseBlock
        )
    }

    override fun visitWhile_expr(ctx: DecafParser.While_exprContext): DecafStatement.While {
        val whileScope = symbols.getNextChildScope("while")
        val whileScopeExpressions = StatementProduction(whileScope, methods, structs)
        return DecafStatement.While(
            condition = currentScope.visitExpression(ctx.expression()),
            block = LabeledBlock(
                label = whileScope.scope.lineageAsString(),
                statements =  DecafStatement.Block(
                    ctx.block().statement().map { whileScopeExpressions.visitStatement(it) }
                ),
                scope = whileScope
            )
        )
    }

    override fun visitReturn_expr(ctx: DecafParser.Return_exprContext): DecafStatement.Return {
        return DecafStatement.Return(currentScope.visitExpression(ctx.expression()))
    }

    override fun visitMethod_call(ctx: DecafParser.Method_callContext): DecafStatement.MethodCall {
        return DecafStatement.MethodCall(currentScope.visitMethod_call(ctx))
    }

    override fun visitBlock(ctx: DecafParser.BlockContext): DecafStatement.Block {
        return DecafStatement.Block(ctx.statement().map(::visitStatement))
    }

    override fun visitAssignment(ctx: DecafParser.AssignmentContext): DecafStatement.Assignment {
        return DecafStatement.Assignment(
            location = currentScope.visitLocation(ctx.location()),
            expression = currentScope.visitExpression(ctx.expression())
        )
    }
}