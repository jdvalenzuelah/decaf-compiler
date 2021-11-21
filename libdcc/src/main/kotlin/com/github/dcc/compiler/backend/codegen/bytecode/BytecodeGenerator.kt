package com.github.dcc.compiler.backend.codegen.bytecode

import com.github.dcc.compiler.backend.Backend
import com.github.dcc.compiler.ir.Program
import com.github.dcc.compiler.ir.tac.Instruction
import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.literals.Literal
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.types.Type
import jdk.internal.org.objectweb.asm.ClassWriter
import jdk.internal.org.objectweb.asm.Label
import jdk.internal.org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.*
import java.io.File

internal class BytecodeGenerator private constructor(
    private val program: Program
) {

    private val methods = program.symbols.methods.toList()
    private val globals = program.symbols.symbolTable.symbols.toList()
    private val types = program.symbols.types.toList()
    private val labels = mutableMapOf<String, Label>()

    companion object {
        operator fun invoke(program: Program) = BytecodeGenerator(program)
            .compile()
    }

    private fun compile(): BytecodeProgram {
        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES)

        cw.visit(V1_8, ACC_PUBLIC, "Program", null, "java/lang/Object", null)

        // Constructor
        cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null).apply {
            visitVarInsn(ALOAD, 0)
            visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
            visitInsn(RETURN)
            visitMaxs(1, 1)
            visitEnd()
        }

        StdLib(cw).apply {
            InputInt.visitEnd()
            OutputInt.visitEnd()
        }

        //java main
        cw.visitDecafMethod(
            signature = Declaration.Method.Signature("main", listOf(Type.ArrayUnknownSize(Type.Char))),
            type = Type.Void
        ).apply {
            visitMethodInsn(INVOKESTATIC, "Program", "main", "()V", false)
            visitInsn(RETURN)
            visitMaxs(0, 1)
            visitEnd()
        }

        cw.visitFields()
        cw.clinit()
        cw.visitMethods()

        cw.visitEnd()

        return BytecodeProgram(
            program = BytecodeProgram.ClassFile("Program", cw),
            structs = structs()
        )
    }

    private fun structs(): Collection<BytecodeProgram.ClassFile> = types.map{
        BytecodeProgram.ClassFile(
            name = it.name,
            cw = struct(it)
        )
    }

    private fun struct(struct: Declaration.Struct): ClassWriter {
        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES)
        cw.visit(V1_8, ACC_PUBLIC, struct.name, null, "java/lang/Object", null)

        // Constructor
        cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null).apply {
            visitVarInsn(ALOAD, 0)
            visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)

            struct.properties.forEach {
                cw.visitDecafField(it.name, it.type, isStatic = false)
                fieldInit(it, struct.name)
            }

            visitInsn(RETURN)
            visitMaxs(4, 1)
            visitEnd()
        }

        return cw
    }

    private fun ClassWriter.visitFields() {
        program.symbols.symbolTable.symbols.forEach { field(it) }
    }

    private fun ClassWriter.visitMethods() {
        program.methods.forEach { method(it) }
    }

    private fun ClassWriter.field(field: Declaration.Variable) {
        visitDecafField(field.name, field.type).visitEnd()
    }

    private fun ClassWriter.clinit() {
        if(program.symbols.symbolTable.symbols.isNotEmpty()) {
            val clinit = visitMethod(ACC_STATIC, "<clinit>", "()V", null, null)
            program.symbols.symbolTable.symbols.forEach { clinit.fieldInit(it) }
            clinit.visitInsn(RETURN)
            clinit.visitMaxs(2, 0)
            clinit.visitEnd()
        }
    }

    private fun MethodVisitor.fieldInit(field: Declaration.Parameter, parent: String) =
        fieldInit(field.name, field.type, parent, false)

    private fun MethodVisitor.fieldInit(field: Declaration.Variable, isStatic: Boolean = true) =
        fieldInit(field.name, field.type, isStatic = isStatic)

    private fun MethodVisitor.fieldInit(name: String, type: Type, parent: String = "Program", isStatic: Boolean = true) {
        val ins = if(isStatic) PUTSTATIC else PUTFIELD

        if(!isStatic)
            visitVarInsn(ALOAD, 0)

        when(type) {
            is Type.Int, is Type.Boolean -> {
                visitLdcInsn(0)
                visitFieldInsn(ins, parent, name, type.toJvmType().descriptor)
            }
            is Type.Char -> {
                visitLdcInsn("")
                visitFieldInsn(ins, parent, name, type.toJvmType().descriptor)
            }
            is Type.Struct -> {
                val struct = type.toJvmType()
                visitTypeInsn(NEW, struct.className)
                visitInsn(DUP)
                visitMethodInsn(INVOKESPECIAL, struct.className, "<init>", "()V", false)
                visitFieldInsn(ins, parent, name, type.toJvmType().descriptor)
            }
            is Type.Array -> {
                val dimensions = loadArraySizes(type)
                when(val subType = type.type) {
                    is Type.Int, is Type.Boolean -> visitIntInsn(NEWARRAY, T_INT)
                    is Type.Char -> visitTypeInsn(ANEWARRAY, subType.toJvmType().className)
                    is Type.Array -> visitMultiANewArrayInsn(subType.toJvmType().descriptor, dimensions)
                    is Type.Struct -> {
                        val struct = subType.toJvmType()
                        visitTypeInsn(ANEWARRAY, struct.className)
                        visitFieldInsn(ins, parent, name, type.toJvmType().descriptor)
                        repeat(type.size) { index ->
                            if(!isStatic) {
                                visitVarInsn(ALOAD, 0)
                                visitFieldInsn(GETFIELD, parent, name, type.toJvmType().descriptor)
                            } else
                                visitFieldInsn(GETSTATIC, parent, name, type.toJvmType().descriptor)
                            visitLdcInsn(index)
                            visitTypeInsn(NEW, struct.className)
                            visitInsn(DUP)
                            visitMethodInsn(INVOKESPECIAL, struct.className, "<init>", "()V", false)
                            visitInsn(AASTORE)
                        }
                        return
                    }
                }
                visitFieldInsn(ins, parent, name, type.toJvmType().descriptor)
            }
            is Type.ArrayUnknownSize, is Type.Nothing, is Type.Void -> error("Ilegal type $type")
        }
    }

    private fun MethodVisitor.loadArraySizes(type: Type.Array, dimensions: Int = 1): Int {
        visitLdcInsn(type.size)
        return if(type.type is Type.Array) {
            loadArraySizes(type.type, dimensions+1)
        } else dimensions
    }

    private fun ClassWriter.method(method: Program.Method) {
        val methodDecl = methods[method.index]
        visitDecafMethod(methodDecl.signature, methodDecl.type).apply {
            visitInstructions(method.body)
            visitMaxs(method.body.maxStack(), method.symbols.allSymbols().size)
            visitEnd()
        }
        labels.clear()
    }

    private fun MethodVisitor.visitInstructions(instructions: Instruction.Instructions) {
        instructions.forEach { visitInstruction(it) }
    }

    private fun MethodVisitor.visitInstruction(instruction: Instruction) {
        when(instruction) {
            is Instruction.PushConstant -> {
                val value: Any = when(instruction.constant) {
                    is Literal.Int -> instruction.constant.value
                    is Literal.Char -> instruction.constant.value
                    is Literal.Boolean -> if(instruction.constant.value) 1 else 0
                }
                visitLdcInsn(value)
            }
            is Instruction.LabeledBlock -> {
                val label = getLabel(instruction.label)
                visitLabel(label)
                visitInstructions(instruction.instruction)
            }
            is Instruction.Goto -> {
                val label = getLabel(instruction.branchLabel)
                visitJumpInsn(GOTO, label)
            }
            is Instruction.Lt -> {
                val label = Label()
                val successLabel = Label()
                visitJumpInsn(IF_ICMPGE, label)
                visitLdcInsn(1)
                visitJumpInsn(GOTO, successLabel)
                visitLabel(label)
                visitLdcInsn(0)
                visitLabel(successLabel)
            }
            is Instruction.Lte -> {
                val label = Label()
                val successLabel = Label()
                visitJumpInsn(IF_ICMPGT, label)
                visitLdcInsn(1)
                visitJumpInsn(GOTO, successLabel)
                visitLabel(label)
                visitLdcInsn(0)
                visitLabel(successLabel)
            }
            is Instruction.Gt -> {
                val label = Label()
                val successLabel = Label()
                visitJumpInsn(IF_ICMPLE, label)
                visitLdcInsn(1)
                visitJumpInsn(GOTO, successLabel)
                visitLabel(label)
                visitLdcInsn(0)
                visitLabel(successLabel)
            }
            is Instruction.Gte -> {
                val label = Label()
                val successLabel = Label()
                visitJumpInsn(IF_ICMPLT, label)
                visitLdcInsn(1)
                visitJumpInsn(GOTO, successLabel)
                visitLabel(label)
                visitLdcInsn(0)
                visitLabel(successLabel)
            }
            is Instruction.Eq -> {
                val label = Label()
                val successLabel = Label()
                visitJumpInsn(IF_ICMPNE, label)
                visitLdcInsn(1)
                visitJumpInsn(GOTO, successLabel)
                visitLabel(label)
                visitLdcInsn(0)
                visitLabel(successLabel)
            }
            is Instruction.Neq -> {
                val label = Label()
                val successLabel = Label()
                visitJumpInsn(IF_ICMPEQ, label)
                visitLdcInsn(1)
                visitJumpInsn(GOTO, successLabel)
                visitLabel(label)
                visitLdcInsn(0)
                visitLabel(successLabel)
            }
            is Instruction.Negate -> {
                val label = Label()
                val successLabel = Label()
                visitJumpInsn(IFNE, label)
                visitLdcInsn(1)
                visitJumpInsn(GOTO, successLabel)
                visitLabel(label)
                visitLdcInsn(0)
                visitLabel(successLabel)
            }
            is Instruction.And -> {
                val label = Label()
                val successLabel = Label()
                visitJumpInsn(IFEQ, label)
                visitJumpInsn(IFEQ, label)
                visitLdcInsn(1)
                visitJumpInsn(GOTO, successLabel)
                visitLabel(label)
                visitLdcInsn(0)
                visitLabel(successLabel)
            }
            is Instruction.Or -> {
                val label1 = Label()
                val label0 = Label()
                val successLabel = Label()
                visitJumpInsn(IFNE, label1)
                visitJumpInsn(IFEQ, label0)
                visitLabel(label1)
                visitLdcInsn(1)
                visitJumpInsn(GOTO, successLabel)
                visitLabel(label0)
                visitLdcInsn(0)
                visitLabel(successLabel)
            }
            is Instruction.LoadGlobal -> {
                val field = globals[instruction.index]
                visitFieldInsn(GETSTATIC, "Program", field.name, field.type.toJvmType().descriptor)
            }
            is Instruction.StoreGlobal -> {
                val field = globals[instruction.index]
                if(field.type is Type.Array) {
                    when(field.type.type) {
                        is Type.Int, is Type.Boolean -> visitInsn(IASTORE)
                        else -> visitInsn(AASTORE)
                    }
                } else
                    visitFieldInsn(PUTSTATIC, "Program", field.name, field.type.toJvmType().descriptor)
            }
            is Instruction.MethodCall -> {
                val method = methods[instruction.index]
                visitDecafMethodInvoke(method.signature, method.type)
            }
            is Instruction.PutField -> {
                val prop = types.first { it.name == instruction.parent.name }.properties[instruction.index]
                visitFieldInsn(PUTFIELD, instruction.parent.name, prop.name, prop.type.toJvmType().descriptor)
            }
            is Instruction.LoadField -> {
                val prop = types.first { it.name == instruction.parent.name }.properties[instruction.index]
                visitFieldInsn(GETFIELD, instruction.parent.name, prop.name, prop.type.toJvmType().descriptor)
            }
            is Instruction.If -> visitJumpInsn(IFNE, getLabel(instruction.branchLabel))
            is Instruction.Add -> visitInsn(IADD)
            is Instruction.Sub -> visitInsn(ISUB)
            is Instruction.Div -> visitInsn(IDIV)
            is Instruction.Mul -> visitInsn(IMUL)
            is Instruction.Rem -> visitInsn(IREM)
            is Instruction.ILoadLocal -> visitVarInsn(ILOAD, instruction.index)
            is Instruction.IStore -> visitVarInsn(ISTORE, instruction.index)
            is Instruction.IAStore -> visitInsn(IASTORE)
            is Instruction.AAStore -> visitInsn(AASTORE)
            is Instruction.IReturn -> visitInsn(IRETURN)
            is Instruction.AReturn -> visitInsn(ARETURN)
            is Instruction.Return -> visitInsn(RETURN)
            is Instruction.ILoadArray -> visitInsn(IALOAD)
            is Instruction.ALoadLocal -> visitVarInsn(ALOAD, instruction.index)
            is Instruction.NewVar -> visitNewVar(instruction)
            is Instruction.LoadArray -> visitInsn(AALOAD)
            is Instruction.LoadLocal -> visitVarInsn(ALOAD, instruction.index)
            is Instruction.StoreRef -> visitVarInsn(ASTORE, instruction.index)
            is Instruction.SubUnary -> {
                visitInsn(ICONST_M1)
                visitInsn(IMUL)
            }
        }
    }

    private fun MethodVisitor.visitNewVar(instruction: Instruction.NewVar) {
        when(instruction.type) {
            is Type.Int, is Type.Boolean -> {
                visitLdcInsn(0)
                visitVarInsn(ISTORE, instruction.index)
            }
            is Type.Char -> {
                visitLdcInsn("")
                visitVarInsn(ASTORE, instruction.index)
            }
            is Type.Struct -> {
                val struct = instruction.type.toJvmType()
                visitTypeInsn(NEW, struct.className)
                visitInsn(DUP)
                visitMethodInsn(INVOKESPECIAL, struct.className, "<init>", "()V", false)
                visitVarInsn(ASTORE, instruction.index)
            }
            is Type.Array -> {
                val dimensions = loadArraySizes(instruction.type)
                when(val subType = instruction.type.type) {
                    is Type.Int, is Type.Boolean -> visitIntInsn(NEWARRAY, T_INT)
                    is Type.Char -> visitTypeInsn(ANEWARRAY, subType.toJvmType().className)
                    is Type.Array -> visitMultiANewArrayInsn(subType.toJvmType().descriptor, dimensions)
                    is Type.Struct -> {
                        val struct = subType.toJvmType()
                        visitTypeInsn(ANEWARRAY, struct.className)
                        visitInsn(DUP)
                        repeat(instruction.type.size) { index ->
                            visitLdcInsn(index)
                            visitTypeInsn(NEW, struct.className)
                            visitInsn(DUP)
                            visitMethodInsn(INVOKESPECIAL, struct.className, "<init>", "()V", false)
                            visitInsn(AASTORE)
                            if(instruction.type.size > index)
                                visitInsn(DUP)
                        }
                    }
                }
                visitVarInsn(ASTORE, instruction.index)
            }
            is Type.ArrayUnknownSize, is Type.Nothing, is Type.Void -> error("Ilegal type $instruction")
        }
    }

    private fun getLabel(label: String) = labels.getOrPut(label) { Label() }

}

object JvmByteCodeGenerator: Backend<BytecodeProgram> {
    override fun compile(program: Program): BytecodeProgram = BytecodeGenerator(program)
}