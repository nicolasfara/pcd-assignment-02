package it.unibo.pcd.presenter.crawler.forkjoin.my

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.processors.FlowableProcessor
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.subjects.PublishSubject
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.Crawler
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import org.jgrapht.graph.concurrent.AsSynchronizedGraph
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool

class ForkJoinCrawler: Crawler {
    private val crawler = WikiCrawler()
    private val graph = DirectedAcyclicGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)
    private val observable = PublishProcessor.create<Graph<WikiPage, DefaultEdge>>()

    override fun crawl(url: String, depth: Int): FlowableProcessor<Graph<WikiPage, DefaultEdge>> {
        CompletableFuture.supplyAsync {
            val rootNode = WikiPage(Optional.empty(), url, crawler.getDescriptionFromPage(url), crawler.getLinksFromAbstract(url).toSet(), entryNode = true)
            graph.addVertex(rootNode)

            val fj = ForkJoinLinksSearch(rootNode, depth, crawler)
            ForkJoinPool().invoke(fj)
            fj.get().forEach {
                it.parent.ifPresent { parent ->
                    val parentNode = graph.vertexSet().find { v -> v.baseURL == parent }
                    if (!graph.vertexSet().map { v -> v.baseURL }.contains(it.baseURL)) {
                        graph.addVertex(it)
                        graph.addEdge(parentNode, it)
                        observable.onNext(AsSynchronizedGraph.Builder<WikiPage, DefaultEdge>().build(graph))
                    }
                }
            }
        }.thenAccept { observable.onComplete() }
        return observable
    }
}
