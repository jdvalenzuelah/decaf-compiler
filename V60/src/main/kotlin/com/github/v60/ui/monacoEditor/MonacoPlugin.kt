package com.github.v60.ui.monacoEditor

import kweb.plugins.KwebPlugin
import kweb.plugins.staticFiles.ResourceFolder
import kweb.plugins.staticFiles.StaticFilesPlugin
import org.jsoup.nodes.Document

private const val resourceFolder = "monaco-editor/min"
private const val resourceRoute = "/static/monaco"

class MonacoPlugin : KwebPlugin(dependsOn = setOf(StaticFilesPlugin(ResourceFolder(resourceFolder), resourceRoute))) {

    override fun decorate(doc: Document) {
        doc.body().appendElement("script")
            .attr("src", "$resourceRoute/vs/loader.js") //TODO: Change to minified
    }

}

val monacoPlugin get() = MonacoPlugin()
