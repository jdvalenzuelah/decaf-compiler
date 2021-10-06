package com.github.jasmin

enum class ReservedWords(
    override val serialize: String
): JasminElement {
    METHOD("method"),
    STACK("stack"),
    LOCALS("locals")
}