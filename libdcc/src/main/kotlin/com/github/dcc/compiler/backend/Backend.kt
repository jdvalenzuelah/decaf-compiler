package com.github.dcc.compiler.backend

import com.github.dcc.compiler.ir.Program

fun interface Backend<I> {
    fun compile(program: Program): I
}