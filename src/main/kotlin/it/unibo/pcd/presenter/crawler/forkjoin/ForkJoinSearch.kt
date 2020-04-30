package it.unibo.pcd.presenter.crawler.forkjoin

import io.reactivex.rxjava3.subjects.PublishSubject
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.Crawler
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool

class ForkJoinSearch: Crawler {
    private lateinit var forkJoin: LinkSearchAction
    private val subject = PublishSubject.create<Graph<WikiPage, DefaultEdge>>()

    override fun crawl(
        url: String,
        depth: Int,
        objectEmit: (Graph<WikiPage, DefaultEdge>) -> Unit,
        onComplete: () -> Unit
    ) {
        subject.subscribe { objectEmit(it) }
        CompletableFuture.supplyAsync {
            val graph = DirectedAcyclicGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)
            forkJoin = LinkSearchAction(graph, depth, url, subject)
            val fjp = ForkJoinPool.commonPool()
            fjp.invoke(forkJoin)
            graph
        }.thenAccept { onComplete() }
    }
}