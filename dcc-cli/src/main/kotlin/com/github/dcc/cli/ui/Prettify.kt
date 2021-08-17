package com.github.dcc.cli.ui

import com.github.dcc.compiler.semanticAnalysis.SemanticError
import com.github.dcc.decaf.enviroment.lineage
import com.github.dcc.decaf.enviroment.lineageAsString
import com.github.dcc.decaf.symbols.Declaration
import com.github.validation.Validated
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.misc.Interval
import org.antlr.v4.runtime.tree.Tree
import org.antlr.v4.runtime.tree.Trees
import java.lang.StringBuilder
import kotlin.math.max

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

    fun semanticErrors(fileName: String, err: Validated.Invalid<SemanticError>, charStream: ANTLRInputStream): String {
        val buf = StringBuilder()
        err.forEach {
            buf.append(semanticError(fileName, it.e, charStream))
            buf.appendLine()
        }
        return buf.toString()
    }

    private fun semanticError(fileName: String, err: SemanticError, charStream: ANTLRInputStream): String {
        val buf = StringBuilder()
        val sourceCode = charStream.getText(Interval(err.context.start.startIndex, err.context.stop.stopIndex))
        val start = err.context.start
        val msg = "semantic error: ${err.message}"
        buf.append(error("$fileName:${start.line}:${start.charPositionInLine}", msg, sourceCode, err.errorStartChar))
        return buf.toString()
    }

    private fun error(location: String, errorMsg: String, sourceCode: String, errStart: Int): String {
        return "$location: $errorMsg\n${sourceCode(sourceCode, errStart)}"
    }

    private fun sourceCode(source: String, mark: Int) = "$source\n${" ".repeat(mark)}^~~~~~~~~~~~~~"


    fun symbols(symbols: Iterable<Declaration>): String {
        val symbolRows = symbols.map {
            mapOf(
                "name" to it.name,
                "symbol type" to if(it is Declaration.Method) "method" else "variable",
                "scope" to it.scope.lineageAsString(),
                "type" to it.type.toString()
            )
        }
        return table(listOf("name", "symbol type", "scope", "type"), symbolRows)
    }

    fun types(types: Iterable<Declaration.Struct>): String {
        val typeRows = types.map {
            mapOf(
                "name" to it.name,
                "scope" to it.scope.lineageAsString(),
                "type" to it.type.toString(),
                "properties" to it.properties.joinToString { p -> "${p.name} : ${p.type}" },
            )
        }

        return table(listOf("name", "scope", "type", "properties"), typeRows)
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