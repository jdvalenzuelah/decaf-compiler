package com.github.dcc.cli.utils

import com.github.dcc.decaf.symbols.SymbolTable
import com.github.dcc.decaf.utils.StoreTable
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
            "UID" to 0,
            "Name" to 0,
            "Type" to 0,
            "Symbol" to 0,
            "Scope" to 0,
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

}