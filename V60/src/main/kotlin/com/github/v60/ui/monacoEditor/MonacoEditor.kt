package com.github.v60.ui.monacoEditor

import kweb.*
import kweb.util.json

data class MonacoEditorContext(
    var id: String = "",
    var style: String = "",
    var value: String = "",
    var readOnly: Boolean = false,
)

private fun String.jsEscape() = this.replace("\n", "\\n").replace("'", "\\'")

private fun String.jsLineList(): String = "[" + this.lines().joinToString(separator = ",") { "'${it.jsEscape()}'" } + "]"

fun ElementCreator<Element>.monacoEditor(init: MonacoEditorContext.() -> Unit): InputElement {
    val context = MonacoEditorContext().apply(init)
    require(context.id.isNotEmpty()) { "id is required to hook editor" }
    div(mapOf("id" to context.id.json, "style" to context.style.json)).also {
        element("script")
            .text(
                """
                document.getElementById("input-${context.id}").value = ${context.value.jsLineList()}.join('\n');
                 
                require.config({ paths: { vs: '/static/monaco/vs' } });
        
                require(['vs/editor/editor.main'], function () {
                    monaco.editor.getModels().forEach(model => model.dispose());
                    var editor = monaco.editor.create(document.getElementById('${context.id}'), {
                        value: ${context.value.jsLineList()}.join('\n'),
                        language: 'javascript',
                        theme: 'vs-dark',
                        readOnly: ${context.readOnly},
                    });
                    
                    editor.getModel().onDidChangeContent(function(event) {
                      document.getElementById("input-${context.id}").value = monaco.editor.getModels()[0].getValue();
                    });
                    
                }); 
                """.trimIndent()
            )
    }

    return input(mapOf("id" to "input-${context.id}".json, "style" to "display: none;".json))
}