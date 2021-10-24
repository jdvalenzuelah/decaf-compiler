package com.github.dcc.compiler.backend.codegen.jasmin

import com.github.dcc.compiler.backend.Backend
import com.github.dcc.compiler.ir.Program
import com.github.dcc.compiler.ir.tac.Instruction
import com.github.dcc.decaf.literals.Literal
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.symbols.MethodStore
import com.github.dcc.decaf.symbols.SymbolStore
import com.github.dcc.decaf.types.Type
import com.github.jasmin.*
import com.github.jasmin.java.Java
import com.github.jasmin.spec.ClassSpec
import com.github.jasmin.spec.CodeBlockSpec
import com.github.jasmin.spec.FieldSpec
import com.github.jasmin.spec.MethodSpec
import com.github.jasmin.spec.builder.CodeBlockSpecBuilder


class JasminGenerator : Backend<ClassSpec> {

    override fun compile(program: Program): ClassSpec {
        val classSpec = ClassSpec.builder("Program")
            .superClass(Java.lang.Object)
            .addModifier(ClassAccessModifiers.PUBLIC)
            .addMethod(Generic.constructor)
            .addMethod(Generic.main)
            .addMethod(StdLib.InputInt)
            .addMethod(StdLib.OutputInt)




        if(program.symbols.symbolTable.symbols.isNotEmpty()) {
            val initializer = MethodSpec.classInitializerBuilder()
                .limitStack(1)

            val initializerCode = CodeBlockSpec.builder()
            program.symbols.symbolTable.symbols.forEach {
                classSpec.addField(field(it))
                fieldInitializer(it, initializerCode)
            }

            initializer.addCodeBlock(
                initializerCode
                    .returns()
                    .build()
            )

            classSpec.addMethod(initializer.build())
        }

        program.methods.forEach {
            classSpec.addMethod(method(program.symbols.methods, program.symbols.symbolTable.symbols, it))
        }

        return classSpec.build()
    }


    private fun field(variable: Declaration.Variable): FieldSpec {
        return FieldSpec.builder(variable.name, getTypeDescriptor(variable.type))
            .addModifier(FieldAccessModifiers.STATIC)
            .build()
    }

    private fun fieldInitializer(variable: Declaration.Variable, codeSpec: CodeBlockSpecBuilder): CodeBlockSpecBuilder {
        when(variable.type) {
            is Type.Array -> {
                codeSpec.ldc(Constant.Int(variable.type.size))
                codeSpec.newarray(getTypeDescriptor(variable.type.type))
                codeSpec.putStatic(ClassName("Program"), FieldSpec(emptySet(), variable.name, getTypeDescriptor(variable.type)))
            }
        }
        return codeSpec
    }

    private fun method(_methods: MethodStore, globals: SymbolStore, method: Program.Method): MethodSpec {

        val methods = _methods.toList()
        val methodDecl = methods[method.index]

        val methodSpec = MethodSpec.builder(null, methodDecl.name)
            .addModifier(MethodAccessModifier.PUBLIC)
            .addModifier(MethodAccessModifier.STATIC)
            .addReturnType(getTypeDescriptor(methodDecl.type))
            .limitStack(method.body.maxStack())
            .limitLocals(method.symbols.allSymbols().size)

        methodDecl.signature.parametersType.forEach {
            methodSpec.addArgument(getTypeDescriptor(it))
        }

        val codeSpec = CodeBlockSpec.builder()
        method.body.forEach { instruction ->
            addInstruction(codeSpec, methods, globals, instruction)
        }

        return methodSpec
            .addCodeBlock(codeSpec.build())
            .build()
    }

