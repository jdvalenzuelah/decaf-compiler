package com.github.v60.ui.monacoEditor

import kweb.*

data class MonacoEditorContext(
    var id: String = "",
    var style: String = "",
    var value: String = "",
)

fun ElementCreator<Element>.monacoEditor(init: MonacoEditorContext.() -> Unit): InputElement {
    val context = MonacoEditorContext().apply(init)
    require(context.id.isNotEmpty()) { "id is required to hook editor" }

    div(mapOf("id" to context.id, "style" to context.style)).also {
        element("script")
            .text(
                """
                require.config({ paths: { vs: '/static/monaco/vs' } });
        
                require(['vs/editor/editor.main'], function () {
                    var editor = monaco.editor.create(document.getElementById('${context.id}'), {
                        value: '${context.value}',
                        language: 'javascript',
                        theme: 'vs-dark',
                    });
                    
                    editor.getModel().onDidChangeContent(function(event) {
                      document.getElementById("input-${context.id}").value = monaco.editor.getModels()[0].getValue();
                    });
                }); 
                """.trimIndent()
            )
    }

    return input(mapOf("id" to "input-${context.id}", "style" to "display: none;"))
}