package com.github.v60

import com.github.dcc.compiler.Compiler
import com.github.dcc.compiler.backend.Dumpable
import com.github.dcc.compiler.backend.noOp
import com.github.dcc.compiler.ir.Program
import com.github.v60.ui.actionBar
import com.github.v60.ui.console
import com.github.v60.ui.editor
import com.github.v60.ui.fileStatus
import com.github.v60.ui.fontAwesome.fontAwesome
import com.github.v60.ui.fontAwesome.fontAwesomePlugin
import com.github.v60.ui.style.ide
import com.github.v60.ui.style.ideStyle
import com.github.validation.Validated
import kweb.*
import kweb.state.KVar
import kweb.state.render
import kweb.util.json
import java.util.*

private fun String.decodeBase64() = String(Base64.getDecoder().decode(this))

class V60(
    private val port: Int = 9000,
    private val debug: Boolean = false
) {

    private val code = KVar("")
    private val fileName = KVar("")
    private val compilationResult = KVar<Compiler.CompilationResult>(Compiler.CompilationResult.Success(Dumpable.noOp(), Program.empty()))

    init {
        Kweb(port = port, debug = debug, plugins = listOf(ideStyle, fontAwesomePlugin)) {
            doc.body {
                div(ide.v60) {
                    div(ide.container) {
                        val actionBarContext = actionBar()

                        actionBarContext.open.onFileSelect {
                            actionBarContext.open.retrieveFile { file ->
                                fileName.value = file.fileName
                                code.value = file.base64Content
                                    .replace("data:.*;base64,".toRegex(), "")
                                    .decodeBase64()
                            }
                        }

                        actionBarContext.build.on.click {
                            val compiler = Compiler(code.value)
                            compilationResult.value = compiler.compileSource()

                        }

                        render(fileName) { fileNameStr ->
                            fileStatus(fileNameStr)
                        }

                        editor(ide.codeEditor, code)

                        render(fileName) { file ->
                            render(compilationResult) { result ->
                                console(file, result)
                            }
                        }
                    }
                }

                render(compilationResult) { result ->
                    if(result is Compiler.CompilationResult.Success) {
                        div(ide.v60) {
                            div(ide.container) {
                                div(ide.irStatus) {
                                    div(attributes = mapOf("style" to "margin: 3px".json))
                                        .text("Intermediate representation")
                                }
                                editor(ide.codeEditor, result.ir.toString())
                            }
                        }
                    }
                }

            }
        }
    }
}

fun main() {
    V60()
}