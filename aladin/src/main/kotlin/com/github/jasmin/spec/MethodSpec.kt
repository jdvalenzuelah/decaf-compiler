package com.github.jasmin.spec

import com.github.jasmin.*
import com.github.jasmin.java.Java
import com.github.jasmin.serialize.SerializeMethod
import com.github.jasmin.spec.builder.MethodSpecBuilder

data class MethodSpec(
    val accessSpec: Set<MethodAccessModifier>,
    val name: MethodName,
    val descriptor: MethodDescriptor,
    val statements: List<CodeBlockSpec>,
    val stackLimit: Int?,
    val localsLimit: Int?,
): JasminElement {
    companion object {
        fun builder(parent: ClassName?, name: String) = MethodSpecBuilder(parent, name)

        fun constructorBuilder()  = MethodSpecBuilder(null, Java.constructor)
            .addReturnType(TypeDescriptor.Void)

    }

    override val serialize: String
        get() = SerializeMethod(this)
}