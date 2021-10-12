package com.github.dcc.compiler.ir.decaf

import com.github.dcc.compiler.symbols.variables.SymbolTable
import com.github.dcc.decaf.literals.Literal
import com.github.dcc.decaf.operators.*
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.symbols.TypeStore
import com.github.dcc.decaf.types.Type
import com.github.dcc.parser.DecafParser

sealed class DecafElementsIR

data class LabeledBlock(
    val label: String,
    val statements: DecafStatement.Block,
    val scope: SymbolTable
): DecafElementsIR()

sealed class DecafExpression : DecafElementsIR() {

    data class MethodCall(
        val signature: Declaration.Method.Signature,
        val parameters: List<DecafExpression>,
    ): DecafExpression()

    sealed class Location(
        val name: String,
        val subLocation: Location?,
        val context: Type.Struct?,
    ): DecafExpression() {
        class ArrayLocation(
            name: String,
            val index: DecafExpression,
            subLocation: Location?,
            context: Type.Struct?,
        ): Location(name, subLocation, context)

        class VarLocation(
            name: String,
            subLocation: Location?,
            context: Type.Struct?,
        ): Location(name, subLocation, context)

        internal fun flatten(): Collection<Location> {
            val all = mutableListOf(this)
            var cur: Location = this
            while (cur.subLocation != null) {
                cur = cur.subLocation!!
                all.add(cur)
            }
            return all
        }

        override fun toString(): String {
            return flatten().joinToString(separator = ".") {
                when(it) {
                    is ArrayLocation -> "${it.name}[${it.index}]"
                    is VarLocation -> it.name
                }
            }
        }
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