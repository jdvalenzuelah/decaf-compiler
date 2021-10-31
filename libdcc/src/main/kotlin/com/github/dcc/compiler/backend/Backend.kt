package com.github.dcc.compiler.backend

import com.github.dcc.compiler.ir.Program
import java.io.File

interface Dumpable {
    companion object
    fun dump(): Collection<File>
}

fun interface Backend<I : Dumpable> {
    fun compile(program: Program): I
}

fun Dumpable.Companion.noOp(): Dumpable = object : Dumpable {
    override fun dump(): Collection<File> = emptyList()
}