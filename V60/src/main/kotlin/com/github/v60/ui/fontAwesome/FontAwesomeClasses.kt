package com.github.v60.ui.fontAwesome

import kweb.AttributeBuilder
import kweb.classes

class FontAwesomeClasses : AttributeBuilder() {

    val fas get() = apply { classes("fas") }

    val flipHorizontal get() = apply { classes("fa-flip-horizontal") }

    val hammer get() = apply { classes("fa-hammer") }

    val save get() = apply { classes("fa-save") }

    val fileUpload get() = apply { classes("fa-file-upload") }

    val fileCode get() = apply { classes("fa-file-code") }

    val times get() = apply { classes("fa-times") }

    val check get() = apply { classes("fa-check") }

    val exclamationCircle get() = apply { classes("fa-exclamation-circle") }

}