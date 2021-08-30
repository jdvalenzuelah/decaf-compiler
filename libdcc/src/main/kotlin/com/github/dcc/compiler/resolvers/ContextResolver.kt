package com.github.dcc.compiler.resolvers

import com.github.dcc.compiler.CompilerContext
import com.github.dcc.compiler.context.Context
import com.github.dcc.compiler.resolvers.DeclarationResolver
import com.github.dcc.compiler.resolvers.StaticTypeResolver
import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.enviroment.child
import com.github.dcc.decaf.literals.Literal
import com.github.dcc.decaf.operators.*
import com.github.dcc.parser.DecafBaseVisitor
import com.github.dcc.parser.DecafParser
import kotlin.math.exp


class ProgramContextResolver private constructor(
    private val declarationResolver: DeclarationResolver,
): DecafBaseVisitor<Context>() {

    companion object {
        fun resolve(context: CompilerContext) = resolve(context.parser)
        fun resolve(parser: DecafParser) = resolve(parser.program())
        fun resolve(parser: DecafParser.ProgramContext) = ProgramContextResolver(
            declarationResolver = DeclarationResolver(
                typeResolver = StaticTypeResolver(),
                currentScope = Scope.Global,
            )
        )
            .visitProgram(parser)
    }


    override fun visitProgram(ctx: DecafParser.ProgramContext): Context.ProgramContext {
        return Context.ProgramContext(
            variables = ctx.var_decl().map(::visitVar_decl),
            structs = ctx.struct_decl().map(::visitStruct_decl),
            methods = ctx.method_decl().map(::visitMethod_decl),
            parserContext = ctx
        )
    }

    override fun visitVar_decl(ctx: DecafParser.Var_declContext): Context.VariableContext {
        return Context.VariableContext(
            declaration = declarationResolver.visitVar_decl(ctx),
            parserContext = ctx
        )
    }

    override fun visitStruct_decl(ctx: DecafParser.Struct_declContext): Context.StructContext {
        return Context.StructContext(
            declaration = declarationResolver.visitStruct_decl(ctx),
            parserContext = ctx
        )
    }

    override fun visitMethod_decl(ctx: DecafParser.Method_declContext): Context.MethodContext {
        val decl = declarationResolver.visitMethod_decl(ctx)
        return Context.MethodContext(
            declaration = decl,
            block = BlockContextResolver.resolve(ctx.block(), Scope.Global.child(decl.name)),
            parserContext = ctx,
        )
    }
}

