package com.github.v60

import com.github.dcc.compiler.semanticAnalysis.SemanticAnalysis
import com.github.dcc.compiler.semanticAnalysis.SemanticError
import com.github.dcc.parser.DecafLexer
import com.github.dcc.parser.DecafParser
import com.github.v60.ui.actionBar
import com.github.v60.ui.console
import com.github.v60.ui.fileStatus
import com.github.v60.ui.fontAwesome.fontAwesomePlugin
import com.github.v60.ui.monacoEditor.monacoEditor
import com.github.v60.ui.monacoEditor.monacoPlugin
import com.github.v60.ui.style.ide
import com.github.v60.ui.style.ideStyle
import com.github.validation.Validated
import kotlinx.serialization.json.jsonPrimitive
import kweb.*
import kweb.state.KVar
import kweb.state.render
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.util.*

private fun String.decodeBase64() = String(Base64.getDecoder().decode(this))

/*
TODO: Fix code input value is empty after opening 2 files
TODO: Fix console scroll
TODO: Semantic error ui
 */
class V60(
    private val port: Int = 9000,
    private val debug: Boolean = false
) {

    val code = KVar("")
    val fileName = KVar("")
    val errors = KVar<Validated<SemanticError>>(Validated.Valid)

    lateinit var codeInput: InputElement

    init {
        Kweb(port = 9000, debug = true, plugins = listOf(monacoPlugin, ideStyle, fontAwesomePlugin)) {
            doc.body {
                div(ide.v60) {
                    div(ide.container) {
                        val actionBarContext = actionBar()

                        actionBarContext.open.onFileSelect {
                            actionBarContext.open.retrieveFile { file ->
                                fileName.value = file.fileName
                                code.value = file.base64Content
                                    .replace("data:application/octet-stream;base64,", "")
                                    .decodeBase64()
                            }
                        }

                        render(fileName) { fileNameStr ->
                            fileStatus(fileNameStr)
                        }

                        render(code) {
                            codeInput = monacoEditor {
                                id = "code-editor"
                                style = "width: 95%; height: 550px; border: 1px solid #2b2b2b; text-align: left;"
                                value = it
                            }
                        }

                        render(errors) { errorsList ->
                            console(errorsList)
                        }

                        println(codeInput.valueJsExpression)
                        actionBarContext.build.on(retrieveJs = codeInput.valueJsExpression).click {
                            val charStream = ANTLRInputStream(it.retrieved.jsonPrimitive.content)
                            val lexer = DecafLexer(charStream)
                            val tokenStream = CommonTokenStream(lexer)
                            val parser = DecafParser(tokenStream)
                            errors.value = SemanticAnalysis(parser)
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