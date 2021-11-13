package com.github.jasmin.spec.builder

import com.github.jasmin.*
import com.github.jasmin.spec.CodeBlockSpec
import com.github.jasmin.spec.FieldSpec

class CodeBlockSpecBuilder {

    private val instructions = mutableListOf<Instruction>()
    private var labelCount = 1

    fun nextLabel(): String {
        val label = "Label$labelCount"
        labelCount++
        return label
    }

    fun aload0() = apply { instructions.add(NoParams.aload_0) }

    fun aload(index: Int) = apply { instructions.add(WithParams.aload(index)) }

    fun iaload() = apply { instructions.add(NoParams.iaload) }

    fun iload0() = apply { instructions.add(NoParams.iload_0) }

    fun iload(index: Int) = apply { instructions.add(WithParams.iload(index)) }

    fun istore(index: Int) = apply { instructions.add(WithParams.istore(index)) }

    fun astore0() = apply { instructions.add(NoParams.astore_0) }

    fun iconst_m1 () = apply { instructions.add(NoParams.iconst_m1 ) }

    fun astore(index: Int) = apply { instructions.add(WithParams.astore(index)) }

    fun iadd() = apply { instructions.add(NoParams.iadd) }

    fun isub() = apply { instructions.add(NoParams.isub) }

    fun idiv() = apply { instructions.add(NoParams.idiv) }

    fun imul() = apply { instructions.add(NoParams.imul) }

    fun iastore() = apply { instructions.add(NoParams.iastore) }

    fun aastore() = apply { instructions.add(NoParams.aastore) }

    fun aaload() = apply { instructions.add(NoParams.aaload) }

    fun irem() = apply { instructions.add(NoParams.irem) }

    fun pop() = apply { instructions.add(NoParams.pop) }

    fun label(label: String) = apply {
        instructions.add(WithParams.label(label))
        labelCount++
    }

    fun if_icmpge(label: String) = apply {
        instructions.add(WithParams.if_icmpge(label))
    }

    fun if_icmple(label: String) = apply {
        instructions.add(WithParams.if_icmple(label))
    }

    fun if_icmplt(label: String) = apply {
        instructions.add(WithParams.if_icmplt(label))
    }

    fun if_icmpgt(label: String) = apply {
        instructions.add(WithParams.if_icmpgt(label))
    }

    fun if_icmpne(label: String) = apply {
        instructions.add(WithParams.if_icmpne(label))
    }

    fun if_icmpeq(label: String) = apply {
        instructions.add(WithParams.if_icmpeq(label))
    }

    fun ifne(label: String) = apply {
        instructions.add(WithParams.ifne(label))
    }

    fun ifeq(label: String) = apply {
        instructions.add(WithParams.ifeq(label))
    }

    fun goto(label: String) = apply { instructions.add(WithParams.goto(label)) }

    fun invokeNonVirtual(methodName: MethodName, descriptor: MethodDescriptor) = apply {
        instructions.add(WithParams.invokenonvirtual(methodName, descriptor))
    }

    fun invokeStatic(methodName: MethodName, descriptor: MethodDescriptor) = apply {
        instructions.add(WithParams.invokestatic(methodName, descriptor))
    }

    fun getStatic(parent: ClassName, fieldSpec: FieldSpec) = apply {
        instructions.add(WithParams.getstatic(parent, fieldSpec.name, fieldSpec.type))
    }

    fun putStatic(parent: ClassName, fieldSpec: FieldSpec) = apply {
        instructions.add(WithParams.putstatic(parent, fieldSpec.name, fieldSpec.type))
    }

    fun putField(parent: ClassName, fieldSpec: FieldSpec) = apply {
        instructions.add(WithParams.putfield(parent, fieldSpec.name, fieldSpec.type))
    }

    fun getField(parent: ClassName, fieldSpec: FieldSpec) = apply {
        instructions.add(WithParams.getfield(parent, fieldSpec.name, fieldSpec.type))
    }

    fun ldc(constant: Constant) = apply {
        instructions.add(WithParams.ldc(constant))
    }

    fun newarray(type: TypeDescriptor) = apply {
        instructions.add(WithParams.newarray(type))
    }

    fun multianewarray(type: TypeDescriptor, dimensions: Int) = apply {
        instructions.add(WithParams.multianewarray(type, dimensions))
    }

    fun anewarray(type: TypeDescriptor) = apply {
        instructions.add(WithParams.anewarray(type))
    }

    fun invokeVirtual(methodName: MethodName, descriptor: MethodDescriptor) = apply {
        instructions.add(WithParams.invokevirtual(methodName, descriptor))
    }

    fun invokeSpecial(methodName: MethodName, descriptor: MethodDescriptor) = apply {
        instructions.add(WithParams.invokespecial(methodName, descriptor))
    }

    fun returns() = apply {
        instructions.add(NoParams._return)
    }

    fun ireturn() = apply {
        instructions.add(NoParams.ireturn)
    }

    fun areturn() = apply {
        instructions.add(NoParams.areturn)
    }

    fun new(cls: ClassName) = apply { instructions.add(WithParams.new(cls)) }

    fun dup() = apply { instructions.add(NoParams.dup) }

    fun build() = CodeBlockSpec(instructions)

}