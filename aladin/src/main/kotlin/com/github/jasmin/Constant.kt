package com.github.jasmin

sealed class Constant(
    override val serialize: String
): JasminElement {

    open class Int(value: kotlin.Int) : Constant("$value")

    class Dec(value: Float) : Constant("$value")

    class Str(value: String): Constant("\"$value\"")

    class Boolean(value: kotlin.Boolean): Int(if(value) 1 else 0)

}
