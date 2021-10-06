package com.github.jasmin

sealed interface Instruction: JasminElement {
    val mnemonic: String
    override val serialize: String
        get() = mnemonic
}

enum class NoParams(
    override val mnemonic: String
): Instruction {
    aload_0("aload_0"),
    aload_1("aload_1"),
    aload_2("aload_2"),
    _return("return")
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

    data class ldc(
        val constant: Constant,
    ) : WithParams() {
        override val mnemonic: String = "ldc"

        override val serialize: String
            get() = "$mnemonic ${constant.serialize}"
    }

    data class invokenonvirtual(
        val methodName: MethodName,
        val descriptor: MethodDescriptor,
    ) : WithParams() {
        override val mnemonic: String = "invokenonvirtual"

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

}
