package com.github.dcc.compiler.backend

import com.github.dcc.compiler.ir.Program
import java.io.File

interface Dumpable {
    fun dump(): Collection<File>
}

fun interface Backend<I : Dumpable> {
    fun compile(program: Program): I
}