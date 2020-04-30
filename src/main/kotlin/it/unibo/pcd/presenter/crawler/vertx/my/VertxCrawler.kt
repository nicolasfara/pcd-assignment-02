package it.unibo.pcd.presenter.crawler.vertx.my

import GenericCodec
import io.vertx.core.Vertx
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.Crawler
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph

class VertxCrawler: Crawler {

    private val vertx = Vertx.vertx()
    private val graph = SimpleDirectedGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)

    override fun crawl(
        url: String,
        depth: Int,
        objectEmit: (Graph<WikiPage, DefaultEdge>) -> Unit,
        onComplete: () -> Unit
    ) {
        val codec = GenericCodec(WikiPair::class.java)
        vertx.eventBus().registerDefaultCodec(WikiPair::class.java, codec)

        vertx.eventBus().consumer<WikiPair>("chanel.new-link") {
            val root = graph.vertexSet().find { e -> e.baseURL == it.body().root.baseURL }
            if (root == null) graph.addVertex(it.body().root)

            if (!graph.vertexSet().map { e -> e.baseURL }.contains(it.body().child.baseURL)) {
                graph.addVertex(it.body().child)
                graph.addEdge(root, it.body().child)
                objectEmit(graph)
            }
        }
        vertx.eventBus().consumer<String>("chanel.finish") {
            onComplete()
        }
        vertx.deployVerticle(VertxSearch(url, depth))
    }
}