package com.github.v60.ui.style

import kweb.AttributeBuilder
import kweb.classes

class IdeStyleClasses : AttributeBuilder() {

    val v60 get() = apply { classes("v60") }

    val container get() = apply { classes("container") }

    val topBar get() = apply { classes("top-bar") }

    val fileStatus get() = apply { classes("file-status") }

    val file get() = apply { classes("file") }

    val fileName get() = apply { classes("file-name") }

    val fileIcon get() = apply { classes("file-icon") }

    val fileClose get() = apply { classes("file-close") }

    val editor get() = apply { classes("editor") }

    val console get() = apply { classes("console") }

    val buildStatus get() = apply { classes("build-status") }

    val buildOutput get() = apply { classes("build-output") }

    val successBuildIcon get() = apply { classes("success-build-icon") }

    val failedBuildIcon get() = apply { classes("failed-build-icon") }

    val saveIcon get() = apply { classes("save-icon") }

    val openFileIcon get() = apply { classes("open-file-icon") }

    val buildIcon get() = apply { classes("build-icon") }

    val btn get() = apply { classes("btn") }

    val left get() = apply { classes("left") }

    val scrollY get() = apply { classes("scroll-y") }

    val codeEditor get() = apply { classes("code-editor") }

    val error get() = apply { classes("error") }

    val irStatus get() = apply { classes("ir-status-bar") }
}