package com.github.dcc.compiler

import com.github.dcc.compiler.backend.Dumpable
import com.github.dcc.compiler.backend.Target
import org.junit.jupiter.api.Test
import java.io.File

class JavaAssemblyTest: AssemblyTest() {

    override val target: Target = Target.JAVA8

    override val generatedFilesFolder: File = File("/tmp/libdcc/java8").apply { mkdirs() }

    override fun assemble(dumps: Dumpable) { dumps.dump(generatedFilesFolder) }

    @Test
    fun `should compile and assemble files`() {
        assertSuccessAndExpectedOutput(getFileFromResources("/samples/fact_array.decaf"), "120")
        assertSuccessAndExpectedOutput(getFileFromResources("/samples/fact_struct.decaf"), "120")
        assertSuccessAndExpectedOutput(getFileFromResources("/samples/quicksort.decaf"), "1\n2\n3\n4\n5\n6\n7\n8\n9\n10", 10 downTo 1)
        assertSuccessAndExpectedOutput(getFileFromResources("/samples/multiple_tests.decaf"), "1\n1\n2\n6\n24\n6\n6\n720\n40320\n362880\n3628800\n39916800", 0..11, true)
        assertSuccessAndExpectedOutput(getFileFromResources("/samples/structs.decaf"), "2")
        assertSuccessAndExpectedOutput(getFileFromResources("/samples/param.decaf"), "17")
        assertSuccessAndExpectedOutput(getFileFromResources("/samples/scope.decaf"), "3\n5\n6\n7\n8\n5\n4")
    }

}