package com.github.dcc.compiler.backend.codegen.jasmin

import com.github.jasmin.spec.ClassSpec

data class JasminProgramSpec(
    val program: ClassSpec,
    val structs: Collection<ClassSpec> = emptyList()
)