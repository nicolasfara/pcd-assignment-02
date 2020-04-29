package it.unibo.pcd.presenter.crawler.rx

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.Crawler
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import java.lang.IllegalArgumentException

class RxSearch: Crawler {

    private val crawler: WikiCrawler = WikiCrawler()

    override fun crawl(url: String, depth: Int, callback: (Graph<WikiPage, DefaultEdge>) -> Unit) {
        val graph = DirectedAcyclicGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)
        val rootNode = WikiPage(url, crawler.getDescriptionFromPage(url), crawler.getLinksFromAbstract(url).toMutableSet(), entryNode = true)
        graph.addVertex(rootNode)

        searchLinks(PairWikiPage(rootNode.baseURL, rootNode), depth)
            .doOnComplete {
                callback(graph)
            }
            .subscribe {
                try {
                    graph.addVertex(it.child)
                    val parent = graph.vertexSet().find { e -> e.baseURL == it.parent }
                    if (parent != null) {
                        graph.addEdge(parent, it.child)
                    }
                } catch (ex: IllegalArgumentException) {
                    println("Found duplicate vertex")
                }
            }
    }

    private fun searchLinks(rootPage: PairWikiPage, depth: Int): Observable<PairWikiPage> {
        return if (depth > 0) {
            Observable.merge(Observable.just(rootPage),
            Observable.fromIterable(rootPage.child.links)
                .flatMap {
                    val node = PairWikiPage(
                        rootPage.child.baseURL,
                        WikiPage(it, crawler.getDescriptionFromPage(it), if(depth == 1) mutableSetOf() else crawler.getLinksFromAbstract(it).toMutableSet())
                    )
                    searchLinks(node, depth - 1).subscribeOn(Schedulers.io())
                })
                .subscribeOn(Schedulers.io())
        } else {
            Observable.just(rootPage)
        }
    }

    private data class PairWikiPage(val parent: String, val child: WikiPage)
}