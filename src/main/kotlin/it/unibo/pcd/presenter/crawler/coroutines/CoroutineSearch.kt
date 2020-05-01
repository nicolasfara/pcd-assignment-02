package it.unibo.pcd.presenter.crawler.coroutines

import io.reactivex.rxjava3.processors.FlowableProcessor
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.Crawler
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import kotlinx.coroutines.*
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import org.jgrapht.graph.concurrent.AsSynchronizedGraph
import java.lang.IllegalArgumentException
import java.util.*

class CoroutineSearch: Crawler {

    private val graph = DirectedAcyclicGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)
    private val crawler: WikiCrawler = WikiCrawler()
    private val observable = PublishProcessor.create<Graph<WikiPage, DefaultEdge>>().toSerialized()
    private val list = mutableListOf<WikiPage>()

    override fun crawl(url: String, depth: Int): FlowableProcessor<Graph<WikiPage, DefaultEdge>> {
        CoroutineScope(Dispatchers.IO).launch {
            val root = WikiPage(Optional.empty(), url, crawler.getDescriptionFromPage(url), crawler.getLinksFromAbstract(url).toSet(), entryNode = true)
            graph.addVertex(root)
            recursiveSearch(root, depth)
            list
                //.map { it.await() }
                .forEach {
                    it.parent.ifPresent { e ->
                        val parentNode = graph.vertexSet().find { v -> v.baseURL == e }
                        if (!graph.vertexSet().map { v -> v.baseURL }.contains(it.baseURL)) {
                            graph.addVertex(it)
                            graph.addEdge(parentNode, it)
                            observable.onNext(AsSynchronizedGraph.Builder<WikiPage, DefaultEdge>().build(graph))
                        }
                    }
            }
            observable.onComplete()
        }
        return observable
    }

    private suspend fun recursiveSearch(rootPage: WikiPage, depth: Int): Unit = coroutineScope {

        return@coroutineScope when {
            depth > 0 -> {
                rootPage.links
                    .map {
                        async {
                            val node = WikiPage(
                                Optional.of(rootPage.baseURL),
                                it,
                                crawler.getDescriptionFromPage(it),
                                crawler.getLinksFromAbstract(it).toSet()
                            )
                            node
                        }
                    }
                    .map { it.await() }
                    .forEach {
                        list.add(it)
                        val node = it
                        recursiveSearch(node, depth - 1)
                    }
            }
            else -> {
                //list.add(rootPage)
                //list
            }
        }
    }
}
