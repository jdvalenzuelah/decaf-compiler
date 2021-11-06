package com.github.dcc.compiler.backend.codegen.jasmin

import com.github.dcc.compiler.backend.Dumpable
import com.github.jasmin.spec.ClassSpec
import java.io.File

data class JasminProgramSpec(
    val program: ClassSpec,
    val structs: Collection<ClassSpec> = emptyList()
): Dumpable {

    private val extension = ".j"
    override fun dump(destination: File): Collection<File> {
        require(destination.isDirectory) { "Destination of dump is not a directory!" }
        val program = File("${destination.path}/${program.name.name}$extension").apply { writeText(program.serialize) }
        val structs = structs.map {
            File("${destination.path}/${it.name.name}$extension").apply { writeText(it.serialize) }
        }
        return listOf(program) + structs
    }

}