package com.github.v60

import com.github.dcc.compiler.CompilationResult
import com.github.dcc.compiler.CompilerContext
import com.github.v60.ui.actionBar
import com.github.v60.ui.console
import com.github.v60.ui.editor
import com.github.v60.ui.fileStatus
import com.github.v60.ui.fontAwesome.fontAwesomePlugin
import com.github.v60.ui.style.ide
import com.github.v60.ui.style.ideStyle
import com.github.validation.Validated
import kweb.*
import kweb.state.KVar
import kweb.state.render
import java.util.*

private fun String.decodeBase64() = String(Base64.getDecoder().decode(this))

class V60(
    private val port: Int = 9000,
    private val debug: Boolean = false
) {

    private val code = KVar("")
    private val fileName = KVar("")
    private val compilationResult = KVar<CompilationResult>(Validated.Valid)

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
                            val compiler = CompilerContext(code.value)
                            compilationResult.value = compiler.compileSource()

                        }

                        render(fileName) { fileNameStr ->
                            fileStatus(fileNameStr)
                        }

                        editor(ide.codeEditor, code)

                        render(compilationResult) { errorsList ->
                            console(fileName.value, errorsList)
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