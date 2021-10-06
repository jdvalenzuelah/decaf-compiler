package com.github.jasmin.java

import com.github.jasmin.ClassName
import com.github.jasmin.MethodName
import com.github.jasmin.TypeDescriptor
import com.github.jasmin.spec.FieldSpec
import com.github.jasmin.spec.builder.asType
import java.io.PrintStream

object Java {
    const val constructor = "<init>"
    object lang {
        const val pckg = "java.lang"
        val Object = ClassName(pckg, "Object")
        val init = MethodName(Object, constructor)
        val String = ClassName(pckg,"String")

        object System {
            val pckg = ClassName("java.lang", "System")

            val out = FieldSpec(emptySet(), "out", io.PrintStream.asType(), null)
        }
    }

    object io {
        const val pckg = "java.io"
        val PrintStream = ClassName(pckg, "PrintStream")

        val println = MethodName(Java.io.PrintStream, "println")

    }
}