package it.unibo.pcd.presenter.crawler.rx

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.processors.FlowableProcessor
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.Crawler
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import java.util.*
import kotlin.collections.HashSet

class RxCrawler: Crawler {

    private val observable = PublishProcessor.create<Set<WikiPage>>().toSerialized()
    private val graph = DirectedAcyclicGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)
    private val crawler = WikiCrawler()

    override fun crawl(url: String, depth: Int): Flowable<Set<WikiPage>> {
        val rootNode = WikiPage(Optional.empty(), url, crawler.getDescriptionFromPage(url), crawler.getLinksFromAbstract(url).toSet(), entryNode = true)
        graph.addVertex(rootNode)
        return searchLinks(rootNode, depth)
            .doOnEach {
                if (it.value != null) {
                    it.value.parent.ifPresent { e ->
                        val parentNode = graph.vertexSet().find { v -> v.baseURL == e }
                        if (!graph.vertexSet().map { v -> v.baseURL }.contains(it.value.baseURL)) {
                            graph.addVertex(it.value)
                            graph.addEdge(parentNode, it.value)
                            observable.onNext(HashSet(graph.vertexSet()))
                        }
                    }
                }
            }.map { HashSet(graph.vertexSet()) }
    }

    private fun searchLinks(root: WikiPage, depth: Int): Flowable<WikiPage> {
        return if (depth > 0) {
            Flowable.merge(Flowable.just(root),
            Flowable.fromIterable(root.links)
                .flatMap {
                    val node = WikiPage(Optional.of(root.baseURL), it, crawler.getDescriptionFromPage(it), crawler.getLinksFromAbstract(it).toSet())
                    searchLinks(node, depth-1).observeOn(Schedulers.io())
                }).observeOn(Schedulers.io())
        } else {
            Flowable.just(root)
        }
    }
}