package it.unibo.pcd.presenter.crawler.coroutines

import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph

class CoroutineSearch {

    private lateinit var graph: DirectedAcyclicGraph<WikiPage, DefaultEdge>
    private val crawler: WikiCrawler =
        WikiCrawler()

    suspend fun searchLinks(url: String, depth: Int): DirectedAcyclicGraph<WikiPage, DefaultEdge> {
        graph = DirectedAcyclicGraph(DefaultEdge::class.java)
        val rootNode = WikiPage(url, crawler.getDescriptionFromPage(url), crawler.getLinksFromAbstract(url).toMutableSet(), entryNode = true)
        return search(rootNode, depth)
    }

    private suspend fun search(rootPage: WikiPage, depth: Int): DirectedAcyclicGraph<WikiPage, DefaultEdge> = coroutineScope {
        if (depth > 0) {
            graph.addVertex(rootPage)
            rootPage.links
                .map {
                    async {
                        val currNode = WikiPage(it, crawler.getDescriptionFromPage(it),
                            if (depth == 1) mutableSetOf() else crawler.getLinksFromAbstract(it).toMutableSet()
                        )
                        try {
                            graph.addVertex(currNode)
                            graph.addEdge(rootPage, currNode)
                            search(currNode, depth - 1)
                        } catch (ex: Exception) {
                            when (ex) {
                                is NullPointerException -> println("null value")
                                is IllegalArgumentException -> println("Duplicate value")
                            }
                            graph.removeVertex(currNode)
                        }
                    }
                }
                .awaitAll()
        }
        return@coroutineScope graph
    }
}