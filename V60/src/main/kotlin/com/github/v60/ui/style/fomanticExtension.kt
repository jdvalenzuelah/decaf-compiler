package com.github.v60.ui.style

import kweb.classes
import kweb.plugins.fomanticUI.FomanticUIClasses

/*
 Extend fomantic UI classes to use ide style classes.
*/

val FomanticUIClasses.console get() = apply { classes("console") }