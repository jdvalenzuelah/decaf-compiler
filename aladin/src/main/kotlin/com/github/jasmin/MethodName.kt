package com.github.jasmin

data class MethodName(
    val className: ClassName?,
    val name: String,
) : JasminElement {
    override val serialize: String
        get() = if(className != null) "${className.serialize}/$name" else name
}