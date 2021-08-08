package com.github.v60.ui.monacoEditor

import kweb.DivElement
import kweb.Element
import kweb.ElementCreator
import kweb.div

//TODO: Figure out a way to extract value (bind to kvar?)
fun ElementCreator<Element>.monacoEditor(
    id: String = "editor-container",
    style: String = "height: 650px; border: 1px solid grey",
): DivElement {
    return div(mapOf("id" to id, "style" to style)).also {
        element("script")
            .text("""
                require.config({ paths: { vs: '/static/monaco' } });
        
                require(['vs/editor/editor.main'], function () {
                    var editor = monaco.editor.create(document.getElementById('$id'), {
                        value: '',
                        language: 'javascript',
                        theme: 'vs-dark',
                    });
                });
                """.trimIndent())
    }
}