package com.github.jasmin.spec

import com.github.jasmin.JasminElement
import com.github.jasmin.serialize.SerializeFile

data class FileSpec(
    val source: String,
    val cls: ClassSpec
) : JasminElement {

    override val serialize: String
        get() = SerializeFile(this)

}