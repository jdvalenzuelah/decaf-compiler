package com.github.jasmin

enum class Directive(
    override val serialize: String
) : JasminElement {
    CATCH(".catch"),
    CLASS(".class"),
    END(".end"),
    FIELD(".field"),
    IMPLEMENTS(".implements"),
    INTERFACE(".interface"),
    LIMIT(".limit"),
    LINE(".line"),
    METHOD(".method"),
    SOURCE(".source"),
    SUPER(".super"),
    THROWS(".throws"),
    VAR(".var");
}