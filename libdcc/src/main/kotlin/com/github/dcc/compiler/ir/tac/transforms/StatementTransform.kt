package com.github.dcc.compiler.ir.tac.transforms

import com.github.dcc.compiler.ir.Program
import com.github.dcc.compiler.ir.decaf.DecafExpression
import com.github.dcc.compiler.ir.decaf.DecafMethod
import com.github.dcc.compiler.ir.decaf.DecafStatement
import com.github.dcc.compiler.ir.tac.Instruction
import com.github.dcc.compiler.ir.tac.instructions
import com.github.dcc.compiler.ir.tac.instructionsOf
import com.github.dcc.decaf.symbols.MethodStore
import com.github.dcc.decaf.symbols.TypeStore
import com.github.dcc.compiler.symbols.variables.SymbolTable
import com.github.dcc.decaf.operators.*
import com.github.dcc.decaf.types.Type

fun interface Transform<in A, out B> {
    fun transform(value: A): B
}


class MethodTransform(
    val global: SymbolTable,
    val methods: MethodStore,
    val types: TypeStore
): Transform<DecafMethod, Program.Method> {

    override fun transform(value: DecafMethod): Program.Method {
        val statementTransform =  StatementTransform(
            global = global,
            local = value.symbols,
            methods = methods,
            types = types
        )

        return Program.Method(
            index = methods.indexOfFirst { it.signature == value.signature },
            body = instructionsOf(value.block.statements.flatMap { statementTransform.transform(it) })
        )

    }

}

class StatementTransform(
    val global: SymbolTable,
    val local: SymbolTable,
    val methods: MethodStore,
    val types: TypeStore
): Transform<DecafStatement, Instruction.Instructions> {

    private val exprTransform = ExpressionTransform(global, local, methods, types)
    private val locationTransform = LocationTransform(global, local, methods, types)
    private var lastLabel: Int = 0

    private var nextNeedsToBeLabeled: Boolean = false

    override fun transform(value: DecafStatement): Instruction.Instructions {
        val needsToBeLabeled = nextNeedsToBeLabeled
        nextNeedsToBeLabeled = false
        val instructions = when(value) {
            is DecafStatement.Block -> {
                instructions {
                    value.statements.forEach {
                        +transform(it)
                    }
                }
            }
            is DecafStatement.If -> {
                val nextLabel = if(needsToBeLabeled) {
                    getLabel(lastLabel+1)
                } else getNextLabel()
                instructions {
                    +exprTransform.transform(value.condition)
                    +Instruction.If(value.ifBlock.label)

                    if(value.elseBlock != null) {
                        +StatementTransform(global, value.elseBlock.scope, methods, types)
                            .transform(value.elseBlock.statements)
                            .apply {
                                lastOrNull()?.also {
                                    if(it !is Instruction.Return)
                                        add(Instruction.Goto(nextLabel))
                                }
                            }
                    } else {
                        +Instruction.Goto(nextLabel)
                    }

                    +Instruction.LabeledBlock(
                        label = value.ifBlock.label,
                        instruction = StatementTransform(global, value.ifBlock.scope, methods, types)
                            .transform(value.ifBlock.statements)
                    )

                    nextNeedsLabel()
                }
            }
            is DecafStatement.While -> {
                val condLabel = "${value.block.label}#condition"
                val bodyLabel = "${value.block.label}#body"
                val nextLabel = if(needsToBeLabeled) {
                    getLabel(lastLabel+1)
                } else getNextLabel()
                instructions {
                    +Instruction.LabeledBlock(
                        label = condLabel,
                        instruction = exprTransform.transform(value.condition)
                    )
                    +Instruction.If(bodyLabel)
                    +Instruction.Goto(nextLabel)
                    +Instruction.LabeledBlock(
                        label = bodyLabel,
                        instruction = StatementTransform(global, value.block.scope, methods, types)
                            .transform(value.block.statements).apply {
                            add(Instruction.Goto(condLabel))
                        }
                    )

                    nextNeedsLabel()
                }
            }
            is DecafStatement.MethodCall -> {
                instructions {
                    value.call.parameters.map {
                        +exprTransform.transform(it)
                    }

                    val index = methods.indexOfFirst { value.call.signature  == it.signature }
                    +Instruction.MethodCall(index, value.call.parameters.size)
                }
            }
            is DecafStatement.Return -> {
                instructions {
                    +exprTransform.transform(value.expression)
                    +Instruction.Return
                }
            }
            is DecafStatement.Assignment -> {
                instructions {
                    +locationTransform.transform(value.location)
                    +exprTransform.transform(value.expression)
                    +Instruction.StoreRef
                }
            }
            is DecafStatement.Expression -> {
                instructions {
                    +exprTransform.transform(value.expression)
                }
            }
        }

        if(instructions.stack() > 0) {
            repeat(instructions.stack()) {
                instructions.add(Instruction.Pop)
            }
        }

        return if(needsToBeLabeled) {
            instructionsOf(
                Instruction.LabeledBlock(
                    label = getNextLabelAndIncrease(),
                    instruction = instructions
                )
            )
        } else instructions
    }

    private fun nextNeedsLabel() {
        nextNeedsToBeLabeled = true
    }

    private fun getNextLabelAndIncrease(): String {
        val label = getNextLabel()
        lastLabel++
        return label
    }

    private fun getNextLabel(): String = getLabel(lastLabel)

    private fun getLabel(index: Int) ="_L$index"
}

