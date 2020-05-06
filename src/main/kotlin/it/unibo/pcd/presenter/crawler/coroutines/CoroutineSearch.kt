package it.unibo.pcd.presenter.crawler.coroutines

import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.Crawler
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import java.util.Optional
import kotlin.collections.ArrayDeque

class CoroutineSearch : Crawler.BasicCrawler {

    private val graph = DirectedAcyclicGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)
    private val crawler: WikiCrawler = WikiCrawler()
    private val list = mutableListOf<WikiPage>()

    @ExperimentalStdlibApi
    override fun crawl(
        url: String,
        depth: Int,
        onNewPage: (Set<WikiPage>) -> Unit,
        onFinish: () -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val root = WikiPage(
                Optional.empty(),
                url,
                crawler.getDescriptionFromPage(url),
                crawler.getLinksFromAbstract(url).toSet(),
                entryNode = true
            )
            graph.addVertex(root)
            iterativeSearch(root, depth).collect {
                it.parent.ifPresent { o ->
                    val parentNode = graph.vertexSet().find { v -> v.baseURL == o }
                    graph.addVertex(it)
                    graph.addEdge(parentNode, it)
                    onNewPage(HashSet(graph.vertexSet()))
                }
            }
            onFinish()
        }
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
                queue.add(WikiPage(
                    Optional.of(node.baseURL),
                    it,
                    crawler.getDescriptionFromPage(it),
                    crawler.getLinksFromAbstract(it).toSet()
                ))
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
            }
        }
    }
}
