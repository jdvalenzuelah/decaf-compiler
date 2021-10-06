package com.github.jasmin

import com.github.jasmin.java.Java

sealed class TypeDescriptor(
    val descriptor: kotlin.String
): JasminElement {

    override val serialize: kotlin.String
        get() = descriptor

    fun asArray() = Array(this)

    open class Class(val className: ClassName) : TypeDescriptor("L${className.name};")

    object String : Class(Java.lang.String)

    object Integer : TypeDescriptor("I")

    object Void : TypeDescriptor("V")

    open class Array(val type: TypeDescriptor) : TypeDescriptor("[${type.descriptor}")
}