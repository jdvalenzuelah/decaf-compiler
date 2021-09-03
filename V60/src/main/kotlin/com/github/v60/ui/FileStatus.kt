package com.github.v60.ui

import com.github.v60.ui.fontAwesome.fontAwesome
import com.github.v60.ui.style.ide
import kweb.DivElement
import kweb.ElementCreator
import kweb.div
import kweb.i

fun ElementCreator<*>.fileStatus(
    fileName: String
): DivElement {
    return div(ide.fileStatus) {
        div(ide.file) {
            div(ide.fileIcon.left) { i(fontAwesome.fas.fileCode) }
            div(ide.fileName.left).text(fileName)
            div(ide.fileClose.left.btn) { i(fontAwesome.fas.times) }
        }
    }
}