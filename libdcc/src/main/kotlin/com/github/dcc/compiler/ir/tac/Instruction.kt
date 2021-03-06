package com.github.dcc.compiler.ir.tac

import com.github.dcc.decaf.literals.Literal
import com.github.dcc.decaf.types.Type
import java.lang.StringBuilder
import kotlin.math.max

class InstructionsBuilder {

    private val instructions = mutableListOf<Instruction>()

    operator fun Collection<Instruction>.unaryPlus() {
        instructions.addAll(this@unaryPlus)
    }

    operator fun Iterable<Instruction>.unaryPlus() {
        instructions.addAll(this@unaryPlus)
    }

    operator fun Instruction.unaryPlus() {
        instructions.add(this@unaryPlus)
    }

    fun build() = Instruction
        .Instructions(instructions)

}

fun instructionsOf(vararg instructions: Instruction) = Instruction
    .Instructions(instructions.toList())

fun instructionsOf(collection: Collection<Instruction>) = Instruction
    .Instructions(collection)

fun instructions(init: InstructionsBuilder.() -> Unit) = InstructionsBuilder().apply(init)
    .build()

sealed class Instruction {

    abstract val adds: Int
    abstract val removes: Int
    abstract val requires: Int
    abstract val mnemonic: String

    override fun toString(): String = mnemonic

    class Instructions(
        instructions: Collection<Instruction>
    ): Collection<Instruction> by instructions {

        private val instructions = instructions.toMutableList()

        override fun iterator(): Iterator<Instruction> = instructions.iterator()
        private var stackSize = 1
        private var maxStackSize = 0

        init {
            instructions.forEach {
                stackSize += it.adds - it.removes
                maxStackSize = max(maxStackSize, stackSize)
            }
         }

        fun stack(): Int = stackSize

        fun maxStack(): Int = maxStackSize
        
        fun add(instruction: Instruction) = apply {
            instructions.add(instruction)
        }

        override fun toString(): String = joinToString(separator = "\n"){ it.toString() }
        
    }


    data class LoadLocal(
        val index: Int
    ): Instruction() {
        override val adds = 1
        override val removes = 0
        override val requires = 0
        override val mnemonic = "load"

        override fun toString(): String = "$mnemonic $index"
    }

    data class ILoadLocal(
        val index: Int
    ): Instruction() {
        override val adds = 1
        override val removes = 0
        override val requires = 0
        override val mnemonic = "iload"

        override fun toString(): String = "${mnemonic}_$index"
    }

    data class ALoadLocal(
        val index: Int
    ): Instruction() {
        override val adds = 1
        override val removes = 0
        override val requires = 0
        override val mnemonic = "aload"

        override fun toString(): String = "${mnemonic}_$index"
    }

    data class LoadGlobal(
        val index: Int
    ): Instruction() {
        override val adds = 1
        override val removes = 0
        override val requires = 0
        override val mnemonic = "gload"

        override fun toString(): String = "$mnemonic $index"
    }

    object LoadArray : Instruction() {
        override val adds = 1
        override val removes = 2
        override val requires = 2
        override val mnemonic = "aaload"

        override fun toString(): String = mnemonic
    }

    object ILoadArray : Instruction() {
        override val adds = 1
        override val removes = 2
        override val requires = 2
        override val mnemonic = "iaload"

        override fun toString(): String = mnemonic
    }

    data class LoadField(
        val index: Int,
        val parent: Type.Struct,
    ) : Instruction() {
        override val adds = 1
        override val removes = 1
        override val requires = 1
        override val mnemonic = "getfield"

        override fun toString(): String = "$mnemonic $index"
    }

    data class StoreGlobal(
        val index: Int,
    ): Instruction() {
        override val adds = 0
        override val removes = 1
        override val requires = 1
        override val mnemonic = "gstore"

        override fun toString(): String = "$mnemonic $index"
    }

    data class StoreRef(val index: Int): Instruction() {
        override val adds = 0
        override val removes = 2
        override val requires = 2
        override val mnemonic = "storef"

        override fun toString(): String = "$mnemonic $index"
    }

    data class PutField(
        val index: Int,
        val parent: Type.Struct,
    ): Instruction() {
        override val adds = 0
        override val removes = 2
        override val requires = 2
        override val mnemonic = "putfield"

        override fun toString(): String = "$mnemonic $index $parent"
    }

