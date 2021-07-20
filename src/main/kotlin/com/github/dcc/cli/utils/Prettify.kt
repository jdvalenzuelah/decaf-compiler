package com.github.dcc.cli.utils

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
}