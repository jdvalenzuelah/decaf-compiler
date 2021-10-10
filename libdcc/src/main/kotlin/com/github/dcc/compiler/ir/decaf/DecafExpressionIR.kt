package com.github.dcc.compiler.ir.decaf

import com.github.dcc.compiler.symbols.variables.SymbolTable
import com.github.dcc.decaf.literals.Literal
import com.github.dcc.decaf.operators.*
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.symbols.TypeStore
import com.github.dcc.parser.DecafParser

sealed class DecafElementsIR

data class LabeledBlock(
    val label: String,
    val statements: DecafStatement.Block
): DecafElementsIR()

sealed class DecafExpression : DecafElementsIR() {

    data class MethodCall(
        val signature: Declaration.Method.Signature,
        val parameters: List<DecafExpression>,
    ): DecafExpression()

    //TODO: remove parser context
    data class Location(
        val ctx: DecafParser.LocationContext,
    ): DecafExpression() {
        override fun toString(): String = ctx.text
    }

    data class Constant(
        val value: Literal
    ): DecafExpression()

    data class ArithOp(
        val op1: DecafExpression,
        val op2: DecafExpression,
        val operator: Arithmetic,
    ): DecafExpression() {
        override fun toString(): String = "$op1$op2${operator.op}"
    }

    data class EqOp(
        val op1: DecafExpression,
        val op2: DecafExpression,
        val operator: Equality,
    ): DecafExpression() {
        override fun toString(): String = "$op1$op2${operator.op}"
    }

    data class CompOp(
        val op1: DecafExpression,
        val op2: DecafExpression,
        val operator: Comparison,
    ): DecafExpression() {
        override fun toString(): String = "$op1$op2${operator.op}"
    }

    data class CondOp(
        val op1: DecafExpression,
        val op2: DecafExpression,
        val operator: Condition,
    ): DecafExpression() {
        override fun toString(): String = "$op1$op2${operator.op}"
    }

    data class UnaryOp(
        val op: DecafExpression,
        val operator: Unary,
    ): DecafExpression() {
        override fun toString(): String = "$op${operator.op}"
    }

}

sealed class DecafStatement : DecafElementsIR() {


    //TODO: Add var decls?
    data class Block(
        val statements: Collection<DecafStatement>,
    ): DecafStatement()

    data class If(
        val condition: DecafExpression,
        val ifBlock: LabeledBlock,
        val elseBlock: LabeledBlock?,
    ): DecafStatement()

    data class While(
        val condition: DecafExpression,
        val block: LabeledBlock,
    ): DecafStatement()

    data class MethodCall(
        val call: DecafExpression.MethodCall,
    ): DecafStatement()

    data class Return(
        val expression: DecafExpression,
    ): DecafStatement()

    data class Assignment(
        val location: DecafExpression.Location,
        val expression: DecafExpression,
    ): DecafStatement()

    data class Expression(
        val expression: DecafExpression,
    ): DecafStatement()

}

data class DecafMethod(
    val signature: Declaration.Method.Signature,
    val block: DecafStatement.Block,
    val symbols: SymbolTable,
): DecafElementsIR()


data class DecafProgram(
    val symbols: SymbolTable,
    val types: TypeStore,
    val methods: List<DecafMethod>,
): DecafElementsIR()