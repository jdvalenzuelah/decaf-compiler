package com.github.jasmin

data class MethodDescriptor(
    val argumentTypes: Iterable<TypeDescriptor>,
    val returnType: TypeDescriptor,
) : JasminElement {
    override val serialize: String
        get() {
            val args = argumentTypes.joinToString(separator = ""){ it.serialize }
            return "($args)${returnType.serialize}"
        }
}