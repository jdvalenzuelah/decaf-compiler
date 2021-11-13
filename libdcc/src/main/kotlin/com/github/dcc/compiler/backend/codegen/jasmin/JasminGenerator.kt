package com.github.dcc.compiler.backend.codegen.jasmin

import com.github.dcc.compiler.backend.Backend
import com.github.dcc.compiler.ir.Program
import com.github.dcc.compiler.ir.tac.Instruction
import com.github.dcc.decaf.literals.Literal
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.symbols.MethodStore
import com.github.dcc.decaf.symbols.SymbolStore
import com.github.dcc.decaf.symbols.TypeStore
import com.github.dcc.decaf.types.Type
import com.github.jasmin.*
import com.github.jasmin.java.Java
import com.github.jasmin.spec.ClassSpec
import com.github.jasmin.spec.CodeBlockSpec
import com.github.jasmin.spec.FieldSpec
import com.github.jasmin.spec.MethodSpec
import com.github.jasmin.spec.builder.CodeBlockSpecBuilder


object JasminGenerator : Backend<JasminProgramSpec> {

    override fun compile(program: Program): JasminProgramSpec {
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
            classSpec.addMethod(method(program.symbols.methods, program.symbols.symbolTable.symbols, program.symbols.types, it))
        }

        val structs = program.symbols.types.map { struct(it) }

