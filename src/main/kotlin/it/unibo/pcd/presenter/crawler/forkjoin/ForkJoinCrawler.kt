package it.unibo.pcd.presenter.crawler.forkjoin

import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool

class ForkJoinCrawler {
    private val crawler = WikiCrawler()
    private val graph = DirectedAcyclicGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)

    fun crawl(url: String, depth: Int, onNewPage: (Set<WikiPage>) -> Unit, onFinish: () -> Unit) {
        CompletableFuture.supplyAsync {
            val rootNode = WikiPage(
                Optional.empty(),
                url,
                crawler.getDescriptionFromPage(url),
                crawler.getLinksFromAbstract(url).toSet(),
                entryNode = true
            )
            graph.addVertex(rootNode)

            val fj = ForkJoinLinksSearch(rootNode, depth, crawler) {
                val parentNode = graph.vertexSet().find { v -> v.baseURL == it.parent.get() }
                graph.addVertex(it)
                graph.addEdge(parentNode, it)
                onNewPage(HashSet(graph.vertexSet()))
            }
            ForkJoinPool().invoke(fj)
        }.thenAccept { onFinish() }
    }
}
