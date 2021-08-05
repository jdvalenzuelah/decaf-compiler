package com.github.dcc.parser

import com.github.dcc.decaf.enviroment.Scope

fun DecafParser.Method_declContext.genSignature(scope: Scope): String {
    val name = ID()!!.text
    val args = parameter().joinToString(separator = ",") { it.text }
    return "$scope.$name($args)"
}