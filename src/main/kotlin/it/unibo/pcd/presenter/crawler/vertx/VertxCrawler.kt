package it.unibo.pcd.presenter.crawler.vertx

import GenericCodec
import io.reactivex.rxjava3.processors.FlowableProcessor
import io.reactivex.rxjava3.processors.PublishProcessor
import io.vertx.core.Vertx
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.Crawler
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
import java.util.*

class VertxCrawler : Crawler {
    private val vertx = Vertx.vertx()
    private val graph = SimpleDirectedGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)
    private val observable = PublishProcessor.create<Set<WikiPage>>().toSerialized()
    private val crawler = WikiCrawler()
    override fun crawl(url: String, depth: Int): FlowableProcessor<Set<WikiPage>> {
        val codec = GenericCodec(WikiPage::class.java)
        vertx.eventBus().registerDefaultCodec(WikiPage::class.java, codec)

        val rootNode = WikiPage(Optional.empty(), url, crawler.getDescriptionFromPage(url), crawler.getLinksFromAbstract(url).toSet(), entryNode = true)
        graph.addVertex(rootNode)
        vertx.eventBus().consumer<WikiPage>("chanel.new-link") {
            it.body().parent.ifPresent { e ->
                val parentNode = graph.vertexSet().find { v -> v.baseURL == e }
                if (!graph.vertexSet().map { v -> v.baseURL }.contains(it.body().baseURL)) {
                    graph.addVertex(it.body())
                    graph.addEdge(parentNode, it.body())
                    observable.onNext(HashSet(graph.vertexSet()))
                }
            }
        }
        vertx.eventBus().consumer<String>("chanel.finish") {
            observable.onComplete()
        }

        vertx.deployVerticle(SearchVerticle(url, depth))
        return observable
    }
}