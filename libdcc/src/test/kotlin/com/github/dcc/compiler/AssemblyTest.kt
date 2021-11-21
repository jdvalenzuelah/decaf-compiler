package com.github.dcc.compiler

import com.github.dcc.compiler.backend.Dumpable
import com.github.dcc.compiler.backend.Target
import org.junit.jupiter.api.Assertions
import java.io.*


abstract class AssemblyTest  {
    abstract val target: Target
    internal open val generatedFilesFolder = File("/tmp/libdcc").apply { mkdirs() }

    fun getFileFromResources(path: String) = File(this::class.java.getResource(path)!!.toURI())

    fun execProgram(input: Iterable<Any> = emptyList(), interactive: Boolean = false): String {
        val process = Runtime.getRuntime().exec("java -noverify -cp ${generatedFilesFolder.path} Program")

        val processOutput = BufferedReader(InputStreamReader(process.inputStream))
        val processInput = BufferedWriter(OutputStreamWriter(process.outputStream))

        val feedback = StringBuilder()

        processInput.use { writer ->
            input.forEach {
                writer.appendLine(it.toString())
                writer.flush()
                feedback.appendLine(processOutput.readLine())
            }
        }

        process.waitFor()

        process.outputStream

        return if(interactive) feedback
            .append(processOutput.readText())
            .toString()
        else processOutput.readText()
    }

    abstract fun assemble(dumps: Dumpable)

    fun assertSuccessAndExpectedOutput(sourceFile: File, expected: String, programInput: Iterable<Any> = emptyList(), interactive: Boolean = false) {
        val compiler = Compiler(sourceFile, target)

        val compilationResult = compiler.compileSource()

        Assertions.assertTrue(compilationResult is Compiler.CompilationResult.Success)

        val compiledSource = (compilationResult as Compiler.CompilationResult.Success).compiledSource

        assemble(compiledSource)
        val executionResult = execProgram(programInput, interactive)

        Assertions.assertEquals(expected, executionResult.trim())
    }
}