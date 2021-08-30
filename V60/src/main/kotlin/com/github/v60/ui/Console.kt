package com.github.v60.ui

import com.github.dcc.compiler.Error
import com.github.dcc.compiler.Error.SemanticError
import com.github.v60.ui.fontAwesome.fontAwesome
import com.github.v60.ui.style.ide
import com.github.validation.Validated
import kweb.*

fun ElementCreator<*>.console(fileName: String, buildOutput: Validated<Error>): DivElement {
    return div(ide.console) {
        div(ide.buildStatus) {
            if(buildOutput is Validated.Valid) {
                div(ide.successBuildIcon.left) { i(fontAwesome.fas.check) }
                div(ide.left).text("Build $fileName: successful")
            } else {
                div(ide.failedBuildIcon.left) { i(fontAwesome.fas.exclamationCircle) }
                div(ide.left).text("Build $fileName: failed")
            }
        }
        div(ide.buildOutput) {
            div(ide.scrollY) {
                if(buildOutput is Validated.Invalid) {
                    buildOutput.forEach {
                        when(val error = it.e) {
                            is SemanticError -> semanticError(error)
                            is Error.SyntaxError -> syntaxError(error)
                        }
                    }
                }
            }
        }
    }
}

private val SemanticError.line: Int get() = context.parserContext.start.line
private val SemanticError.charPos: Int get() = context.parserContext.start.charPositionInLine

fun ElementCreator<*>.semanticError(error: SemanticError) {
    p(ide.error).text("line  ${error.line}:${error.charPos} ${error.message}")
}

fun ElementCreator<*>.syntaxError(error: Error.SyntaxError) {
    p(ide.error).text(error.message)
}