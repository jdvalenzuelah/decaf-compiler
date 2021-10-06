package com.github.jasmin.spec.builder

import com.github.jasmin.*
import com.github.jasmin.spec.CodeBlockSpec
import com.github.jasmin.spec.MethodSpec

class MethodSpecBuilder(
    val parent: ClassName?,
    val name: String
) {

    constructor(name: MethodName): this(name.className, name.name)

    private var returnType: TypeDescriptor = TypeDescriptor.Void
    private val args = mutableListOf<TypeDescriptor>()
    private val modifiers = mutableSetOf<MethodAccessModifier>()
    private val statements = mutableListOf<CodeBlockSpec>()
    private var stackLimit: Int? = null
    private var localsLimit: Int? = null

    fun addReturnType(type: TypeDescriptor) = apply { returnType = type }

    fun addArgument(type: TypeDescriptor) = apply { args.add(type) }

    fun addModifier(modifier: MethodAccessModifier) = apply { modifiers.add(modifier) }

    fun addCodeBlock(stm: CodeBlockSpec) = apply { statements.add(stm) }

    fun limitStack(limit: Int) = apply { stackLimit = limit }

    fun limitLocals(limit: Int) = apply { localsLimit = limit }

    fun build(): MethodSpec {
        return MethodSpec(
            accessSpec = modifiers,
            name = MethodName(
                className = parent,
                name = name
            ),
            descriptor = MethodDescriptor(
                argumentTypes = args,
                returnType = returnType
            ),
            statements = statements,
            stackLimit = stackLimit,
            localsLimit = localsLimit
        )
    }

}