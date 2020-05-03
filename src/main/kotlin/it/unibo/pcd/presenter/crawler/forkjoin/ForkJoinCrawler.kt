package it.unibo.pcd.presenter.crawler.forkjoin

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.PublishProcessor
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.CrawlerUtility
import it.unibo.pcd.presenter.crawler.Crawler
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool

class ForkJoinCrawler : Crawler {
    private val crawler = WikiCrawler()
    private val graph = DirectedAcyclicGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)
    private val observable = PublishProcessor.create<Set<WikiPage>>().toSerialized()

    override fun crawl(url: String, depth: Int): Flowable<Set<WikiPage>> {
        CompletableFuture.supplyAsync {
            val rootNode = WikiPage(
                Optional.empty(),
                url,
                crawler.getDescriptionFromPage(url),
                crawler.getLinksFromAbstract(url).toSet(),
                entryNode = true
            )
            graph.addVertex(rootNode)

            val fj = ForkJoinLinksSearch(rootNode, depth, crawler)
            ForkJoinPool().invoke(fj)
            fj.get().forEach {
                CrawlerUtility.addVertexToGraph(graph, it)
                    .ifPresent { s -> observable.onNext(s) }
            }
        }.thenAccept { observable.onComplete() }
        return observable
    }
}