    object IAStore: Instruction() {
        override val adds = 0
        override val removes = 2
        override val requires = 2
        override val mnemonic = "iastore"

        override fun toString(): String = mnemonic
    }

    object AAStore: Instruction() {
        override val adds = 0
        override val removes = 2
        override val requires = 2
        override val mnemonic = "aastore"

        override fun toString(): String = mnemonic
    }

    data class IStore(val index: Int): Instruction() {
        override val adds = 0
        override val removes = 1
        override val requires = 1
        override val mnemonic = "istore"

        override fun toString(): String = "${mnemonic}_$index"
    }

    data class PushConstant(
        val constant: Literal,
    ): Instruction() {
        override val adds = 1
        override val removes = 0
        override val requires = 0
        override val mnemonic = "ldc"

        override fun toString(): String = "$mnemonic $constant"
    }

    data class MethodCall(
        val index: Int,
        val parameterCount: Int,
    ): Instruction() {
        override val mnemonic: String = "invoke"
        override val adds: Int = 1
        override val requires: Int = parameterCount
        override val removes: Int = parameterCount

        override fun toString(): String = "$mnemonic $index"
    }

    object Mul : Instruction() {
        override val mnemonic: String = "mul"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Div : Instruction() {
        override val mnemonic: String = "div"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Rem : Instruction() {
        override val mnemonic: String = "rem"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Add : Instruction() {
        override val mnemonic: String = "add"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Sub : Instruction() {
        override val mnemonic: String = "sub"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Eq : Instruction() {
        override val mnemonic: String = "eq"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }
    object Neq : Instruction() {
        override val mnemonic: String = "neq"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Gt : Instruction() {
        override val mnemonic: String = "gt"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Lt : Instruction() {
        override val mnemonic: String = "lt"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Gte : Instruction() {
        override val mnemonic: String = "gte"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Lte : Instruction() {
        override val mnemonic: String = "lte"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object And : Instruction() {
        override val mnemonic: String = "and"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object Or : Instruction() {
        override val mnemonic: String = "or"
        override val adds: Int = 1
        override val requires: Int = 2
        override val removes: Int = 2
    }

    object SubUnary : Instruction() {
        override val mnemonic: String = "subu"
        override val adds: Int = 1
        override val requires: Int = 1
        override val removes: Int = 1
    }

    object Negate : Instruction() {
        override val mnemonic: String = "neg"
        override val adds: Int = 1
        override val requires: Int = 1
        override val removes: Int = 1
    }

    object Return : Instruction() {
        override val mnemonic: String = "return"
        override val adds: Int = 0
        override val requires: Int = 1
        override val removes: Int = 1
    }

    object IReturn : Instruction() {
        override val mnemonic: String = "ireturn"
        override val adds: Int = 0
        override val requires: Int = 1
        override val removes: Int = 1
    }

    object AReturn : Instruction() {
        override val mnemonic: String = "areturn"
        override val adds: Int = 0
        override val requires: Int = 1
        override val removes: Int = 1
    }

    data class If(val branchLabel: String) : Instruction() {
        override val mnemonic: String = "if"
        override val adds: Int = 0
        override val requires: Int = 1
        override val removes: Int = 1

        override fun toString(): String = "$mnemonic $branchLabel"
    }

    data class Goto(val branchLabel: String) : Instruction() {
        override val mnemonic: String = "goto"
        override val adds: Int = 0
        override val requires: Int = 0
        override val removes: Int = 0

        override fun toString(): String = "$mnemonic $branchLabel"
    }

    data class LabeledBlock(
        val label: String,
        val instruction: Instructions,
    ) : Instruction() {
        override val mnemonic: String = label
        override val adds: Int = 0
        override val requires: Int = 0
        override val removes: Int = 0

        override fun toString(): String = StringBuilder()
            .appendLine("$mnemonic:")
            .apply {
                append(instruction.joinToString(separator = "\n") { it.toString() })
            }
            .toString()
    }

    data class NewVar(
        val index: Int,
        val type: Type
    ) : Instruction() {
        override val mnemonic: String = "init"
        override val adds: Int = 0
        override val requires: Int = 0
        override val removes: Int = 0

        override fun toString(): String = "$mnemonic $index"
    }
}