class BlockContextResolver internal constructor(
    private val declarationResolver: DeclarationResolver,
) : DecafBaseVisitor<Context>() {

    companion object {
        fun resolve(parser: DecafParser.BlockContext, scope: Scope = Scope.Global) = BlockContextResolver(
            declarationResolver = DeclarationResolver(
                typeResolver = StaticTypeResolver(),
                currentScope = scope,
            )
        )
            .visitBlock(parser)
    }

    override fun visitBlock(ctx: DecafParser.BlockContext): Context.BlockContext {
        return Context.BlockContext(
            variables = ctx.var_decl().map {
                Context.VariableContext(
                    declaration = declarationResolver.visitVar_decl(it),
                    parserContext = it
                )
            },
            statements = ctx.statement().mapNotNull(::visitStatement),
            parserContext = ctx
        )
    }

    override fun visitStatement(ctx: DecafParser.StatementContext): Context.StatementContext? {
        return when {
            ctx.if_expr() != null -> Context.StatementContext.If(visitIf_expr(ctx.if_expr()), ctx)
            ctx.while_expr() != null -> Context.StatementContext.While(visitWhile_expr(ctx.while_expr()), ctx)
            ctx.return_expr() != null -> Context.StatementContext.Return(visitReturn_expr(ctx.return_expr()), ctx)
            ctx.method_call() != null -> Context.StatementContext.MethodCall(visitMethod_call(ctx.method_call()), ctx)
            ctx.block() != null -> Context.StatementContext.Block(visitBlock(ctx.block()), ctx)
            ctx.assignment() != null -> Context.StatementContext.Assignment(visitAssignment(ctx.assignment()), ctx)
            ctx.expression() != null -> Context.StatementContext.Expression(visitExpression(ctx.expression()), ctx)
            else -> null
        }
    }

    override fun visitWhile_expr(ctx: DecafParser.While_exprContext): Context.WhileContext {
        return Context.WhileContext(
            expression = visitExpression(ctx.expression()),
            block = visitBlock(ctx.block()),
            parserContext = ctx
        )
    }

    override fun visitReturn_expr(ctx: DecafParser.Return_exprContext): Context.ReturnContext {
        return Context.ReturnContext(
            expression = ctx.expression()?.let(::visitExpression),
            parserContext = ctx,
        )
    }

    override fun visitAssignment(ctx: DecafParser.AssignmentContext): Context.AssignmentContext {
        return Context.AssignmentContext(
            location = visitLocation(ctx.location()),
            expression = visitExpression(ctx.expression()),
            parserContext = ctx
        )
    }

    override fun visitIf_expr(ctx: DecafParser.If_exprContext): Context.IfExpressionContext {
        return Context.IfExpressionContext(
            ifBlockContext = visitIf_block(ctx.if_block()),
            elseBlock = ctx.else_block()?.let { visitElse_block(it) },
            parserContext = ctx,
        )
    }

    override fun visitIf_block(ctx: DecafParser.If_blockContext): Context.IfBlockContext {
        return Context.IfBlockContext(
            expression = visitExpression(ctx.expression()),
            block = visitBlock(ctx.block()),
            parserContext = ctx,
        )
    }

    override fun visitElse_block(ctx: DecafParser.Else_blockContext): Context.ElseBlockContext {
        return Context.ElseBlockContext(
            block = visitBlock(ctx.block()),
            parserContext = ctx
        )
    }

    override fun visitExpression(ctx: DecafParser.ExpressionContext): Context.ExpressionContext {
        return when {
            ctx.equality() != null -> Context.ExpressionContext.Equality(visitEquality(ctx.equality()), ctx)
            ctx.location() != null -> Context.ExpressionContext.Location(visitLocation(ctx.location()), ctx)
            ctx.method_call() != null -> Context.ExpressionContext.MethodCall(visitMethod_call(ctx.method_call()), ctx)
            ctx.literal() != null -> Context.ExpressionContext.Literal(visitLiteral(ctx.literal()), ctx)
            else -> error("Malformed expression!")
        }
    }

    override fun visitEquality(ctx: DecafParser.EqualityContext): Context.EqualityContext {
        return Context.EqualityContext(
            comparison = visitComparison(ctx.comparison()),
            eqOperations = ctx.eq_operation().map(::visitEq_operation),
            condOperations = ctx.cond_operation().map(::visitCond_operation),
            parserContext = ctx,
        )
    }

    override fun visitComparison(ctx: DecafParser.ComparisonContext): Context.ComparisonContext {
        return Context.ComparisonContext(
            term = visitTerm(ctx.term()),
            operations = ctx.boolean_operation().map(::visitBoolean_operation),
            parserContext = ctx
        )
    }

    override fun visitTerm(ctx: DecafParser.TermContext): Context.TermContext {
        return Context.TermContext(
            factor = visitFactor(ctx.factor()),
            operations = ctx.sub_add_op().map(::visitSub_add_op),
            parserContext = ctx,
        )
    }

    override fun visitFactor(ctx: DecafParser.FactorContext): Context.FactorContext {
        return Context.FactorContext(
            unary = visitUnary(ctx.unary()),
            operations = ctx.mul_div_op().map(::visitMul_div_op),
            parserContext = ctx
        )
    }

    override fun visitUnary(ctx: DecafParser.UnaryContext): Context.UnaryContext {
        return ctx.primary()
            ?.let { Context.UnaryContext.Primary(primary = visitPrimary(it), parserContext = ctx) }
            ?: Context.UnaryContext.Operation(
                operator = Unary.valueOfOrNull(ctx.unary_op().EXCL()?.text ?: ctx.unary_op().SUB()?.text ?: "") ?: error("unrecognized operator"),
                unary = visitUnary(ctx.unary()),
                parserContext = ctx
            )
    }

    override fun visitPrimary(ctx: DecafParser.PrimaryContext): Context.PrimaryContext {
        return ctx.symbol_pri()
            ?.let(::visitSymbol_pri)
            ?.let { Context.PrimaryContext.SymbolPri(it, ctx) }
            ?: ctx.expression()
                .let(::visitExpression)
                .let { Context.PrimaryContext.Expression(it, ctx) }
    }

    override fun visitSymbol_pri(ctx: DecafParser.Symbol_priContext): Context.SymbolPriContext {
        return ctx.literal()
            ?.let(::visitLiteral)
            ?.let { Context.SymbolPriContext.Literal(it, ctx) }
            ?: ctx.method_call()
                ?.let(::visitMethod_call)
                ?.let { Context.SymbolPriContext.MethodCall(it, ctx) }
            ?: ctx.location()
                .let(::visitLocation)
                .let { Context.SymbolPriContext.Location(it, ctx) }
    }

    override fun visitMul_div_op(ctx: DecafParser.Mul_div_opContext): Context.MulDivContext {
        return Context.MulDivContext(
            operator = Arithmetic.valueOfOrNull(ctx.arith_op_mul().text) ?: error("Unrecognized operator"),
            unary = visitUnary(ctx.unary()),
            parserContext = ctx
        )
    }

    override fun visitSub_add_op(ctx: DecafParser.Sub_add_opContext): Context.SubAddContext {
        return Context.SubAddContext(
            operator = Arithmetic.valueOfOrNull(ctx.arith_op_sub().text) ?: error("Unrecognized operator"),
            factor = visitFactor(ctx.factor()),
            parserContext = ctx
        )
    }

    override fun visitBoolean_operation(ctx: DecafParser.Boolean_operationContext): Context.BooleanOperationContext {
        return Context.BooleanOperationContext(
            operator = visitBool_ret_op(ctx.bool_ret_op()),
            term = visitTerm(ctx.term()),
            parserContext = ctx,
        )
    }

    override fun visitBool_ret_op(ctx: DecafParser.Bool_ret_opContext): Context.BoolOpContext {
        return ctx.rel_op()?.let {
            val txt = it.GT()?.text ?: it.GTE()?.text ?: it.LT()?.text ?: it.LTE().text
            Context.BoolOpContext.RelOp(
                relOp = Comparison.valueOfOrNull(txt) ?: error("Unrecognized operator"),
                parserContext = ctx,
            )
        } ?: ctx.eq_op().let {
            Context.BoolOpContext.EqOp(
                eqOp = Equality.valueOfOrNull(it.EQUALTO()?.text ?: it.NOTEQUAL().text) ?: error("Unrecognized operator"),
                parserContext = ctx,
            )
        }
    }

    override fun visitEq_operation(ctx: DecafParser.Eq_operationContext): Context.EqOperationContext {
        return Context.EqOperationContext(
            operator = Equality.valueOfOrNull(ctx.eq_op().EQUALTO()?.text ?: ctx.eq_op().NOTEQUAL().text) ?: error("Unrecognized operator"),
            comparison = visitComparison(ctx.comparison()),
            parserContext = ctx,
        )
    }

    override fun visitCond_operation(ctx: DecafParser.Cond_operationContext): Context.CondOperationContext {
        return Context.CondOperationContext(
            operator = Condition.valueOfOrNull(ctx.cond_op().AND()?.text ?: ctx.cond_op().OR().text) ?: error("Unrecognized operator"),
            comparison = visitComparison(ctx.comparison()),
            parserContext = ctx,
        )
    }

    override fun visitLocation(ctx: DecafParser.LocationContext): Context.LocationContext {
        val sub = ctx.sub_location()?.let(::visitSub_location)
        val varLoc = ctx.var_location()
        return varLoc.ID()?.let { Context.LocationContext.Simple(it.text, ctx, sub) }
            ?: varLoc.location_array().let(::visitLocation_array).let { Context.LocationContext.Array(it, ctx, sub) }
    }

    override fun visitLocation_array(ctx: DecafParser.Location_arrayContext): Context.LocationArrayContext {
        return Context.LocationArrayContext(
            id = ctx.ID().text,
            expression = visitExpression(ctx.expression()),
            parserContext = ctx,
        )
    }

    override fun visitSub_location(ctx: DecafParser.Sub_locationContext): Context.SubLocationContext {
        return Context.SubLocationContext(
            location = visitLocation(ctx.location()),
            parserContext = ctx
        )
    }

    override fun visitMethod_call(ctx: DecafParser.Method_callContext): Context.MethodCallContext {
        return Context.MethodCallContext(
            name = ctx.ID().text,
            args = ctx.arg().map(::visitArg),
            parserContext = ctx,
        )
    }

    override fun visitArg(ctx: DecafParser.ArgContext): Context.ArgContext {
        return Context.ArgContext(
            expression = visitExpression(ctx.expression()),
            parserContext = ctx,
        )
    }

    override fun visitLiteral(ctx: DecafParser.LiteralContext): Context.LiteralContext {
        return Context.LiteralContext(
            literal = when {
                ctx.CHAR_LITERAL() != null -> Literal.Char(ctx.CHAR_LITERAL().text)
                ctx.INT_LITERAL() != null -> Literal.Int(ctx.INT_LITERAL().text.toInt())
                ctx.BOOL_LITERAL() != null -> Literal.Boolean(ctx.BOOL_LITERAL().text.toBoolean())
                else -> error("unrecognized literal!")
            },
            parserContext = ctx
        )
    }

}