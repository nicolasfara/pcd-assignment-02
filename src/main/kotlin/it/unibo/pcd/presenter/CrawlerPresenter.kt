package it.unibo.pcd.presenter

import io.reactivex.rxjava3.schedulers.Schedulers
import it.unibo.pcd.contract.Contract
import it.unibo.pcd.model.WikiGraph
import it.unibo.pcd.presenter.crawler.coroutines.CoroutineCrawler
import it.unibo.pcd.presenter.crawler.forkjoin.ForkJoinCrawler
import it.unibo.pcd.presenter.crawler.rx.FlowableRxCrawler
import it.unibo.pcd.presenter.crawler.vertx.VertxCrawler

class CrawlerPresenter : Contract.Presenter {

    private lateinit var view: Contract.View
    private val graph = WikiGraph.instance

    companion object {
        private const val BUFFER_SIZE = 5_000
    }

    @ExperimentalStdlibApi
    override fun startSearch(url: String, depth: Int, strategy: SearchStrategy) {
        println("URL: $url DEPTH: $depth STRATEGY: $strategy")
        when (strategy) {
            SearchStrategy.COROUTINES -> {
                CoroutineCrawler().crawl(url, depth, {
                    graph.addVertex(it)
                    view.displaySearchResult(it)
                }, {
                    view.onFinishResult()
                    view.displayGraphSize(graph.getSize())
                })
            }
            SearchStrategy.FORK_JOIN -> {
                ForkJoinCrawler().crawl(url, depth, {
                    graph.addVertex(it)
                    view.displaySearchResult(it)
                }, {
                    view.onFinishResult()
                    view.displayGraphSize(graph.getSize())
                })
            }
            SearchStrategy.REACTIVE -> {
                FlowableRxCrawler().crawl(url, depth)
                    .onBackpressureBuffer(BUFFER_SIZE) { println("Error") }
                    .doOnComplete {
                        view.onFinishResult()
                        view.displayGraphSize(graph.getSize())
                    }
                    .subscribeOn(Schedulers.computation())
                    .subscribe {
                        graph.addVertex(it)
                        view.displaySearchResult(it)
                    }
            }
            SearchStrategy.VERTX -> {
                VertxCrawler().crawl(url, depth, {
                    graph.addVertex(it)
                    view.displaySearchResult(it)
                }, {
                    view.onFinishResult()
                    view.displayGraphSize(graph.getSize())
                })
            }
        }
    }

    override fun clearGraph() {
        graph.clearGraph()
    }

    override fun attachView(view: Contract.View) {
        this.view = view
    }
}
