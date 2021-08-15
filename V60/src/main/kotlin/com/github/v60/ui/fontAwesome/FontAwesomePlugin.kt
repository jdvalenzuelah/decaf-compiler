package com.github.v60.ui.fontAwesome

import kweb.plugins.KwebPlugin
import kweb.plugins.staticFiles.ResourceFolder
import kweb.plugins.staticFiles.StaticFilesPlugin
import org.jsoup.nodes.Document

private const val resourceFolder = "font-awesome"
private const val resourceRoute = "/static/fontawesome"

class FontAwesomePlugin : KwebPlugin(dependsOn = setOf(StaticFilesPlugin(ResourceFolder(resourceFolder), resourceRoute))) {

    override fun decorate(doc: Document) {
        doc.head().appendElement("link")
            .attr("rel", "stylesheet")
            .attr("type", "text/css")
            .attr("href", "$resourceRoute/css/all.min.css")
    }

}

val fontAwesomePlugin get() = FontAwesomePlugin()

val fontAwesome get() = FontAwesomeClasses()