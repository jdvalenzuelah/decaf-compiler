package com.github.jasmin.spec.builder

import com.github.jasmin.*
import com.github.jasmin.spec.CodeBlockSpec
import com.github.jasmin.spec.FieldSpec

class CodeBlockSpecBuilder {

    private val instructions = mutableListOf<Instruction>()

    fun aload0() = apply { instructions.add(NoParams.aload_0) }

    fun invokeNonVirtual(methodName: MethodName, descriptor: MethodDescriptor) = apply {
        instructions.add(WithParams.invokenonvirtual(methodName, descriptor))
    }

    fun getStatic(parent: ClassName, fieldSpec: FieldSpec) = apply {
        instructions.add(WithParams.getstatic(parent, fieldSpec.name, fieldSpec.type))
    }

    fun ldc(constant: Constant) = apply {
        instructions.add(WithParams.ldc(constant))
    }

    fun invokeVirtual(methodName: MethodName, descriptor: MethodDescriptor) = apply {
        instructions.add(WithParams.invokevirtual(methodName, descriptor))
    }

    fun returns() = apply {
        instructions.add(NoParams._return)
    }

    fun build() = CodeBlockSpec(instructions)

}