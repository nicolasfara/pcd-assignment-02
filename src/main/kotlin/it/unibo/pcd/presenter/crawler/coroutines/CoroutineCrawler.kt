package it.unibo.pcd.presenter.crawler.coroutines

import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.Crawler
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.Optional
import kotlin.collections.ArrayDeque

class CoroutineCrawler : Crawler.BasicCrawler {

    private val crawler: WikiCrawler = WikiCrawler()
    private val list = mutableListOf<WikiPage>()

    @ExperimentalStdlibApi
    override fun crawl(
        url: String,
        depth: Int,
        onNewPage: (WikiPage) -> Unit,
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

            val f = flow {
                emit("Hello")
            }

            /*recursiveSearch(root, depth).flowOn(Dispatchers.IO).collect {
                onNewPage(it)
            }*/
            iterativeSearch(root, depth).flowOn(Dispatchers.IO).collect {
                onNewPage(it)
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

            coroutineScope {
                node.links
                    .forEach {
                        async {
                            queue.add(
                                WikiPage(
                                    Optional.of(node.baseURL),
                                    it,
                                    crawler.getDescriptionFromPage(it),
                                    crawler.getLinksFromAbstract(it).toSet()
                                )
                            )
                        }
                    }
            }
        }
    }

    @FlowPreview
    private suspend fun recursiveSearch(rootPage: WikiPage, depth: Int): Flow<WikiPage> = coroutineScope {
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
                    .flatMap {
                        recursiveSearch(it, depth - 1).toList()
                    }
                    .asFlow()
            }
            else -> { flowOf() }
        }
    }
}
