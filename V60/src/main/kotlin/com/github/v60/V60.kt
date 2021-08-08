package com.github.v60

import com.github.v60.ui.monacoEditor.monacoEditor
import com.github.v60.ui.monacoEditor.monacoPlugin
import com.github.v60.ui.style.console
import com.github.v60.ui.style.ideStyle
import kweb.*
import kweb.plugins.fomanticUI.fomantic
import kweb.plugins.fomanticUI.fomanticUIPlugin


fun main() {
    Kweb(port = 9000, debug = true, plugins = listOf(monacoPlugin, fomanticUIPlugin, ideStyle)) {
        doc.body {
            div(fomantic.ui.middle.aligned.column.centered.grid) {
                div(fomantic.ui.row) {
                    div(
                        attributes = mapOf(
                            "style" to "width: 95%; height: 20px; border: 1px solid grey; text-align: left;"
                        )
                    ).text("Top bar")
                }
                div(fomantic.ui.row) {
                    monacoEditor(
                        id = "code-editor",
                        style = "width: 95%; height: 550px; border: 1px solid grey; text-align: left;"
                    )
                }

                div(fomantic.ui.row) {
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