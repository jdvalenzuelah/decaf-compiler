package com.github.dcc.compiler.backend.codegen.bytecode

import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.types.Type
import jdk.internal.org.objectweb.asm.ClassWriter
import jdk.internal.org.objectweb.asm.FieldVisitor
import jdk.internal.org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

internal fun Type.toJvmType(): org.objectweb.asm.Type = when(this) {
    is Type.Char -> org.objectweb.asm.Type.getObjectType("java/lang/String")
    is Type.Struct -> org.objectweb.asm.Type.getObjectType(name)
    is Type.Int -> org.objectweb.asm.Type.INT_TYPE
    is Type.Void -> org.objectweb.asm.Type.VOID_TYPE
    is Type.Boolean -> org.objectweb.asm.Type.BOOLEAN_TYPE
    is Type.ArrayUnknownSize -> org.objectweb.asm.Type.getType("[${type.toJvmType().descriptor}")
    is Type.Array -> org.objectweb.asm.Type.getType("[${type.toJvmType().descriptor}")
    is Type.Nothing -> error("Ilegal type $this")
}

internal fun Declaration.Method.Signature.descriptor(returnType: Type): String =
    "(${parametersType.joinToString(separator = "") { it.toJvmType().descriptor }})${returnType.toJvmType().descriptor}"

internal fun ClassWriter.visitDecafMethod(
    signature: Declaration.Method.Signature,
    type: Type,
) = visitMethod(ACC_PUBLIC + ACC_STATIC, signature.name, signature.descriptor(type), null, null)

internal fun ClassWriter.visitDecafField(
    name: String,
    type: Type,
    isStatic: Boolean = true
): FieldVisitor {
    val acc = if(isStatic) ACC_PUBLIC + ACC_STATIC else ACC_PUBLIC
    return visitField(acc, name, type.toJvmType().descriptor, null, null)
}

internal fun MethodVisitor.visitDecafMethodInvoke(
    signature: Declaration.Method.Signature,
    type: Type,
) = visitMethodInsn(INVOKESTATIC, "Program", signature.name, signature.descriptor(type), false)
