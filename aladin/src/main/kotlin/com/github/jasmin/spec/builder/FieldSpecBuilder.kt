package com.github.jasmin.spec.builder

import com.github.jasmin.Constant
import com.github.jasmin.FieldAccessModifiers
import com.github.jasmin.TypeDescriptor
import com.github.jasmin.spec.FieldSpec

class FieldSpecBuilder(
    val name: String,
    val type: TypeDescriptor,
) {

    private val modifiers = mutableSetOf<FieldAccessModifiers>()
    private var initializer: Constant? = null

    fun addModifier(modifier: FieldAccessModifiers) = apply { modifiers.add(modifier) }

    fun addInitializer(cons: Constant) = apply { initializer = cons }

    fun build(): FieldSpec {
        return FieldSpec(
            accessSpec = modifiers,
            name = name,
            type = type,
            value = initializer
        )
    }

}