class ExpressionTransform(
    val global: SymbolTable,
    val local: SymbolTable,
    val methods: MethodStore,
    val types: TypeStore
): Transform<DecafExpression, Instruction.Instructions> {
    override fun transform(value: DecafExpression): Instruction.Instructions {
        val instructions =  when(value) {
            is DecafExpression.MethodCall -> {
                val params = value.parameters
                    .flatMap(::transform)

                val methodIndex = methods.indexOfFirst { it.signature == value.signature }

                instructions {
                    +params
                    +Instruction.MethodCall(
                        index = methodIndex,
                        parameterCount = value.parameters.size
                    )
                }
            }
            is DecafExpression.Location -> {
                LocationTransform(global, local, methods, types)
                    .transform(value)
            }
            is DecafExpression.Constant -> {
                instructions {
                    +Instruction.PushConstant(value.value)
                }
            }
            is DecafExpression.ArithOp -> {
                instructions {
                    +transform(value.op1)
                    +transform(value.op2)
                    +when(value.operator) {
                        Arithmetic.SUB -> Instruction.Sub
                        Arithmetic.ADD -> Instruction.Add
                        Arithmetic.DIV -> Instruction.Div
                        Arithmetic.MOD -> Instruction.Rem
                        Arithmetic.MUl -> Instruction.Mul
                    }
                }
            }
            is DecafExpression.EqOp -> {
                instructions {
                    +transform(value.op1)
                    +transform(value.op2)
                    +when(value.operator) {
                        Equality.EQUAL_TO -> Instruction.Eq
                        Equality.NOT_EQUAL -> Instruction.Neq
                    }
                }
            }
            is DecafExpression.CompOp -> {
                instructions {
                    +transform(value.op1)
                    +transform(value.op2)
                    +when(value.operator) {
                        Comparison.GT -> Instruction.Gt
                        Comparison.GTE -> Instruction.Gte
                        Comparison.LT -> Instruction.Lt
                        Comparison.LTE -> Instruction.Lte
                    }
                }
            }
            is DecafExpression.CondOp -> {
                instructions {
                    +transform(value.op1)
                    +transform(value.op2)
                    +when(value.operator) {
                        Condition.AND -> Instruction.And
                        Condition.OR -> Instruction.Or
                    }
                }
            }
            is DecafExpression.UnaryOp -> {
                instructions {
                    +transform(value.op)
                    +when(value.operator) {
                        Unary.EXCL -> Instruction.Negate
                        Unary.SUB -> Instruction.SubUnary
                    }
                }
            }
        }

        check(instructions.stack() > 0) {
            "stack for expression $value is empty!"
        }

        return instructions
    }

}

class LocationTransform(
    val global: SymbolTable,
    val local: SymbolTable,
    val methods: MethodStore,
    val types: TypeStore
): Transform<DecafExpression.Location, Instruction.Instructions> {

    private val exprTransform = ExpressionTransform(global, local, methods, types)

    override fun transform(value: DecafExpression.Location): Instruction.Instructions {
        val localIndex = local.localSymbolIndex(value.name)

        val (index, isGlobal) = if(0 > localIndex)
            global.localSymbolIndex(value.name, skipGlobals = false) to true
        else
            localIndex to false

        return instructions {
            +loadVar(index, isGlobal)

            if(value is DecafExpression.Location.ArrayLocation) {
                +exprTransform.transform(value.index)
                +Instruction.LoadArray
            }

            if(value.subLocation != null && value.context != null) {
               +subLocation(value.subLocation, value.context)
            }

        }
    }

    private fun subLocation(location: DecafExpression.Location, context: Type.Struct): Instruction.Instructions {
        val structContext = types.first { it.name == context.name }
        val fieldIndex = structContext.properties.indexOfFirst { it.name == location.name }

        return instructions {
            +Instruction.LoadField(fieldIndex)

            if(location is DecafExpression.Location.ArrayLocation) {
                +exprTransform.transform(location.index)
                +Instruction.LoadArray
            }

            if(location.subLocation != null && location.context != null) {
                +subLocation(location.subLocation, location.context)
            }

        }

    }

    private fun loadVar(index: Int, isGlobal: Boolean): Instruction =
        if(isGlobal) Instruction.LoadGlobal(index) else Instruction.LoadLocal(index)

}