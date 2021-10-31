package com.github.v60.ui

import com.github.dcc.compiler.Compiler
import com.github.dcc.compiler.Error
import com.github.dcc.compiler.Error.SemanticError
import com.github.v60.ui.fontAwesome.fontAwesome
import com.github.v60.ui.style.ide
import com.github.validation.Validated
import kweb.*

fun ElementCreator<*>.console(fileName: String, buildOutput: Compiler.CompilationResult): DivElement {
    return div(ide.console) {
        div(ide.buildStatus) {
            if(buildOutput is Compiler.CompilationResult.Success) {
                div(ide.successBuildIcon.left) { i(fontAwesome.fas.check) }
                div(ide.left).text("Build $fileName: successful")
            } else {
                div(ide.failedBuildIcon.left) { i(fontAwesome.fas.exclamationCircle) }
                div(ide.left).text("Build $fileName: failed")
            }
        }
        div(ide.buildOutput) {
            div(ide.scrollY) {
                val erros = when(buildOutput) {
                    is Compiler.CompilationResult.Success -> emptyList()
                    is Compiler.CompilationResult.SemanticError -> buildOutput.errors
                    is Compiler.CompilationResult.SyntaxError -> buildOutput.errors
                }
                erros.forEach {
                    when(val error = it.e) {
                        is SemanticError -> semanticError(error)
                        is Error.SyntaxError -> syntaxError(error)
                    }
                }
            }
        }
    }
}

private val SemanticError.line: Int get() = context?.start?.line ?: -1
private val SemanticError.charPos: Int get() = context?.start?.charPositionInLine ?: -1

fun ElementCreator<*>.semanticError(error: SemanticError) {
    p(ide.error).text("line  ${error.line}:${error.charPos} ${error.message}")
}

fun ElementCreator<*>.syntaxError(error: Error.SyntaxError) {
    p(ide.error).text(error.message)
}