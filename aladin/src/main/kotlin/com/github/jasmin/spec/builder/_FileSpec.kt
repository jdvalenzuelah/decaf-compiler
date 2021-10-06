package com.github.jasmin.spec.builder

import com.github.jasmin.spec.ClassSpec
import com.github.jasmin.spec.FileSpec

fun ClassSpec.asFileSpec(name: String) = FileSpec(name, this)