        return JasminProgramSpec(
            program = classSpec.build(),
            structs = structs
        )
    }


    private fun struct(struct: Declaration.Struct): ClassSpec {
        val constructor = MethodSpec.constructorBuilder()
            .addModifier(MethodAccessModifier.PUBLIC)
            .limitStack(1)
            .limitLocals(1)

        val constructorInit = CodeBlockSpec.builder()
            .aload0()
            .invokeSpecial(Java.lang.init, MethodDescriptor(emptyList(), TypeDescriptor.Void))

        return ClassSpec.builder(struct.name)
            .superClass(Java.lang.Object)
            .addModifier(ClassAccessModifiers.PUBLIC)
            .apply {
                struct.properties.forEach { property ->
                    addField(field(property, isStatic = false))
                    fieldInitializer(property, constructorInit, ClassName(struct.name), isStatic = false)
                }
            }
            .addMethod(
                constructor
                    .addCodeBlock(
                        constructorInit
                            .returns()
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private fun field(variable: Declaration.Variable, isStatic: Boolean = true): FieldSpec =
        field(variable.name, variable.type, isStatic)

    private fun field(variable: Declaration.Parameter, isStatic: Boolean = true): FieldSpec =
        field(variable.name, variable.type, isStatic)

    private fun field(name: String, type: Type, isStatic: Boolean = true): FieldSpec {
        return FieldSpec.builder(name, getTypeDescriptor(type))
            .addModifier(FieldAccessModifiers.PUBLIC)
            .apply { if(isStatic) addModifier(FieldAccessModifiers.STATIC) }
            .build()
    }

    private fun fieldInitializer(
        variable: Declaration.Variable,
        codeSpec: CodeBlockSpecBuilder,
        parent: ClassName = ClassName("Program"),
        isStatic: Boolean = true
    ): CodeBlockSpecBuilder = fieldInitializer(variable.type, variable.name, codeSpec, parent, isStatic)

    private fun fieldInitializer(
        variable: Declaration.Parameter,
        codeSpec: CodeBlockSpecBuilder,
        parent: ClassName = ClassName("Program"),
        isStatic: Boolean = true
    ): CodeBlockSpecBuilder = fieldInitializer(variable.type, variable.name, codeSpec, parent, isStatic)

    private fun fieldInitializer(
        type: Type,
        name: String,
        codeSpec: CodeBlockSpecBuilder,
        parent: ClassName = ClassName("Program"),
        isStatic: Boolean = true
    ): CodeBlockSpecBuilder {
        if(!isStatic)
            codeSpec.aload0()
        when(type) {
            is Type.Array -> {
                codeSpec.ldc(Constant.Int(type.size))

                when(type.type) {
                    is Type.Int, Type.Boolean -> codeSpec.newarray(getTypeDescriptor(type.type))
                    is Type.Char -> codeSpec.anewarray(getTypeDescriptor(type.type))
                    is Type.Struct -> {
                        val structClass = ClassName(type.type.name)
                        codeSpec.anewarray(getTypeDescriptor(type.type))
                            .putField(parent, FieldSpec(emptySet(), name, getTypeDescriptor(type)))

                        repeat(type.size) { elementIndex ->
                            codeSpec.aload0()
                                .getField(parent, FieldSpec(emptySet(), name, getTypeDescriptor(type)))
                                .ldc(Constant.Int(elementIndex))
                                .new(structClass)
                                .dup()
                                .invokeSpecial(MethodName(structClass, Java.constructor), MethodDescriptor(emptyList(), TypeDescriptor.Void))
                                .aastore()
                        }
                        return codeSpec
                    }
                    is Type.Array -> codeSpec.multianewarray(getTypeDescriptor(type.type), loadArraySizes(type.type, codeSpec, 2))
                    else -> codeSpec.anewarray(getTypeDescriptor(type.type))
                }

                if(isStatic)
                    codeSpec.putStatic(parent, FieldSpec(emptySet(), name, getTypeDescriptor(type)))
                else
                    codeSpec.putField(parent, FieldSpec(emptySet(), name, getTypeDescriptor(type)))
            }
            is Type.Int, is Type.Boolean -> {
                codeSpec.ldc(Constant.Int(0))
                if(isStatic)
                    codeSpec.putStatic(parent, FieldSpec(emptySet(), name, getTypeDescriptor(type)))
                else
                    codeSpec.putField(parent, FieldSpec(emptySet(), name, getTypeDescriptor(type)))
            }
            is Type.Struct -> {
                val structClass = ClassName(type.name)
                codeSpec.new(structClass)
                codeSpec.dup()
                codeSpec.invokeSpecial(MethodName(structClass, Java.constructor), MethodDescriptor(emptyList(), TypeDescriptor.Void))
                if(isStatic)
                    codeSpec.putStatic(parent, FieldSpec(emptySet(), name, getTypeDescriptor(type)))
                else
                    codeSpec.putField(parent, FieldSpec(emptySet(), name, getTypeDescriptor(type)))
            }
            is Type.Char -> {
                codeSpec.ldc(Constant.Str(""))
                if(isStatic)
                    codeSpec.putStatic(parent, FieldSpec(emptySet(), name, getTypeDescriptor(type)))
                else
                    codeSpec.putField(parent, FieldSpec(emptySet(), name, getTypeDescriptor(type)))
            }
            is Type.ArrayUnknownSize, Type.Nothing, Type.Void -> error("ilegal type $type")
        }
        return codeSpec
    }

    private fun variableInitializer(index: Int, type: Type, codeSpec: CodeBlockSpecBuilder) {

        when(type) {
            is Type.Struct -> {
                val structClass = ClassName(type.name)
                codeSpec.new(structClass)
                    .dup()
                    .invokeSpecial(MethodName(structClass, Java.constructor), MethodDescriptor(emptyList(), TypeDescriptor.Void))
                    .astore(index)
            }
            is Type.Char -> {
                codeSpec.ldc(Constant.Str(""))
                    .astore(index)
            }
            is Type.Int, Type.Boolean -> {
                codeSpec.ldc(Constant.Int(0))
                    .istore(index)
            }
            is Type.Array -> {
                val dimensions = loadArraySizes(type, codeSpec)
                when(type.type) {
                    is Type.Int, Type.Boolean -> codeSpec.newarray(getTypeDescriptor(type.type)).astore(index)
                    is Type.Char -> codeSpec.anewarray(getTypeDescriptor(type.type)).astore(index)
                    is Type.Struct -> {
                        val structClass = ClassName(type.type.name)
                        codeSpec.anewarray(getTypeDescriptor(type.type))
                            .dup()

                        repeat(type.size) { elementIndex ->
                            codeSpec.ldc(Constant.Int(elementIndex))
                                .new(structClass)
                                .dup()
                                .invokeSpecial(MethodName(structClass, Java.constructor), MethodDescriptor(emptyList(), TypeDescriptor.Void))
                                .aastore()
                            if(type.size > elementIndex)
                                codeSpec.dup()
                        }

                        codeSpec.astore(index)
                    }
                    is Type.Array -> codeSpec.multianewarray(getTypeDescriptor(type), dimensions).astore(index)
                    is Type.ArrayUnknownSize, is Type.Nothing, is Type.Void -> error("Ilegal type $type")
                }

            }
            is Type.ArrayUnknownSize, is Type.Nothing, is Type.Void -> error("Ilegal type $type")
        }
    }

    private fun loadArraySizes(type: Type.Array, codeSpec: CodeBlockSpecBuilder, dimensions: Int = 1): Int {
        codeSpec.ldc(Constant.Int(type.size))
        return if(type.type is Type.Array)
            loadArraySizes(type.type, codeSpec, dimensions+1)
        else dimensions
    }

    private fun method(_methods: MethodStore, globals: SymbolStore, types: TypeStore, method: Program.Method): MethodSpec {

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
            addInstruction(codeSpec, methods, globals, types, instruction)
        }

        return methodSpec
            .addCodeBlock(codeSpec.build())
            .build()
    }

    private fun addInstruction(codeSpec: CodeBlockSpecBuilder, _methods: MethodStore, _globals: SymbolStore, _types: TypeStore, instruction: Instruction): CodeBlockSpecBuilder {
        val methods = _methods.toList()
        val globals = _globals.toList()
        val types = _types.toList()
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
                instruction.instruction.forEach { addInstruction(codeSpec, methods, globals, types, it) }
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
            is Instruction.StoreGlobal -> {
                val field = globals[instruction.index]
                if(field.type is Type.Array) {
                    when(field.type.type) {
                        is Type.Int, is Type.Boolean -> codeSpec.iastore()
                        else -> codeSpec.aastore()
                    }
                } else
                    codeSpec.putStatic(ClassName("Program"), FieldSpec(emptySet(), field.name, getTypeDescriptor(field.type)))
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
            is Instruction.PutField -> {
                val prop = types.first { it.name == instruction.parent.name }.properties[instruction.index]
                codeSpec.putField(ClassName(instruction.parent.name), FieldSpec(emptySet(), prop.name, getTypeDescriptor(prop.type)))
            }
            is Instruction.LoadField -> {
                val prop = types.first { it.name == instruction.parent.name }.properties[instruction.index]
                codeSpec.getField(ClassName(instruction.parent.name), FieldSpec(emptySet(), prop.name, getTypeDescriptor(prop.type)))
            }
            is Instruction.If -> codeSpec.ifne(instruction.branchLabel)
            is Instruction.Add -> codeSpec.iadd()
            is Instruction.Sub -> codeSpec.isub()
            is Instruction.Div -> codeSpec.idiv()
            is Instruction.Mul -> codeSpec.imul()
            is Instruction.Rem -> codeSpec.irem()
            is Instruction.ILoadLocal -> codeSpec.iload(instruction.index)
            is Instruction.IStore -> codeSpec.istore(instruction.index)
            is Instruction.IAStore -> codeSpec.iastore()
            is Instruction.AAStore -> codeSpec.aastore()
            is Instruction.IReturn -> codeSpec.ireturn()
            is Instruction.AReturn -> codeSpec.areturn()
            is Instruction.Return -> codeSpec.returns()
            is Instruction.ILoadArray -> codeSpec.iaload()
            is Instruction.ALoadLocal -> codeSpec.aload(instruction.index)
            is Instruction.NewVar -> variableInitializer(instruction.index, instruction.type, codeSpec)
            is Instruction.LoadArray -> codeSpec.aaload()
            is Instruction.LoadLocal -> codeSpec.aload(instruction.index)
            is Instruction.StoreRef -> codeSpec.astore(instruction.index)
            is Instruction.SubUnary -> codeSpec.iconst_m1().imul()
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