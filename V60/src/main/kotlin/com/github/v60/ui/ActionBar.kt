package com.github.v60.ui

import com.github.v60.ui.fontAwesome.fontAwesome
import com.github.v60.ui.style.ide
import kweb.*
import kweb.html.fileUpload.FileFormInput

data class ActionBarContext(
    val open: FileFormInput,
    val save: ButtonElement,
    val build: ButtonElement
)

fun ElementCreator<*>.actionBar(): ActionBarContext {
    lateinit var build: ButtonElement
    lateinit var open: FileFormInput
    lateinit var save: ButtonElement

    div(ide.topBar) {
        label(ide.btn) {
            open = fileInput()
            span(ide.openFileIcon) {
                i(fontAwesome.fas.fileUpload)
            }
        }

        save = button(ide.btn) {
            span(ide.saveIcon) {
                i(fontAwesome.fas.save)
            }
        }
        build = button(ide.btn) {
            span(ide.buildIcon) {
                i(fontAwesome.fas.hammer.flipHorizontal)
            }
        }
    }

    return ActionBarContext(open, save, build)
}