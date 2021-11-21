package com.github.dcc.compiler.backend.codegen.bytecode

import com.github.dcc.compiler.backend.Dumpable
import jdk.internal.org.objectweb.asm.ClassWriter
import java.io.File

data class BytecodeProgram(
    val program: ClassFile,
    val structs: Collection<ClassFile>
): Dumpable {
    data class ClassFile(val name: String, val cw: ClassWriter)

    private val extension = ".class"
    override fun dump(destination: File): Collection<File> {
        require(destination.isDirectory) { "Destination of dump is not a directory!" }
        val program = File("${destination.path}/${program.name}$extension").apply { writeBytes(program.cw.toByteArray()) }
        val structs = structs.map {
            File("${destination.path}/${it.name}$extension").apply { writeBytes(it.cw.toByteArray()) }
        }
        return listOf(program) + structs
    }

}
