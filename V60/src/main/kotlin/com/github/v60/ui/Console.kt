package com.github.v60.ui

import com.github.dcc.compiler.semanticAnalysis.SemanticError
import com.github.v60.ui.fontAwesome.fontAwesome
import com.github.v60.ui.style.ide
import com.github.validation.Validated
import kweb.*

fun ElementCreator<*>.console(buildOutput: Validated<SemanticError>): DivElement {
    return div(ide.console) {
        div(ide.buildStatus) {
            if(buildOutput is Validated.Valid) {
                div(ide.successBuildIcon.left) { i(fontAwesome.fas.check) }
                div(ide.left).text("Build hello_world.decaf: successful")
            } else {
                div(ide.failedBuildIcon.left) { i(fontAwesome.fas.exclamationCircle) }
                div(ide.left).text("Build hello_world.decaf: failed")
            }
        }
        div(ide.buildOutput) {
            div(ide.scrollY) {
                if(buildOutput is Validated.Invalid) {
                    buildOutput.forEach { semanticError(it.e) }
                }
            }
        }
    }
}

fun ElementCreator<*>.semanticError(error: SemanticError) {
    println(error)
    p().text("../decaf-samples/precedence.decaf:20:4: semantic error: Program must contain exactly one main function")
    p().text("int main(char args[])")
    p().text("    ^~~~~~~~~~~~~~")
}