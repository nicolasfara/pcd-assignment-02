package it.unibo.pcd.presenter.crawler.forkjoin

import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.Crawler
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool

class ForkJoinSearch: Crawler {
    private lateinit var forkJoin: LinkSearchAction

    override fun crawl(url: String, depth: Int, callback: (Graph<WikiPage, DefaultEdge>) -> Unit) {
        CompletableFuture.supplyAsync {
            val graph = DirectedAcyclicGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)
            forkJoin = LinkSearchAction(graph, depth, url)
            val fjp = ForkJoinPool.commonPool()
            fjp.invoke(forkJoin)
            graph
        }.thenAccept { callback(it) }
    }
}