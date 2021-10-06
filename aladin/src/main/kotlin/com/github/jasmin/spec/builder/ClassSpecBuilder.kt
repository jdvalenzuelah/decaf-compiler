package com.github.jasmin.spec.builder

import com.github.jasmin.ClassAccessModifiers
import com.github.jasmin.java.Java
import com.github.jasmin.ClassName
import com.github.jasmin.spec.ClassSpec
import com.github.jasmin.spec.FieldSpec
import com.github.jasmin.spec.MethodSpec

class ClassSpecBuilder(
    val name: ClassName
) {
    private var superClass: ClassName = Java.lang.Object
    private val accessModifiers = mutableSetOf<ClassAccessModifiers>()
    private val fields = mutableListOf<FieldSpec>()
    private val methods = mutableListOf<MethodSpec>()

    fun superClass(cls: ClassName) = apply { superClass = cls }

    fun addModifier(modifier: ClassAccessModifiers) = apply {
        accessModifiers.add(modifier)
    }

    fun addField(field: FieldSpec) = apply { fields.add(field) }

    fun addMethod(method: MethodSpec) = apply { methods.add(method) }

    fun build() : ClassSpec {
        return ClassSpec(
            name = name,
            accessSpec = accessModifiers,
            superClass = superClass,
            fields = fields,
            methods = methods
        )
    }

}