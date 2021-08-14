package com.github.v60.ui

import com.github.v60.ui.monacoEditor.monacoEditor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kweb.*
import kweb.state.KVar

fun ElementCreator<*>.codeEditor(
    attributes: Map<String, Any>,
    codeBinding: KVar<String>,
    runButton: ButtonElement,
): DivElement = div(attributes) {
    val codeEditor = monacoEditor {
        id = "code-editor"
        style = "width: 95%; height: 550px; border: 1px solid grey; text-align: left;"
        value = codeBinding.value
    }
    runButton.apply {
        on.click { GlobalScope.launch { codeBinding.value = codeEditor.getValue().await() } }
    }
}