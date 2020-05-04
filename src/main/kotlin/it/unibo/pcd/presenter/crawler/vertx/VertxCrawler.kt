package it.unibo.pcd.presenter.crawler.vertx

import GenericCodec
import io.vertx.core.Vertx
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.CrawlerUtility
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
import java.util.Optional

class VertxCrawler {
    private val vertx = Vertx.vertx()
    private val graph = SimpleDirectedGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)
    private val crawler = WikiCrawler()
    fun crawl(url: String, depth: Int, onNewPage: (Set<WikiPage>) -> Unit, onFinish: () -> Unit) {
        val codec = GenericCodec(WikiPage::class.java)
        vertx.eventBus().registerDefaultCodec(WikiPage::class.java, codec)

        val rootNode = WikiPage(
            Optional.empty(),
            url,
            crawler.getDescriptionFromPage(url),
            crawler.getLinksFromAbstract(url).toSet(),
            entryNode = true
        )
        graph.addVertex(rootNode)
        vertx.eventBus().consumer<WikiPage>("chanel.new-link") {
            CrawlerUtility.addVertexToGraph(graph, it.body())
                .ifPresent { s -> onNewPage(s) }
        }
        vertx.eventBus().consumer<String>("chanel.finish") {
            vertx.close()
            onFinish()
        }

        vertx.deployVerticle(SearchVerticle(url, depth))
    }
}
