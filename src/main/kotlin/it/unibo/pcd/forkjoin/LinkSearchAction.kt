package it.unibo.pcd.forkjoin

import it.unibo.pcd.data.Graph
import it.unibo.pcd.data.WikiPage
import it.unibo.pcd.network.WikiCrawler
import java.util.concurrent.RecursiveAction

class LinkSearchAction(private val graph: Graph<WikiPage>, val depth: Int = 5, val startURL: String) : RecursiveAction() {

    private val crawler: WikiCrawler = WikiCrawler()
    private val tasks: MutableList<LinkSearchAction> = mutableListOf()

    override fun compute() {
        if (depth > 0) {
            createSubAction()
        }
        tasks.forEach{ it.join() }
    }

    private fun createSubAction() {
        var baseVertex = graph.getAllVertex()
            .find { it.baseURL == startURL }

        if (baseVertex == null)
            baseVertex = WikiPage(startURL, crawler.getDescriptionFromPage(startURL))

        baseVertex.links.addAll(crawler.getLinksFromAbstract(startURL))
        try {
            baseVertex.links.forEach {
                graph.addUniqueEdge(
                    baseVertex,
                    WikiPage(it, crawler.getDescriptionFromPage(it), mutableSetOf())
                )
            }
        } catch (ex: IllegalArgumentException) {
            println("Link already found")
        }

        baseVertex.links.forEach {
            val lsa = LinkSearchAction(graph, depth-1, it)
            tasks.add(lsa)
            lsa.fork()
        }
    }
}