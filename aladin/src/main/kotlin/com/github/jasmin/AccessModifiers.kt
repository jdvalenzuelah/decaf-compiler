package com.github.jasmin

enum class ClassAccessModifiers(
    override val serialize: String
) : JasminElement {
    PUBLIC("public"),
    FINAL("final"),
    SUPER("super"),
    INTERFACE("interface"),
    ABSTRACT("abstract");
}

enum class FieldAccessModifiers(
    override val serialize: String
) : JasminElement {
    PUBLIC("public"),
    PRIVATE("private"),
    PROTECTED("protected"),
    STATIC("static"),
    FINAL("final"),
    VOLATILE("volatile"),
    TRANSIENT("transient")
}

enum class MethodAccessModifier(
    override val serialize: String
): JasminElement {
    PUBLIC("public"),
    PRIVATE("private"),
    PROTECTED("protected"),
    STATIC("static"),
    FINAL("final"),
    SYNCHRONIZED("synchronized"),
    NATIVE("native"),
    ABSTRACT("abstract"),
}