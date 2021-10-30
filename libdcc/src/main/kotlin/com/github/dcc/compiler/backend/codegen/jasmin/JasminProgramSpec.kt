package com.github.dcc.compiler.backend.codegen.jasmin

import com.github.dcc.compiler.backend.Dumpable
import com.github.jasmin.spec.ClassSpec
import java.io.File

data class JasminProgramSpec(
    val program: ClassSpec,
    val structs: Collection<ClassSpec> = emptyList()
): Dumpable {

    private val extension = ".j"
    override fun dump(): Collection<File> {
        val program = File("${program.name.name}$extension").apply { writeText(program.serialize) }
        val structs = structs.map {
            File("${it.name.name}$extension").apply { writeText(it.serialize) }
        }
        return listOf(program) + structs
    }

}