package it.unibo.pcd.presenter

import it.unibo.pcd.contract.Contract
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.coroutines.CoroutineSearch
import it.unibo.pcd.presenter.forkjoin.LinkSearchAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
import java.util.concurrent.ForkJoinPool

class CrawlerPresenter: Contract.Presenter {

    private lateinit var view: Contract.View

    override fun startSearch(url: String, depth: Int, strategy: SearchStrategy) {
        println("URL: $url DEPTH: $depth STRATEGY: $strategy")
        when (strategy) {
            SearchStrategy.COROUTINES -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val graph = CoroutineSearch().searchLinks(url, depth)
                    view.displaySearchResult(graph)
                }
            }
            SearchStrategy.FORK_JOIN -> {
                val wikiSearch = WikiSearch(depth, url)
                wikiSearch.search({ d: Int, u: String ->
                    val graph = SimpleDirectedGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)
                    val fjp = ForkJoinPool.commonPool()
                    fjp.invoke(LinkSearchAction(graph, d, u))
                    graph
                }, {
                    view.displaySearchResult(it)
                })
            }
            SearchStrategy.REACTIVE -> {

            }
            SearchStrategy.VERTX -> {
                println("Vertx")
            }
        }
    }

    override fun attachView(view: Contract.View) {
        this.view = view
    }
}