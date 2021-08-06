package com.github.dcc.cli.utils

import com.github.dcc.decaf.sematicRules.SemanticError
import com.github.dcc.decaf.symbols.SymbolTable
import com.github.dcc.decaf.types.TypeTable
import com.github.rules.Result
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

    fun symbolTable(t: SymbolTable): String {
        val header = mutableMapOf(
            "UID" to 1,
            "Name" to 1,
            "Type" to 1,
            "Symbol" to 1,
            "Scope" to 1,
        )
        val rows = t.map { (uid, symbol) ->
            header["UID"] = max(header["UID"]!!, uid.length)
            header["Name"] = max(header["Name"]!!, symbol.name.length)
            header["Type"] = max(header["Type"]!!, symbol.type.toString().length)
            header["Symbol"] = max(header["Symbol"]!!, symbol::class.simpleName!!.length)
            header["Scope"] = max(header["Scope"]!!, symbol.scope.genId().length)
            mapOf(
                "UID" to uid,
                "Name" to symbol.name,
                "Type" to symbol.type.toString(),
                "Symbol" to symbol::class.simpleName!!,
                "Scope" to symbol.scope.genId()
            )
        }
        return table(header, rows)
    }

    fun typeTable(t: TypeTable): String {
        val header = mutableMapOf(
            "UID" to 1,
            "Name" to 1,
            "Args" to 1,
        )

        val rows = t.map { (uid, type) ->
            header["UID"] = max(header["UID"]!!, uid.length)
            header["Name"] = max(header["Name"]!!, type.name.length)
            mapOf(
                "UID" to uid,
                "Name" to type.name,
                "Args" to type.args.joinToString { "${it.name}:${it.type}" }
            )
        }

        return table(header, rows)
    }

    private fun table(header: Map<String, Int>, rows: List<Map<String, String>>): String {
        val buf = StringBuilder()
        header.forEach { (header, size) ->
            buf.append(header.padEnd(size+2, ' '))
        }
        buf.appendLine()
        rows.forEach { row ->
            row.forEach { (col, value) ->
                val size = header[col] ?: 0
                buf.append(value.padEnd(size+2, ' '))
            }
            buf.appendLine()
        }

        return buf.toString()
    }

    fun semanticErrors(err: Result.Error<SemanticError>, fileName: String = ""): String {
        val flattenErrors = mutableListOf<SemanticError>()
            .apply { add(err.e) }
        var nextError = err.next
        while (nextError != null) {
            flattenErrors.add(nextError.e)
            nextError = nextError.next
        }

        return semanticErrors(flattenErrors, fileName)
    }

    fun semanticErrors(erros: Iterable<SemanticError>, fileName: String = ""): String {
        val buf = StringBuilder()
        erros.forEach {
            buf.append(semanticError(it, fileName, it.sourceLocation?.start?.line ?: 0, it.sourceLocation?.start?.charPositionInLine ?: 0))
            buf.appendLine()
        }
        return buf.toString()
    }

    fun semanticError(err: SemanticError, fileName: String = "", line: Int, char: Int): String {
        return fileError(fileName, line, char, "semantic error: ${err.message}")
    }

    private fun fileError(fileName: String, line: Int, char: Int, error: String): String {
        return "$fileName:$line:$char $error"
    }

}