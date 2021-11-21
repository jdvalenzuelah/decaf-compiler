package com.github.dcc.compiler.backend.codegen.bytecode

import com.github.dcc.decaf.DecafStdLib
import com.github.dcc.decaf.symbols.StdLib
import jdk.internal.org.objectweb.asm.ClassWriter
import jdk.internal.org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

class StdLib(private val cw: ClassWriter) : DecafStdLib<MethodVisitor> {

    override val InputInt: MethodVisitor
        get() {
            return cw.visitDecafMethod(StdLib.InputInt.signature, StdLib.InputInt.type).apply {
                visitTypeInsn(NEW, "java/util/Scanner")
                visitInsn(DUP)
                visitFieldInsn(GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;")
                visitMethodInsn(INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false)
                visitVarInsn(ASTORE, 0)
                visitVarInsn(ALOAD, 0)
                visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I", false)
                visitInsn(IRETURN)
                visitMaxs(3, 2)
            }
        }

    override val OutputInt: MethodVisitor
        get() {
            return cw.visitDecafMethod(StdLib.OutputInt.signature, StdLib.OutputInt.type).apply {
                visitFieldInsn(GETSTATIC, "java/lang/System", "out",  "Ljava/io/PrintStream;")
                visitVarInsn(ILOAD, 0)
                visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false)
                visitInsn(RETURN)
                visitMaxs(2, 2)
            }
        }

}