package com.github.v60

import com.github.v60.ui.codeEditor
import com.github.v60.ui.monacoEditor.monacoEditor
import com.github.v60.ui.monacoEditor.monacoPlugin
import com.github.v60.ui.style.ideStyle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kweb.*
import kweb.plugins.fomanticUI.fomantic
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.state.KVar
import kweb.state.render

fun retrieveCode(editor: InputElement, codeVar: KVar<String>) {
    GlobalScope.launch { codeVar.value = editor.getValue().await() }
}

fun main() {
    Kweb(port = 9000, debug = true, plugins = listOf(monacoPlugin, fomanticUIPlugin, ideStyle)) {
        doc.body {
            val code = KVar("Hello world!")
            lateinit var run: ButtonElement
            div(fomantic.ui.middle.aligned.column.centered.grid) {
                div(fomantic.ui.row) {
                    div(fomantic.ui.classes("v60-top-bar")) {
                        run = button().apply { text("run") }
                    }
                }
                codeEditor(fomantic.ui.row, code, run)

                div(fomantic.ui.row) {
                    render(code) { txt ->
                        p().text(txt)
                    }
                    div(
                        attributes = mapOf(
                            "style" to "width: 95%; height: 200px; border: 1px solid grey; text-align: left; background-color: #1E1E1E; color: white;"
                        )
                    ) {
                        span {
                            p().text("../decaf-samples/precedence.decaf:20:4: semantic error: Program must contain exactly one main function")
                        }
                    }
                }

                div(fomantic.ui.row) {
                    div(
                        attributes = mapOf(
                            "style" to "width: 95%; height: 10px; border: 1px solid grey; text-align: left;"
                        )
                    ).text("status bar")
                }
            }
        }
    }
}