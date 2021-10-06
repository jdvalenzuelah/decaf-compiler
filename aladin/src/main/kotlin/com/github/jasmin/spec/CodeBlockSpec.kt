package com.github.jasmin.spec

import com.github.jasmin.Instruction
import com.github.jasmin.JasminElement
import com.github.jasmin.serialize.SerializeCodeBlock
import com.github.jasmin.spec.builder.CodeBlockSpecBuilder

data class CodeBlockSpec(
    val instructions: List<Instruction>
) : JasminElement {
    override val serialize: String
        get() = SerializeCodeBlock(this)

    companion object {
        fun builder() = CodeBlockSpecBuilder()
    }
}