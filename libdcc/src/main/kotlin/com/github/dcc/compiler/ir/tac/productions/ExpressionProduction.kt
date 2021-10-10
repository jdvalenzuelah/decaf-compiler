package com.github.dcc.compiler.ir.tac.productions

import com.github.dcc.compiler.ir.decaf.DecafExpression
import com.github.dcc.compiler.resolvers.ContextualTypeResolver
import com.github.dcc.compiler.symbols.variables.SymbolTable
import com.github.dcc.decaf.literals.Literal
import com.github.dcc.decaf.operators.*
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.symbols.MethodStore
import com.github.dcc.decaf.symbols.TypeStore
import com.github.dcc.parser.DecafBaseVisitor
import com.github.dcc.parser.DecafParser

class ExpressionProduction(symbols: SymbolTable, methods: MethodStore, structs: TypeStore): DecafBaseVisitor<DecafExpression>() {

    private val contextualTypeResolver = ContextualTypeResolver(symbols, methods, structs)

    override fun visitExpression(ctx: DecafParser.ExpressionContext): DecafExpression {
        return when {
            ctx.method_call() != null -> visitMethod_call(ctx.method_call())
            ctx.location() != null -> visitLocation(ctx.location())
            ctx.literal() != null -> visitLiteral(ctx.literal())
            ctx.OPARENTHESIS() != null -> visitExpression(ctx.expression().first())
            ctx.unary_op() != null -> {
                DecafExpression.UnaryOp(
                    operator = Unary.valueOfOrNull(ctx.unary_op().text)!!,
                    op = visitExpression(ctx.expression().first())
                )
            }
            ctx.arith_op_mul() != null || ctx.arith_op_sub() != null -> {
                val (op1, op2) = ctx.expression()
                val op = ctx.arith_op_mul()?.text ?:  ctx.arith_op_sub()?.text ?: ""
                DecafExpression.ArithOp(
                    op1 = visitExpression(op1),
                    op2 = visitExpression(op2),
                    operator = Arithmetic.valueOfOrNull(op)!!
                )
            }
            ctx.rel_op() != null -> {
                val (op1, op2) = ctx.expression()
                DecafExpression.CompOp(
                    op1 = visitExpression(op1),
                    op2 = visitExpression(op2),
                    operator = Comparison.valueOfOrNull(ctx.rel_op().text)!!
                )
            }
            ctx.eq_op() != null -> {
                val (op1, op2) = ctx.expression()
                DecafExpression.EqOp(
                    op1 = visitExpression(op1),
                    op2 = visitExpression(op2),
                    operator = Equality.valueOfOrNull(ctx.eq_op().text)!!
                )
            }
            ctx.cond_op() != null -> {
                val (op1, op2) = ctx.expression()
                DecafExpression.CondOp(
                    op1 = visitExpression(op1),
                    op2 = visitExpression(op2),
                    operator = Condition.valueOfOrNull(ctx.cond_op().text)!!
                )
            }
            else -> error("unexpected expression ${ctx.text}")
        }
    }

    override fun visitMethod_call(ctx: DecafParser.Method_callContext): DecafExpression.MethodCall  {
        return DecafExpression.MethodCall(
            signature = Declaration.Method.Signature(
                name = ctx.ID().text,
                parametersType = ctx.arg().map { contextualTypeResolver.visitArg(it) }
            ),
            parameters = ctx.arg().map { visitExpression(it.expression()) }
        )

    }

    override fun visitLocation(ctx: DecafParser.LocationContext): DecafExpression.Location  {
        return DecafExpression.Location(ctx)
    }

    override fun visitLiteral(ctx: DecafParser.LiteralContext): DecafExpression  {
        val literal = when {
            ctx.BOOL_LITERAL() != null -> Literal.Boolean(ctx.BOOL_LITERAL().text == "true")
            ctx.INT_LITERAL() != null -> Literal.Int(ctx.INT_LITERAL().text.toInt())
            else -> Literal.Char(ctx.CHAR_LITERAL().text)
        }

        return DecafExpression.Constant(literal)
    }
}

