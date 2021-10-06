package com.github.jasmin.spec

import com.github.jasmin.ClassAccessModifiers
import com.github.jasmin.ClassName
import com.github.jasmin.JasminElement
import com.github.jasmin.java.Java
import com.github.jasmin.serialize.SerializeClass
import com.github.jasmin.spec.builder.ClassSpecBuilder
import com.github.jasmin.spec.builder.asClassName

data class ClassSpec(
    val name: ClassName,
    val accessSpec: Set<ClassAccessModifiers> = emptySet(),
    val superClass: ClassName = Java.lang.Object,
    val fields: Collection<FieldSpec> = emptyList(),
    val methods: Collection<MethodSpec> = emptyList()
): JasminElement {

    companion object {
        fun builder(name: String) = ClassSpecBuilder(name.asClassName())
    }

    override val serialize: String
        get() = SerializeClass(this)

}