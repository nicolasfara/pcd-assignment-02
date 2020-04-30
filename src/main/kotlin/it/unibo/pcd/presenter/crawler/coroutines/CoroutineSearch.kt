package it.unibo.pcd.presenter.crawler.coroutines

import io.reactivex.rxjava3.subjects.PublishSubject
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.Crawler
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import kotlinx.coroutines.*
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph

class CoroutineSearch: Crawler {

    private lateinit var graph: DirectedAcyclicGraph<WikiPage, DefaultEdge>
    private val crawler: WikiCrawler = WikiCrawler()
    private val subject = PublishSubject.create<Graph<WikiPage, DefaultEdge>>()

    override fun crawl(
        url: String,
        depth: Int,
        objectEmit: (Graph<WikiPage, DefaultEdge>) -> Unit,
        onComplete: () -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            subject.subscribe { objectEmit(it) }
            graph = DirectedAcyclicGraph(DefaultEdge::class.java)
            val rootNode = WikiPage(url, crawler.getDescriptionFromPage(url), crawler.getLinksFromAbstract(url).toMutableSet(), entryNode = true)
            search(rootNode, depth)
            subject.onComplete()
            onComplete()
        }
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
                            subject.onNext(graph)
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