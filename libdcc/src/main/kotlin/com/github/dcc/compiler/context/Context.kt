package com.github.dcc.compiler.context

import com.github.dcc.decaf.literals.Literal
import com.github.dcc.decaf.operators.*
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.types.Type
import com.github.dcc.parser.DecafParser
import org.antlr.v4.runtime.ParserRuleContext

sealed class Context(
    open val parserContext: ParserRuleContext?
) {

    data class ProgramContext(
        val variables: List<VariableContext>,
        val structs: List<StructContext>,
        val methods: List<MethodContext>,
        override val parserContext: DecafParser.ProgramContext
    ): Context(parserContext)


    data class VariableContext(
        val declaration: Declaration.Variable,
        override val parserContext: DecafParser.Var_declContext?
    ): Context(parserContext)

    data class StructContext(
        val declaration: Declaration.Struct,
        override val parserContext: DecafParser.Struct_declContext
    ): Context(parserContext)

    data class MethodContext(
        val declaration: Declaration.Method,
        val block: BlockContext?,
        override val parserContext: DecafParser.Method_declContext
    ): Context(parserContext)

    data class BlockContext(
        val variables: List<VariableContext>,
        val statements: List<StatementContext>,
        override val parserContext: DecafParser.BlockContext
    ): Context(parserContext)

    sealed class StatementContext(
        override val parserContext: DecafParser.StatementContext
    ): Context(parserContext) {

        data class If(
            val ifContext: IfExpressionContext,
            override val parserContext: DecafParser.StatementContext
        ): StatementContext(parserContext)

        data class While(
            val whileContext: WhileContext,
            override val parserContext: DecafParser.StatementContext
        ): StatementContext(parserContext)

        data class Return(
            val returnContext: ReturnContext,
            override val parserContext: DecafParser.StatementContext
        ): StatementContext(parserContext)

        data class MethodCall(
            val methodCallContext: MethodCallContext,
            override val parserContext: DecafParser.StatementContext
        ): StatementContext(parserContext)

        data class Block(
            val blockContext: BlockContext,
            override val parserContext: DecafParser.StatementContext
        ): StatementContext(parserContext)

        data class Assignment(
            val assignmentContext: AssignmentContext,
            override val parserContext: DecafParser.StatementContext
        ): StatementContext(parserContext)

        data class Expression(
            val expression: ExpressionContext,
            override val parserContext: DecafParser.StatementContext
        ): StatementContext(parserContext)

    }

    data class IfExpressionContext(
        val ifBlockContext: IfBlockContext,
        val elseBlock: ElseBlockContext?,
        override val parserContext: DecafParser.If_exprContext
    ): Context(parserContext)

    data class IfBlockContext(
        val expression: ExpressionContext,
        val block: BlockContext,
        override val parserContext: DecafParser.If_blockContext
    ): Context(parserContext)

    data class ElseBlockContext(
        val block: BlockContext?,
        override val parserContext: DecafParser.Else_blockContext
    ): Context(parserContext)

    data class WhileContext(
        val expression: ExpressionContext,
        val block: BlockContext,
        override val parserContext: DecafParser.While_exprContext
    ): Context(parserContext)

    data class ReturnContext(
        val expression: ExpressionContext?,
        override val parserContext: DecafParser.Return_exprContext
    ): Context(parserContext)

    data class MethodCallContext(
        val name: String,
        val args: List<ArgContext>,
        override val parserContext: DecafParser.Method_callContext
    ): Context(parserContext)

    data class ArgContext(
        val expression: ExpressionContext,
        override val parserContext: DecafParser.ArgContext
    ): Context(parserContext)

    data class AssignmentContext(
        val location: LocationContext,
        val expression: ExpressionContext,
        override val parserContext: DecafParser.AssignmentContext
    ): Context(parserContext)

    sealed class LocationContext(
        open val subLocation: SubLocationContext?,
        override val parserContext: DecafParser.LocationContext
    ): Context(parserContext) {
        data class Simple(
            val id: String,
            override val parserContext: DecafParser.LocationContext,
            override val subLocation: SubLocationContext?,
        ): LocationContext(subLocation, parserContext)

        data class Array(
            val arrayLocation: LocationArrayContext,
            override val parserContext: DecafParser.LocationContext,
            override val subLocation: SubLocationContext?,
        ): LocationContext(subLocation, parserContext)
    }

    data class LocationArrayContext(
        val id: String,
        val expression: ExpressionContext,
        override val parserContext: DecafParser.Location_arrayContext
    ): Context(parserContext)

    data class SubLocationContext(
        val location: LocationContext,
        override val parserContext: DecafParser.Sub_locationContext
    ): Context(parserContext)

    sealed class ExpressionContext(
        override val parserContext: DecafParser.ExpressionContext
    ): Context(parserContext) {

        data class Equality(
            val equalityContext: EqualityContext,
            override val parserContext: DecafParser.ExpressionContext
        ): ExpressionContext(parserContext)

        data class Location(
            val locationContext: LocationContext,
            override val parserContext: DecafParser.ExpressionContext
        ): ExpressionContext(parserContext)

        data class MethodCall(
            val methodCallContext: MethodCallContext,
            override val parserContext: DecafParser.ExpressionContext
        ): ExpressionContext(parserContext)

        data class Literal(
            val literalContext: LiteralContext,
            override val parserContext: DecafParser.ExpressionContext
        ): ExpressionContext(parserContext)

    }

    data class EqualityContext(
        val comparison: ComparisonContext,
        val eqOperations: List<EqOperationContext>,
        val condOperations: List<CondOperationContext>,
        override val parserContext: DecafParser.EqualityContext,
    ): Context(parserContext)

    data class EqOperationContext(
        val operator: Equality,
        val comparison: ComparisonContext,
        override val parserContext: DecafParser.Eq_operationContext,
    ): Context(parserContext)

    data class CondOperationContext(
        val operator: Condition,
        val comparison: ComparisonContext,
        override val parserContext: DecafParser.Cond_operationContext,
    ): Context(parserContext)

    data class ComparisonContext(
        val term: TermContext,
        val operations: List<BooleanOperationContext>,
        override val parserContext: DecafParser.ComparisonContext,
    ): Context(parserContext)

    data class TermContext(
        val factor: FactorContext,
        val operations: List<SubAddContext>,
        override val parserContext: DecafParser.TermContext,
    ): Context(parserContext)

    data class FactorContext(
        val unary: UnaryContext,
        val operations: List<MulDivContext>,
        override val parserContext: DecafParser.FactorContext,
    ): Context(parserContext)


    sealed class UnaryContext(
        override val parserContext: DecafParser.UnaryContext,
    ): Context(parserContext) {

        data class Operation(
            val operator: Unary,
            val unary: UnaryContext,
            override val parserContext: DecafParser.UnaryContext,
        ): UnaryContext(parserContext)

        data class Primary(
            val primary: PrimaryContext,
            override val parserContext: DecafParser.UnaryContext,
        ): UnaryContext(parserContext)
    }

    sealed class PrimaryContext(
        override val parserContext: DecafParser.PrimaryContext
    ): Context(parserContext) {
        data class SymbolPri(
            val symbolPriContext: SymbolPriContext,
            override val parserContext: DecafParser.PrimaryContext
        ): PrimaryContext(parserContext)

        data class Expression(
            val expression: ExpressionContext,
            override val parserContext: DecafParser.PrimaryContext
        ): PrimaryContext(parserContext)
    }

    sealed class SymbolPriContext(
        override val parserContext: DecafParser.Symbol_priContext
    ): Context(parserContext) {
        data class Literal(
            val literal: LiteralContext,
            override val parserContext: DecafParser.Symbol_priContext
        ): SymbolPriContext(parserContext)

        data class Location(
            val location: LocationContext,
            override val parserContext: DecafParser.Symbol_priContext
        ): SymbolPriContext(parserContext)

        data class MethodCall(
            val methodCall: MethodCallContext,
            override val parserContext: DecafParser.Symbol_priContext
        ): SymbolPriContext(parserContext)
    }

    data class LiteralContext(
        val literal: Literal,
        override val parserContext: DecafParser.LiteralContext,
    ): Context(parserContext)

    data class MulDivContext(
        val operator: Arithmetic,
        val unary: UnaryContext,
        override val parserContext: DecafParser.Mul_div_opContext,
    ): Context(parserContext)

    data class SubAddContext(
        val operator: Arithmetic,
        val factor: FactorContext,
        override val parserContext: DecafParser.Sub_add_opContext,
    ): Context(parserContext)

    data class BooleanOperationContext(
        val operator: BoolOpContext,
        val term: TermContext,
        override val parserContext: DecafParser.Boolean_operationContext,
    ): Context(parserContext)

    sealed class BoolOpContext(
        override val parserContext: DecafParser.Bool_ret_opContext,
    ): Context(parserContext) {

        data class RelOp(
            val relOp: Comparison,
            override val parserContext: DecafParser.Bool_ret_opContext,
        ): BoolOpContext(parserContext)

        data class EqOp(
            val eqOp: Equality,
            override val parserContext: DecafParser.Bool_ret_opContext,
        ): BoolOpContext(parserContext)
    }


}