package com.github.dcc.compiler.backend.codegen.jasmin

import com.github.dcc.decaf.DecafStdLib
import com.github.jasmin.MethodAccessModifier
import com.github.jasmin.MethodDescriptor
import com.github.jasmin.MethodName
import com.github.jasmin.TypeDescriptor
import com.github.jasmin.java.Java
import com.github.jasmin.spec.CodeBlockSpec
import com.github.jasmin.spec.MethodSpec
import com.github.jasmin.spec.builder.asType

object StdLib : DecafStdLib<MethodSpec> {

    override val InputInt: MethodSpec = MethodSpec.builder(null, "InputInt")
        .addModifier(MethodAccessModifier.STATIC)
        .addModifier(MethodAccessModifier.PUBLIC)
        .addReturnType(TypeDescriptor.Integer)
        .limitStack(3)
        .limitLocals(1)
        .addCodeBlock(
            CodeBlockSpec.builder()
                .new(Java.util.Scanner)
                .dup()
                .getStatic(Java.lang.System.pckg, Java.lang.System.`in`)
                .invokeSpecial(Java.util.Scanner.init, MethodDescriptor(listOf(Java.io.InputStream.asType()), TypeDescriptor.Void))
                .astore0()
                .aload0()
                .invokeVirtual(MethodName(Java.util.Scanner, "nextInt"), MethodDescriptor(emptyList(), TypeDescriptor.Integer))
                .ireturn()
                .build()
        )
        .build()

    override val OutputInt: MethodSpec = MethodSpec.builder(null, "OutputInt")
        .addModifier(MethodAccessModifier.STATIC)
        .addModifier(MethodAccessModifier.PUBLIC)
        .addReturnType(TypeDescriptor.Void)
        .addArgument(TypeDescriptor.Integer)
        .limitStack(2)
        .limitLocals(1)
        .addCodeBlock(
            CodeBlockSpec.builder()
                .getStatic(Java.lang.System.pckg, Java.lang.System.out)
                .iload0()
                .invokeVirtual(Java.io.println, MethodDescriptor(listOf(TypeDescriptor.Integer), TypeDescriptor.Void))
                .returns()
                .build()
        )
        .build()

}