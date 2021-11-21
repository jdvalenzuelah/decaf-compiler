package com.github.dcc.compiler

import com.github.dcc.compiler.backend.Dumpable
import com.github.dcc.compiler.backend.Target
import com.github.jasmin.assembly.AssembleJasmin
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.*

class JasminAssemblyTest : AssemblyTest() {

    override val generatedFilesFolder = File("/tmp/libdcc/jasmin").apply { mkdirs() }

    override val target: Target = Target.JASMIN

    override fun assemble(dumps: Dumpable) {
        dumps.dump(generatedFilesFolder).forEach {
            AssembleJasmin.assemble(it.reader(), it.name, FileOutputStream("${generatedFilesFolder.path}/${it.nameWithoutExtension}.class"))
        }

    }

    @Test
    fun `should compile and assemble files`() {
        assertSuccessAndExpectedOutput(getFileFromResources("/samples/fact_array.decaf"), "120")
        assertSuccessAndExpectedOutput(getFileFromResources("/samples/fact_struct.decaf"), "120")
        assertSuccessAndExpectedOutput(getFileFromResources("/samples/param.decaf"), "17")
        assertSuccessAndExpectedOutput(getFileFromResources("/samples/quicksort.decaf"), "1\n2\n3\n4\n5\n6\n7\n8\n9\n10", 10 downTo 1)
        assertSuccessAndExpectedOutput(getFileFromResources("/samples/scope.decaf"), "3\n5\n6\n7\n8\n5\n4")
        assertSuccessAndExpectedOutput(getFileFromResources("/samples/structs.decaf"), "2")
        assertSuccessAndExpectedOutput(getFileFromResources("/samples/multiple_tests.decaf"), "1\n1\n2\n6\n24\n6\n6\n720\n40320\n362880\n3628800\n39916800", 0..11, true)
    }

}