package com.github.jasmin

class ClassName(name: String): JasminElement {
    constructor(pckg: String, name: String) : this("$pckg.$name")

    val name: String =  name.replace(".", "/")

    override val serialize: String
        get() = name
}