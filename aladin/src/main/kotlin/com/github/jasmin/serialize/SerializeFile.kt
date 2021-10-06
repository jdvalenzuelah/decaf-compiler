package com.github.jasmin.serialize

import com.github.jasmin.*
import com.github.jasmin.spec.*
import java.lang.StringBuilder

object SerializeCodeBlock {
    operator fun invoke(code: CodeBlockSpec): String {
        return StringBuilder()
            .appendJasminElements(code.instructions, sep = "\n",  prefix = "\t")
            .toString()
    }
}

object SerializeMethod {
    operator fun invoke(field: MethodSpec): String {
        return StringBuilder()
            .appendJasminElement(Directive.METHOD)
            .appendSpace()
            .appendJasminElements(field.accessSpec)
            .appendSpace()
            .appendJasminElement(field.name)
            .appendJasminElement(field.descriptor)
            .appendLine()
            .apply {
                if(field.stackLimit != null) {
                    appendJasminElement(Directive.LIMIT, prefix = "\t")
                    appendSpace()
                    appendJasminElement(ReservedWords.STACK)
                    appendSpace()
                    appendLine(field.stackLimit)
                        .appendLine()
                }

                if(field.localsLimit != null) {
                    appendJasminElement(Directive.LIMIT, prefix = "\t")
                    appendSpace()
                    appendJasminElement(ReservedWords.LOCALS)
                    appendSpace()
                    appendLine(field.localsLimit)
                        .appendLine()
                }
            }
            .appendJasminElements(field.statements)
            .appendLine()
            .appendJasminElement(Directive.END)
            .appendSpace()
            .appendJasminElement(ReservedWords.METHOD)
            .toString()
    }
}

object SerializeField {
    operator fun invoke(field: FieldSpec): String {
        return StringBuilder()
            .appendJasminElement(Directive.FIELD)
            .appendSpace()
            .appendJasminElements(field.accessSpec)
            .appendSpace()
            .append(field.name)
            .appendSpace()
            .appendJasminElement(field.type)
            .apply {
                if(field.value != null) {
                    append(" = ")
                    appendJasminElement(field.value)
                }
            }
            .toString()
    }
}

object SerializeClass {
    operator fun invoke(cls: ClassSpec): String {
        return StringBuilder()
            .setClassName(cls.name, cls.accessSpec)
            .appendLine()
            .setSuperClass(cls.superClass.name)
            .appendLine()
            .appendJasminElements(cls.fields, "\n")
            .appendLine()
            .appendJasminElements(cls.methods, "\n\n")
            .toString()
    }

    private fun StringBuilder.setClassName(name: ClassName, modifiers: Iterable<ClassAccessModifiers>): StringBuilder = apply {
        appendJasminElement(Directive.CLASS)
        appendSpace()
        appendJasminElements(modifiers)
        appendSpace()
        appendJasminElement(name)
    }

    private fun StringBuilder.setSuperClass(name: String): StringBuilder = apply {
        appendJasminElement(Directive.SUPER)
        appendSpace()
        appendLine(name)
    }
}

object SerializeFile {

    operator fun invoke(spec: FileSpec): String {
        return StringBuilder()
            .setSource(spec.source)
            .appendJasminElement(spec.cls)
            .toString()
    }

    private fun StringBuilder.setSource(src: String): StringBuilder = apply {
        appendJasminElement(Directive.SOURCE)
        append(" ", src)
        appendLine()
    }

}

private fun StringBuilder.appendSpace(): StringBuilder = append(" ")

private fun StringBuilder.appendJasminElement(el: JasminElement, prefix: String = ""): StringBuilder = append(prefix)
    .append(el.serialize)

private fun StringBuilder.appendJasminElements(els: Iterable<JasminElement>, sep: String = " ", prefix: String = ""): StringBuilder = apply {
    append(els.joinToString(separator = sep) { "$prefix${it.serialize}" })
}