package com.github.jasmin.spec

import com.github.jasmin.Constant
import com.github.jasmin.FieldAccessModifiers
import com.github.jasmin.JasminElement
import com.github.jasmin.TypeDescriptor
import com.github.jasmin.serialize.SerializeField
import com.github.jasmin.spec.builder.FieldSpecBuilder

data class FieldSpec(
    val accessSpec: Set<FieldAccessModifiers>,
    val name: String,
    val type: TypeDescriptor,
    val value: Constant? = null,
): JasminElement {
    companion object {
        fun builder(name: String, type: TypeDescriptor) = FieldSpecBuilder(name, type)
    }

    override val serialize: String
        get() = SerializeField(this)
}