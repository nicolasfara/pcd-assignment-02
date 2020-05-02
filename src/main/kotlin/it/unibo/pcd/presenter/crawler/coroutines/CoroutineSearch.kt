package it.unibo.pcd.presenter.crawler.coroutines

import io.reactivex.rxjava3.processors.FlowableProcessor
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.Crawler
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.jgrapht.Graph
import org.jgrapht.graph.AsUnmodifiableGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import org.jgrapht.graph.concurrent.AsSynchronizedGraph
import tornadofx.runAsync
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.ArrayDeque

class CoroutineSearch: Crawler {

    private val graph = DirectedAcyclicGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)
    private val crawler: WikiCrawler = WikiCrawler()
    private val observable = PublishProcessor.create<Set<WikiPage>>().toSerialized()
    private val list = mutableListOf<WikiPage>()

    @ExperimentalStdlibApi
    override fun crawl(url: String, depth: Int): FlowableProcessor<Set<WikiPage>> {
        CoroutineScope(Dispatchers.IO).launch {
            val root = WikiPage(Optional.empty(), url, crawler.getDescriptionFromPage(url), crawler.getLinksFromAbstract(url).toSet(), entryNode = true)
            graph.addVertex(root)
            //recursiveSearch(root, depth)
            /*list
                //.map { it.await() }
                .forEach {
                    /*it.parent.ifPresent { e ->
                        val parentNode = graph.vertexSet().find { v -> v.baseURL == e }
                        if (!graph.vertexSet().map { v -> v.baseURL }.contains(it.baseURL)) {
                            graph.addVertex(it)
                            graph.addEdge(parentNode, it)
                            observable.onNext(AsSynchronizedGraph.Builder<WikiPage, DefaultEdge>().build(AsUnmodifiableGraph(graph)))
                        }
                    }*/
                    observable.onNext(it)
            }
            observable.onComplete()*/
            iterativeSearch(root, depth).collect {
                it.parent.ifPresent { e ->
                    val parentNode = graph.vertexSet().find { v -> v.baseURL == e }
                    if (!graph.vertexSet().map { v -> v.baseURL }.contains(it.baseURL)) {
                        graph.addVertex(it)
                        graph.addEdge(parentNode, it)
                        observable.onNext(HashSet(graph.vertexSet()))
                    }
                }
            }
            observable.onComplete()
        }
        return observable
    }

    @ExperimentalStdlibApi
    private fun iterativeSearch(rootPage: WikiPage, depth: Int): Flow<WikiPage> = flow {
        val queue = ArrayDeque<WikiPage>()
        queue.add(rootPage)

        var currentDepth = 0
        var elementsToDepthIncrease = 1
        var nextElementsToDepthIncrease = 0

        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            emit(node)
            nextElementsToDepthIncrease += node.links.size
            if (--elementsToDepthIncrease == 0) {
                if (++currentDepth > depth) return@flow
                elementsToDepthIncrease = nextElementsToDepthIncrease
                nextElementsToDepthIncrease = 0
            }

            node.links.parallelStream().forEach {
                queue.add(WikiPage(Optional.of(node.baseURL), it, crawler.getDescriptionFromPage(it), crawler.getLinksFromAbstract(it).toSet()))
            }
        }
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