    private fun addInstruction(codeSpec: CodeBlockSpecBuilder, _methods: MethodStore, _globals: SymbolStore, instruction: Instruction): CodeBlockSpecBuilder {
        val methods = _methods.toList()
        val globals = _globals.toList()
        when(instruction) {
            is Instruction.PushConstant -> {
                val const = when(instruction.constant) {
                    is Literal.Int -> Constant.Int(instruction.constant.value)
                    is Literal.Char -> Constant.Str(instruction.constant.value)
                    is Literal.Boolean -> Constant.Boolean(instruction.constant.value)
                }
                codeSpec.ldc(const)
            }
            is Instruction.LabeledBlock -> {
                codeSpec.label(instruction.label)
                instruction.instruction.forEach { addInstruction(codeSpec, methods, globals, it) }
            }
            is Instruction.Goto -> codeSpec.goto(instruction.branchLabel)
            is Instruction.Lt -> {
                val label = codeSpec.nextLabel()
                val successLabel = codeSpec.nextLabel()
                codeSpec.if_icmpge(label)
                codeSpec.ldc(Constant.Int(1))
                codeSpec.goto(successLabel)
                codeSpec.label(label)
                codeSpec.ldc(Constant.Int(0))
                codeSpec.label(successLabel)
            }
            is Instruction.Lte -> {
                val label = codeSpec.nextLabel()
                val successLabel = codeSpec.nextLabel()
                codeSpec.if_icmpgt(label)
                codeSpec.ldc(Constant.Int(1))
                codeSpec.goto(successLabel)
                codeSpec.label(label)
                codeSpec.ldc(Constant.Int(0))
                codeSpec.label(successLabel)
            }
            is Instruction.Gt -> {
                val label = codeSpec.nextLabel()
                val successLabel = codeSpec.nextLabel()
                codeSpec.if_icmple(label)
                codeSpec.ldc(Constant.Int(1))
                codeSpec.goto(successLabel)
                codeSpec.label(label)
                codeSpec.ldc(Constant.Int(0))
                codeSpec.label(successLabel)
            }
            is Instruction.Gte -> {
                val label = codeSpec.nextLabel()
                val successLabel = codeSpec.nextLabel()
                codeSpec.if_icmplt(label)
                codeSpec.ldc(Constant.Int(1))
                codeSpec.goto(successLabel)
                codeSpec.label(label)
                codeSpec.ldc(Constant.Int(0))
                codeSpec.label(successLabel)
            }
            is Instruction.Eq -> {
                val label = codeSpec.nextLabel()
                val successLabel = codeSpec.nextLabel()
                codeSpec.if_icmpne(label)
                codeSpec.ldc(Constant.Int(1))
                codeSpec.goto(successLabel)
                codeSpec.label(label)
                codeSpec.ldc(Constant.Int(0))
                codeSpec.label(successLabel)
            }
            is Instruction.Neq -> {
                val label = codeSpec.nextLabel()
                val successLabel = codeSpec.nextLabel()
                codeSpec.if_icmpeq(label)
                codeSpec.ldc(Constant.Int(1))
                codeSpec.goto(successLabel)
                codeSpec.label(label)
                codeSpec.ldc(Constant.Int(0))
                codeSpec.label(successLabel)
            }
            is Instruction.Negate -> {
                val label = codeSpec.nextLabel()
                val successLabel = codeSpec.nextLabel()
                codeSpec.ifne(label)
                codeSpec.ldc(Constant.Int(1))
                codeSpec.goto(successLabel)
                codeSpec.label(label)
                codeSpec.ldc(Constant.Int(0))
                codeSpec.label(successLabel)
            }
            is Instruction.And -> {
                val label = codeSpec.nextLabel()
                val successLabel = codeSpec.nextLabel()
                codeSpec.ifeq(label)
                codeSpec.ifeq(label)
                codeSpec.ldc(Constant.Int(1))
                codeSpec.goto(successLabel)
                codeSpec.label(label)
                codeSpec.ldc(Constant.Int(0))
                codeSpec.label(successLabel)
            }
            Instruction.Or -> {
                //TODO: This might be incorrect
                val label1 = codeSpec.nextLabel()
                val label0 = codeSpec.nextLabel()
                val successLabel = codeSpec.nextLabel()
                codeSpec.ifne(label1)
                codeSpec.ifeq(label0)
                codeSpec.label(label1)
                codeSpec.ldc(Constant.Int(1))
                codeSpec.goto(successLabel)
                codeSpec.label(label0)
                codeSpec.ldc(Constant.Int(0))
            }
            is Instruction.LoadGlobal -> {
                val field = globals[instruction.index]
                codeSpec.getStatic(ClassName("Program"), FieldSpec(emptySet(), field.name, getTypeDescriptor(field.type)))
            }
            is Instruction.MethodCall -> {
                val method = methods[instruction.index]
                codeSpec.invokeStatic(
                    MethodName(ClassName("Program"), method.name),
                    MethodDescriptor(
                        method.signature.parametersType.map { getTypeDescriptor(it) },
                        getTypeDescriptor(method.type)
                    )
                )
            }
            is Instruction.If -> codeSpec.ifne(instruction.branchLabel)
            is Instruction.Add -> codeSpec.iadd()
            is Instruction.Sub -> codeSpec.isub()
            is Instruction.Div -> codeSpec.idiv()
            is Instruction.Mul -> codeSpec.imul()
            is Instruction.Pop -> codeSpec.pop()
            is Instruction.Rem -> codeSpec.irem()
            is Instruction.ILoadLocal -> codeSpec.iload(instruction.index)
            is Instruction.IStore -> codeSpec.istore(instruction.index)
            is Instruction.IAStore -> codeSpec.iastore()
            is Instruction.IReturn -> codeSpec.ireturn()
            is Instruction.Return -> codeSpec.returns()
            is Instruction.ILoadArray -> codeSpec.iaload()
            is Instruction.LoadArray -> {}
            is Instruction.LoadField -> error("Not implemented")
            is Instruction.LoadLocal -> error("Not implemented")
            is Instruction.StoreGlobal -> error("Not implemented")
            is Instruction.StoreLocal -> error("Not implemented")
            is Instruction.StoreRef -> error("Not implemented")
            is Instruction.SubUnary -> error("Not implemented")
        }
        return codeSpec
    }

    private fun getTypeDescriptor(type: Type): TypeDescriptor {
        return when(type) {
            is Type.Int -> TypeDescriptor.Integer
            is Type.Boolean -> TypeDescriptor.Boolean
            is Type.Char -> TypeDescriptor.String
            is Type.Void, is Type.Nothing -> TypeDescriptor.Void
            is Type.Array -> getTypeDescriptor(type.type).asArray()
            is Type.ArrayUnknownSize -> getTypeDescriptor(type.type).asArray()
            is Type.Struct -> TypeDescriptor.Class(ClassName(type.name))
        }
    }

}