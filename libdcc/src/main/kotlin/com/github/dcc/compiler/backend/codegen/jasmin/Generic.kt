package com.github.dcc.compiler.backend.codegen.jasmin

import com.github.jasmin.*
import com.github.jasmin.java.Java
import com.github.jasmin.spec.CodeBlockSpec
import com.github.jasmin.spec.MethodSpec

object Generic {

    val constructor: MethodSpec = MethodSpec.constructorBuilder()
        .addModifier(MethodAccessModifier.PUBLIC)
        .limitStack(1)
        .limitLocals(1)
        .addCodeBlock(
            CodeBlockSpec.builder()
                .aload0()
                .invokeSpecial(Java.lang.init, MethodDescriptor(emptyList(), TypeDescriptor.Void))
                .returns()
                .build()
        )
        .build()

    val main: MethodSpec = MethodSpec.builder(null, "main")
        .addModifier(MethodAccessModifier.PUBLIC)
        .addModifier(MethodAccessModifier.STATIC)
        .addReturnType(TypeDescriptor.Void)
        .addArgument(TypeDescriptor.String.asArray())
        .limitStack(0)
        .limitLocals(1)
        .addCodeBlock(
            CodeBlockSpec.builder()
                .invokeStatic(MethodName(ClassName("Program"), "main"), MethodDescriptor(emptyList(), TypeDescriptor.Void))
                .returns()
                .build()
        )
        .build()

}