package com.github.v60.ui.style

import kweb.plugins.KwebPlugin
import kweb.plugins.staticFiles.ResourceFolder
import kweb.plugins.staticFiles.StaticFilesPlugin
import org.jsoup.nodes.Document

private const val resourceFolder = "css"
private const val resourceRoute = "/static/v60"

class IdeStyle : KwebPlugin(dependsOn = setOf(StaticFilesPlugin(ResourceFolder(resourceFolder), resourceRoute))) {

    override fun decorate(doc: Document) {
        doc.head().appendElement("link")
            .attr("rel", "stylesheet")
            .attr("type", "text/css")
            .attr("href", "$resourceRoute/ide.css")
    }

}

val ideStyle get() = IdeStyle()

val ide get() = IdeStyleClasses()