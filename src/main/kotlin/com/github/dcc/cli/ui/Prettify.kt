package com.github.dcc.cli.ui

import com.github.dcc.compiler.semanticAnalysis.SemanticError
import com.github.validation.Validated
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.misc.Interval
import org.antlr.v4.runtime.tree.Tree
import org.antlr.v4.runtime.tree.Trees
import java.lang.StringBuilder

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

}