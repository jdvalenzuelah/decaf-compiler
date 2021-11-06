package com.github.jasmin.assembly

import com.github.jasmin.spec.ClassSpec
import com.github.jasmin.spec.FileSpec
import jasmin.ClassFile
import java.io.FileOutputStream
import java.io.Reader
import java.io.StringReader

object AssembleJasmin {

    fun assemble(fileSpec: FileSpec, destination: FileOutputStream) {
        val reader = StringReader(fileSpec.serialize)
        assemble(reader, fileSpec.source, destination)
    }

    fun assemble(classSpec: ClassSpec, destination: FileOutputStream) {
        val reader = StringReader(classSpec.serialize)
        assemble(reader, classSpec.name.name, destination)
    }

    fun assemble(reader: Reader, name: String, destination: FileOutputStream) {
        ClassFile().apply {
            readJasmin(reader, name, true)
            write(destination)
        }
    }


}