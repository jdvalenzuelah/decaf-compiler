package com.github.jasmin

sealed class Constant(
    override val serialize: String
): JasminElement {

    class Int(value: kotlin.Int) : Constant("$value")

    class Dec(value: Float) : Constant("$value")

    class Str(value: String): Constant("\"$value\"")

}
