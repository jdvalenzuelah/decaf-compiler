package com.github.dcc.compiler.ir.tac

import com.github.dcc.decaf.literals.Literal

class InstructionsVBuilder {

    private val instructions = mutableListOf<InstructionV>()

    operator fun Collection<InstructionV>.unaryPlus() {
        instructions.addAll(this)
    }

    operator fun Iterable<InstructionV>.unaryPlus() {
        instructions.addAll(this)
    }

    operator fun InstructionV.unaryPlus() {
        instructions.add(this)
    }

    fun build() = InstructionV
        .Instructions(instructions)

}

fun instructionsOf(vararg instructions: InstructionV) = InstructionV
    .Instructions(instructions.toList())

fun instructionsOf(collection: Collection<InstructionV>) = InstructionV
    .Instructions(collection)

fun instructions(init: InstructionsVBuilder.() -> Unit) = InstructionsVBuilder().apply(init)
    .build()

sealed class InstructionV {

    abstract val adds: Int
    abstract val removes: Int
    abstract val requires: Int
    abstract val mnemonic: String

    override fun toString(): String = mnemonic

    class Instructions(private val instructions: Collection<InstructionV>): Iterable<InstructionV> {

        override fun iterator(): Iterator<InstructionV> = instructions.iterator()
        private var stackSize = 0

        init {
            instructions.forEach {
                stackSize += it.adds
                stackSize -+ it.removes
            }
        }

        fun stack(): Int = stackSize
    }


    data class LoadLocal(
        val index: Int
    ): InstructionV() {
        override val adds = 1
        override val removes = 0
        override val requires = 0
        override val mnemonic = "load"

        override fun toString(): String = "$mnemonic $index"
    }

    data class StoreLocal(
        val index: Int,
    ): InstructionV() {
        override val adds = 0
        override val removes = 1
        override val requires = 1
        override val mnemonic = "store"

        override fun toString(): String = "$mnemonic $index"
    }

    data class PushConstant(
        val constant: Literal,
    ): InstructionV() {
        override val adds = 1
        override val removes = 0
        override val requires = 0
        override val mnemonic = "ldc"

        override fun toString(): String = "$mnemonic $constant"
    }

    data class MethodCall(
        val index: Int,
        val parameterCount: Int,
    ): InstructionV() {
        override val mnemonic: String = "invoke"
        override val adds: Int = 1
        override val requires: Int = parameterCount
        override val removes: Int = parameterCount

        override fun toString(): String = "$mnemonic $index"
    }

    object Mul : InstructionV() {
        override val mnemonic: String = "mul"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Div : InstructionV() {
        override val mnemonic: String = "div"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Rem : InstructionV() {
        override val mnemonic: String = "rem"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Add : InstructionV() {
        override val mnemonic: String = "add"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Sub : InstructionV() {
        override val mnemonic: String = "sub"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Eq : InstructionV() {
        override val mnemonic: String = "eq"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }
    object Neq : InstructionV() {
        override val mnemonic: String = "neq"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Gt : InstructionV() {
        override val mnemonic: String = "gt"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Lt : InstructionV() {
        override val mnemonic: String = "lt"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Gte : InstructionV() {
        override val mnemonic: String = "gte"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Lte : InstructionV() {
        override val mnemonic: String = "lte"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object And : InstructionV() {
        override val mnemonic: String = "and"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Or : InstructionV() {
        override val mnemonic: String = "or"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object SubUnary : InstructionV() {
        override val mnemonic: String = "subu"
        override val adds: Int = 1
        override val requires: Int = 1
        override val removes: Int = 1
    }

    object Negate : InstructionV() {
        override val mnemonic: String = "neg"
        override val adds: Int = 1
        override val requires: Int = 1
        override val removes: Int = 1
    }

    object Return : InstructionV() {
        override val mnemonic: String = "return"
        override val adds: Int = 0
        override val requires: Int = 1
        override val removes: Int = 1
    }

}