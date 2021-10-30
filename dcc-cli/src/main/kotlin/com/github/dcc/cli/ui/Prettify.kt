package com.github.dcc.cli.ui

import com.github.dcc.compiler.Compiler
import com.github.dcc.compiler.Error
import com.github.dcc.compiler.Error.SemanticError
import com.github.dcc.compiler.Error.SyntaxError
import com.github.dcc.compiler.ir.Program
import com.github.dcc.compiler.symbols.variables.SymbolTable
import com.github.dcc.decaf.enviroment.lineageAsString
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.symbols.signature
import com.github.validation.Validated
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.Tree
import org.antlr.v4.runtime.tree.Trees
import java.io.File
import kotlin.math.max
import kotlin.text.StringBuilder

object Prettify {

    fun tree(t: Tree, ruleNames: List<String>): String {
        val buf = StringBuffer()
        buf.append(Trees.getNodeText(t, ruleNames), "\n")
        buf.append(walk(t, ruleNames, ""))
        return buf.toString()
    }

    private fun walk(t: Tree, ruleNames: List<String>, prefix: String): String {
        val buf = StringBuilder()
        (0 until t.childCount).forEach { index ->
            val tree = t.getChild(index)
            val (pointer, segment) = if(index == t.childCount - 1) "└── " to "    " else "├── " to "│   "
            buf.append(prefix, pointer, Trees.getNodeText(tree, ruleNames), "\n")
            buf.append(walk(tree, ruleNames, "$prefix$segment"))
        }
        return buf.toString()
    }

    fun ir(program: Program): String = program.toString()

    fun compilationResult(result: Compiler.CompilationResult, file: File): String {
        return when(result) {
            is Compiler.CompilationResult.Success -> "${file.path}: Build passed!"
            is Compiler.CompilationResult.SyntaxError -> errors(result.errors, file)
            is Compiler.CompilationResult.SemanticError -> errors(result.errors, file)
        }
    }

    private fun errors(err: Iterable<Validated.Invalid<Error>>, file: File): String {
        val buf = StringBuilder()
        err.forEach { error ->
            val errorStr = when(val ce = error.e) {
                is SemanticError -> semanticError(file.path, ce)
                is SyntaxError -> syntaxError(file.path, ce)
            }
            buf.append(errorStr)
            buf.appendLine()
        }
        return buf.toString()
    }

    private fun semanticError(fileName: String, err: SemanticError): String {
        return error(fileParsingLocation(fileName, err.context), "semantic error: ${err.message}")
    }

    private fun syntaxError(fileName: String, err: SyntaxError): String {
        return error(fileParsingLocation(fileName, err.context), "syntax error: ${err.message}")
    }

    private fun fileParsingLocation(fileName: String, parserContext: ParserRuleContext?): String {
        return "$fileName:${parserContext?.start?.line}:${parserContext?.start?.charPositionInLine}"
    }

    private fun error(location: String, errorMsg: String): String {
        return "$location: $errorMsg"
    }

    fun methods(symbols: Iterable<Declaration.Method>): String {
        val symbolRows = symbols.mapIndexed { index, it ->
            mapOf(
                "index" to index.toString(),
                "name" to it.name,
                "type" to it.type.toString(),
                "signature" to it.signature().toString()
            )
        }
        return table(listOf("index","name", "signature"), symbolRows)
    }

    fun variables(symbolTable: SymbolTable): String {
        val variables = symbolTable.allSymbols().mapIndexed { index, variable ->
            mapOf(
                "index" to index.toString(),
                "name" to variable.name,
                "type" to variable.type.toString(),
                "scope" to variable.scope.lineageAsString()
            )
        }

        return table(listOf("index", "name", "type", "scope"), variables)
    }

    fun types(types: Iterable<Declaration.Struct>): String {
        val typeRows = types.mapIndexed { index, it ->
            mapOf(
                "index" to index.toString(),
                "name" to it.name,
                "type" to it.type.toString(),
                "properties" to it.properties.mapIndexed { i, p -> "[$i] - ${p.name} : ${p.type}" }.joinToString(),
            )
        }

        return table(listOf("index","name", "type", "properties"), typeRows)
    }

    private fun table(cols: List<String>, rows: List<Map<String, String>>): String {
        val data = StringBuilder()

        val header = StringBuilder()

        val colSizes = cols.associateWith { col ->
            val rowMax = rows.mapNotNull { it[col] }
                .maxByOrNull { it.length }
                ?.length ?: 0
            max(rowMax, col.length)
        }

        header.append("|")
        val colRowDivider = StringBuilder()
        cols.forEach { col ->
            val size = colSizes[col] ?: 0
            val padded = col.padEnd(size, ' ')
            header.append(" $padded |")
            colRowDivider.append("+-","-".repeat(padded.length), '-')
        }
        colRowDivider.append("+")

        header.appendLine()
        header.appendLine(colRowDivider.toString())

        rows.forEach { row ->
            cols.forEach { col ->
                val size = colSizes[col] ?: 0
                val paddedRow = row.getOrDefault(col, "")
                    .padEnd(size, ' ')
                data.append("| $paddedRow ")
            }
            data.appendLine("|")
        }

        val table = StringBuilder()

        table.appendLine(colRowDivider.toString())
        table.append(header)
        table.append(data)
        table.append(colRowDivider.toString())

        return table.toString()
    }

}