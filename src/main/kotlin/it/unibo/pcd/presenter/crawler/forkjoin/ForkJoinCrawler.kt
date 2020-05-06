package it.unibo.pcd.presenter.crawler.forkjoin

import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.Crawler
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool

class ForkJoinCrawler : Crawler.BasicCrawler {
    private val crawler = WikiCrawler()

    override fun crawl(url: String, depth: Int, onNewPage: (WikiPage) -> Unit, onFinish: () -> Unit) {
        CompletableFuture.supplyAsync {
            val rootNode = WikiPage(
                Optional.empty(),
                url,
                crawler.getDescriptionFromPage(url),
                crawler.getLinksFromAbstract(url).toSet(),
                entryNode = true
            )
            onNewPage(rootNode)

            val fj = ForkJoinLinksSearch(rootNode, depth, crawler) {
                onNewPage(it)
            }
            ForkJoinPool().invoke(fj)
        }.thenAccept { onFinish() }
    }
}
