package it.unibo.pcd.presenter.crawler

import it.unibo.pcd.model.WikiPage
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge

interface Crawler {
    fun crawl(url: String, depth: Int, objectEmit: (Graph<WikiPage, DefaultEdge>) -> Unit, onComplete: () -> Unit)
}