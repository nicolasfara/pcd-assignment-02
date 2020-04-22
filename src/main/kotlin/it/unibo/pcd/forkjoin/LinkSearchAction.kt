package it.unibo.pcd.forkjoin

import it.unibo.pcd.data.WikiPage
import it.unibo.pcd.network.WikiCrawler
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
import java.util.concurrent.RecursiveAction

class LinkSearchAction(private val graph: SimpleDirectedGraph<WikiPage, DefaultEdge>, val depth: Int = 5, val startURL: String) : RecursiveAction() {

    private val crawler: WikiCrawler = WikiCrawler()
    private val tasks: MutableList<LinkSearchAction> = mutableListOf()

    override fun compute() {
        if (depth > 0) {
            createSubAction()
        }
        tasks.forEach { it.join() }
    }

    private fun createSubAction() {
        var baseVertex = graph.vertexSet().find { it.baseURL == startURL }

        if (baseVertex == null)
            baseVertex = WikiPage(startURL, crawler.getDescriptionFromPage(startURL), entryNode = true)

        baseVertex.links.addAll(crawler.getLinksFromAbstract(startURL)) //Create all links inside the page
        graph.addVertex(baseVertex) // Add the page (vertex) to the graph

        baseVertex.links.forEach {
            val linkVertex = WikiPage(it, crawler.getDescriptionFromPage(it), mutableSetOf())
            // If the page is already present will not be add
            if (!graph.vertexSet().map { e -> e.baseURL }.contains(linkVertex.baseURL)) {
                graph.addVertex(linkVertex)
                graph.addEdge(baseVertex, linkVertex)
            }
        }

        baseVertex.links.forEach {
            val lsa = LinkSearchAction(graph, depth-1, it)
            tasks.add(lsa)
            lsa.fork()
        }
    }
}