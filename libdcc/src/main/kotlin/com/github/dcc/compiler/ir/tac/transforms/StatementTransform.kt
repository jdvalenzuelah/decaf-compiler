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
            body = instructionsOf(value.block.statements.flatMap { statementTransform.transform(it) }),
            symbols = value.symbols
        )

    }

}

private interface LabelingStrategy {

    val lastLabel: Int

    fun getNextLabelAndIncrease(): String

    fun getNextLabel(): String

    fun getLabel(index: Int): String
}

private class AlphaNumericLabelingStrategy(init: Int = 0) : LabelingStrategy {

    private val prefix = ('A'..'Z').toList()[init]

    private var lastLabelIndex: Int = 0

    override val lastLabel: Int
        get() = lastLabelIndex

    override fun getLabel(index: Int): String = "_L$prefix$index"

    override fun getNextLabel(): String = getLabel(lastLabel)

    override fun getNextLabelAndIncrease(): String {
        val label = getNextLabel()
        lastLabelIndex++
        return label
    }
}

class StatementTransform(
    val global: SymbolTable,
    val local: SymbolTable,
    val methods: MethodStore,
    val types: TypeStore,
    private val depth: Int = 0,
): Transform<DecafStatement, Instruction.Instructions> {

    private val exprTransform = ExpressionTransform(global, local, methods, types)
    private val locationTransform = LocationTransform(global, local, methods, types)

    private val labelStrategy: LabelingStrategy = AlphaNumericLabelingStrategy(depth)

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
                    getLabel(labelStrategy.lastLabel+1)
                } else getNextLabel()
                instructions {
                    +exprTransform.transform(value.condition)
                    +Instruction.If(value.ifBlock.label)

                    if(value.elseBlock != null) {
                        +StatementTransform(global, value.elseBlock.scope, methods, types, depth + 1)
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
                        instruction = StatementTransform(global, value.ifBlock.scope, methods, types, depth + 1)
                            .transform(value.ifBlock.statements)
                    )

                    nextNeedsLabel()
                }
            }
            is DecafStatement.While -> {
                val condLabel = "${value.block.label}#condition"
                val bodyLabel = "${value.block.label}#body"
                val nextLabel = if(needsToBeLabeled) {
                    getLabel(labelStrategy.lastLabel+1)
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
                        instruction = StatementTransform(global, value.block.scope, methods, types, depth + 1)
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
                    value.expression?.also {
                        +exprTransform.transform(it)
                    }

                    if(value.expression?.type is Type.Int)
                        +Instruction.IReturn
                    else
                        +Instruction.Return
                }
            }
            is DecafStatement.Assignment -> {
                instructions {

                    val (index, isGlobal) = locationTransform.getIndexAndGlob(value.location.name)

                    if(value.location.type == Type.Int && value.location !is DecafExpression.Location.ArrayLocation
                        && value.location.subLocation == null && value.location.context == null && !isGlobal) {
                        locationTransform.disableLoad()
                        +exprTransform.transform(value.expression)
                        locationTransform.enableLoad()
                        +Instruction.IStore(index)
                    } else {
                        locationTransform.disableLoad()
                        +locationTransform.transform(value.location)
                        locationTransform.enableLoad()
                        +exprTransform.transform(value.expression)



                        if(value.location.context == null && value.location.subLocation == null) {
                            +if(!isGlobal) {
                                when(value.expression.type) {
                                    is Type.Int, is Type.Boolean -> {
                                        when(value.location) {
                                            is DecafExpression.Location.ArrayLocation -> Instruction.IAStore
                                            is DecafExpression.Location.VarLocation -> Instruction.IStore(index)
                                        }
                                    }
                                    is Type.Struct, is Type.Char, is Type.ArrayUnknownSize, is Type.Array-> {
                                        when(value.location) {
                                            is DecafExpression.Location.ArrayLocation -> Instruction.AAStore
                                            is DecafExpression.Location.VarLocation -> Instruction.StoreRef(index)
                                        }
                                    }
                                    is Type.Nothing, is Type.Void -> error("Ilegal type $value")
                                }
                            } else {
                                Instruction.StoreGlobal(index)
                            }
                        } else {
                            val lastContext = value.location.flatten().last { it.context != null }.context!!
                            val lastLocation = value.location.flatten().last()
                            val fieldIndex = locationTransform.getFieldIndex(lastLocation, lastContext)
                            +Instruction.PutField(fieldIndex, lastContext)
                        }

                    }

                }
            }
            is DecafStatement.Expression -> {
                instructions {
                    +exprTransform.transform(value.expression)
                }
            }
            is DecafStatement.VarDecl -> {
                val (index, _) = locationTransform.getIndexAndGlob(value.name)
                instructions {
                    +Instruction.NewVar(index, value.type)
                }
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

    private fun getNextLabelAndIncrease(): String = labelStrategy.getNextLabelAndIncrease()

    private fun getNextLabel(): String = labelStrategy.getNextLabel()

    private fun getLabel(index: Int) = labelStrategy.getLabel(index)
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

    private var load = true

    fun enableLoad() { load = true }

    fun disableLoad() { load = false }

    fun compareAndSet(expected: Boolean, value: Boolean) { if(load == expected) load = value }

    override fun transform(value: DecafExpression.Location): Instruction.Instructions {
        val (index, isGlobal) = getIndexAndGlob(value.name)
        return instructions {
            if(value !is DecafExpression.Location.ArrayLocation
                && value.subLocation == null && value.context == null && !isGlobal) {
                +when(value.type) {
                    is Type.Int, Type.Boolean -> Instruction.ILoadLocal(index)
                    is Type.Struct, is Type.Char,
                    is Type.Array, is Type.ArrayUnknownSize -> Instruction.ALoadLocal(index)
                    is Type.Nothing, is Type.Void -> error("Ilegal type for $value")
                }
            } else {
                +loadVar(index, isGlobal, value.type)

                if(value is DecafExpression.Location.ArrayLocation) {
                    +exprTransform.transform(value.index)
                    when {
                        (value.type is Type.Int || value.type is Type.Boolean) && load -> +Instruction.ILoadArray
                        value.type is Type.Struct && value.context != null -> +Instruction.LoadArray
                    }
                }

                if(value.subLocation != null && value.context != null) {
                    +subLocation(value.subLocation, value.context)
                }
            }

        }
    }

    fun getIndexAndGlob(name: String): Pair<Int, Boolean> {
        val localIndex = local.localSymbolIndex(name)
        return if(0 > localIndex)
            global.localSymbolIndex(name, skipGlobals = false) to true
        else
            localIndex to false
    }

    private fun subLocation(location: DecafExpression.Location, context: Type.Struct): Instruction.Instructions {
        val fieldIndex = getFieldIndex(location, context)

        return instructions {
            if(load || (location.subLocation != null && location.context != null))
                +Instruction.LoadField(fieldIndex, context)

            if(location is DecafExpression.Location.ArrayLocation) {
                +exprTransform.transform(location.index)

                when {
                    (location.type is Type.Int || location.type is Type.Boolean) && load -> +Instruction.ILoadArray
                    location.type is Type.Struct && location.context != null -> +Instruction.LoadArray
                }

            }

            if(location.subLocation != null && location.context != null) {
                +subLocation(location.subLocation, location.context)
            }

        }

    }

    fun getFieldIndex(location: DecafExpression.Location, context: Type.Struct): Int {
        val structContext = types.first { it.name == context.name }
        return structContext.properties.indexOfFirst { it.name == location.name }
    }

    private fun loadVar(index: Int, isGlobal: Boolean, type: Type): Instruction {
        return if(isGlobal)
            Instruction.LoadGlobal(index)
        else {
            when(type) {
                is Type.Struct -> Instruction.ALoadLocal(index)
                else -> Instruction.LoadLocal(index)
            }
        }
    }

}