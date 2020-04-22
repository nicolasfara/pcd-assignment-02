package it.unibo.pcd.forkjoin

import it.unibo.pcd.data.WikiPage
import it.unibo.pcd.network.WikiCrawler
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
import java.util.*
import java.util.concurrent.RecursiveAction

class LinkSearchAction(private val graph: SimpleDirectedGraph<WikiPage, DefaultEdge>, val depth: Int = 5, val startURL: String) : RecursiveAction() {

    private val crawler: WikiCrawler = WikiCrawler()
    private val tasks: MutableList<LinkSearchAction> = Collections.synchronizedList(mutableListOf())

    override fun compute() {
        if (depth > 0) {
            createSubAction()
        }
        tasks.forEach { it.join() }
    }

    private fun createSubAction() {
        var currentVertex = graph.vertexSet().find { it.baseURL == startURL }

        // If no vertex are present in the graph, we create the entry point
        if (currentVertex == null)
            currentVertex = WikiPage(startURL, crawler.getDescriptionFromPage(startURL), entryNode = true)

        currentVertex.links.addAll(crawler.getLinksFromAbstract(startURL)) // Create all links inside the page
        graph.addVertex(currentVertex) // Add the page (vertex) to the graph

        currentVertex.links.forEach {
            val linkVertex = WikiPage(it, crawler.getDescriptionFromPage(it), mutableSetOf())
            // If the page is already present will not be add
            if (!graph.vertexSet().map { e -> e.baseURL }.contains(linkVertex.baseURL)) {
                graph.addVertex(linkVertex)
                graph.addEdge(currentVertex, linkVertex)
            }
        }

        currentVertex.links.forEach {
            val lsa = LinkSearchAction(graph, depth-1, it)
            tasks.add(lsa)
            lsa.fork()
        }
    }
}