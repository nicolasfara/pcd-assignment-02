package it.unibo.pcd.presenter.crawler.vertx

import GenericCodec
import io.vertx.core.Vertx
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.Crawler
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import java.util.Optional

class VertxCrawler : Crawler.BasicCrawler {
    private val vertx = Vertx.vertx()
    private val crawler = WikiCrawler()

    override fun crawl(url: String, depth: Int, onNewPage: (WikiPage) -> Unit, onFinish: () -> Unit) {
        val codec = GenericCodec(WikiPage::class.java)
        vertx.eventBus().registerDefaultCodec(WikiPage::class.java, codec)

        val rootNode = WikiPage(
            Optional.empty(),
            url,
            crawler.getDescriptionFromPage(url),
            crawler.getLinksFromAbstract(url).toSet(),
            entryNode = true
        )
        onNewPage(rootNode)
        vertx.eventBus().consumer<WikiPage>("chanel.new-link") {
            onNewPage(it.body())
        }
        vertx.eventBus().consumer<String>("chanel.finish") {
            vertx.close()
            onFinish()
        }

        vertx.deployVerticle(SearchVerticle(url, depth))
    }
}
