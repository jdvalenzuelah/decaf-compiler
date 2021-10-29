package com.github.jasmin

sealed interface Instruction: JasminElement {
    val mnemonic: String
    override val serialize: String
        get() = mnemonic
}

enum class NoParams(
    override val mnemonic: String
): Instruction {
    iaload("iaload"),
    aload_0("aload_0"),
    aload_1("aload_1"),
    aload_2("aload_2"),
    iload_0("iload_0"),
    astore_0("astore_0"),
    _return("return"),
    ireturn("ireturn"),
    iadd("iadd"),
    isub("isub"),
    idiv("idiv"),
    imul("imul"),
    irem("irem"),
    iastore("iastore"),
    dup("dup"),
    pop("pop"),
}

sealed class WithParams : Instruction {

    data class getstatic(
        val parent: ClassName,
        val name: String,
        val descriptor: TypeDescriptor,
    ) : WithParams() {
        override val mnemonic: String = "getstatic"

        override val serialize: String
            get() = "$mnemonic ${parent.serialize}/$name ${descriptor.serialize}"

    }

    data class putstatic(
        val parent: ClassName,
        val name: String,
        val descriptor: TypeDescriptor,
    ) : WithParams() {
        override val mnemonic: String = "putstatic"

        override val serialize: String
            get() = "$mnemonic ${parent.serialize}/$name ${descriptor.serialize}"

    }

    data class putfield(
        val parent: ClassName,
        val name: String,
        val descriptor: TypeDescriptor,
    ) : WithParams() {
        override val mnemonic: String = "putfield"

        override val serialize: String
            get() = "$mnemonic ${parent.serialize}/$name ${descriptor.serialize}"

    }

    data class label(
        val label: String
    ) : WithParams() {
        override val mnemonic: String = label

        override val serialize: String = "$mnemonic:"
    }

    data class if_icmpge(
        val label: String
    ) : WithParams() {
        override val mnemonic: String = "if_icmpge"

        override val serialize: String = "$mnemonic $label"
    }

    data class if_icmple(
        val label: String
    ) : WithParams() {
        override val mnemonic: String = "if_icmple"

        override val serialize: String = "$mnemonic $label"
    }

    data class if_icmplt(
        val label: String
    ) : WithParams() {
        override val mnemonic: String = "if_icmplt"

        override val serialize: String = "$mnemonic $label"
    }

    data class if_icmpgt(
        val label: String
    ) : WithParams() {
        override val mnemonic: String = "if_icmpgt"

        override val serialize: String = "$mnemonic $label"
    }

    data class if_icmpne(
        val label: String
    ) : WithParams() {
        override val mnemonic: String = "if_icmpne"

        override val serialize: String = "$mnemonic $label"
    }

    data class if_icmpeq(
        val label: String
    ) : WithParams() {
        override val mnemonic: String = "if_icmpeq"

        override val serialize: String = "$mnemonic $label"
    }

    data class ifne(
        val label: String
    ) : WithParams() {
        override val mnemonic: String = "ifne"

        override val serialize: String = "$mnemonic $label"
    }

    data class ifeq(
        val label: String
    ) : WithParams() {
        override val mnemonic: String = "ifeq"

        override val serialize: String = "$mnemonic $label"
    }

    data class goto(
        val label: String
    ) : WithParams() {
        override val mnemonic: String = "goto"

        override val serialize: String = "$mnemonic $label"
    }

    data class ldc(
        val constant: Constant,
    ) : WithParams() {
        override val mnemonic: String = "ldc"

        override val serialize: String
            get() = "$mnemonic ${constant.serialize}"
    }

    data class newarray(
        val type: TypeDescriptor
    ) : WithParams() {
        override val mnemonic: String = "newarray"

        override val serialize: String
            get() = "$mnemonic ${type.atype}"
    }

    data class iload(
        val index: Int
    ) : WithParams() {
        override val mnemonic: String = "iload"

        override val serialize: String
            get() = "$mnemonic $index"
    }

    data class istore(
        val index: Int
    ) : WithParams() {
        override val mnemonic: String = "istore"

        override val serialize: String
            get() = "$mnemonic $index"
    }

    data class invokenonvirtual(
        val methodName: MethodName,
        val descriptor: MethodDescriptor,
    ) : WithParams() {
        override val mnemonic: String = "invokenonvirtual"

        override val serialize: String
            get() = "$mnemonic ${methodName.serialize}${descriptor.serialize}"

    }

    data class invokestatic(
        val methodName: MethodName,
        val descriptor: MethodDescriptor,
    ) : WithParams() {
        override val mnemonic: String = "invokestatic"

        override val serialize: String
            get() = "$mnemonic ${methodName.serialize}${descriptor.serialize}"

    }

    data class invokevirtual(
        val methodName: MethodName,
        val descriptor: MethodDescriptor,
    ) : WithParams() {
        override val mnemonic: String = "invokevirtual"

        override val serialize: String
            get() = "$mnemonic ${methodName.serialize}${descriptor.serialize}"

    }

    data class invokespecial(
        val methodName: MethodName,
        val descriptor: MethodDescriptor,
    ) : WithParams() {
        override val mnemonic: String = "invokespecial"

        override val serialize: String
            get() = "$mnemonic ${methodName.serialize}${descriptor.serialize}"

    }

    data class new(
        val className: ClassName
    ): WithParams() {
        override val mnemonic: String = "new"

        override val serialize: String
            get() = "$mnemonic ${className.serialize}"
    }

}
