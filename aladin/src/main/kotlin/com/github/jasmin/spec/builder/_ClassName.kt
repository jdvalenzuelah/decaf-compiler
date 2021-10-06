package com.github.jasmin.spec.builder

import com.github.jasmin.TypeDescriptor
import com.github.jasmin.ClassName

fun String.asClassName() = ClassName(this)

fun ClassName.asType() = TypeDescriptor.Class(this)