package com.github.v60.ui

import kotlinx.serialization.json.JsonPrimitive
import kweb.*
import kweb.state.KVar

fun ElementCreator<*>.editor(attributes: Map<String, JsonPrimitive>, content: KVar<String>): ValueElement = textArea(attributes)
    .apply { value = content }

fun ElementCreator<*>.editor(attributes: Map<String, JsonPrimitive>, content: String): Element = textArea(attributes)
    .text(content)