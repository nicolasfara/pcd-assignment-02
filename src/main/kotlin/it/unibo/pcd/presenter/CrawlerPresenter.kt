package it.unibo.pcd.presenter

import io.reactivex.rxjava3.schedulers.Schedulers
import it.unibo.pcd.contract.Contract
import it.unibo.pcd.presenter.crawler.coroutines.CoroutineSearch
import it.unibo.pcd.presenter.crawler.forkjoin.ForkJoinCrawler
import it.unibo.pcd.presenter.crawler.rx.FlowableRxCrawler
import it.unibo.pcd.presenter.crawler.vertx.VertxCrawler

class CrawlerPresenter : Contract.Presenter {

    private lateinit var view: Contract.View
    companion object {
        private const val BUFFER_SIZE = 5_000
    }

    @ExperimentalStdlibApi
    override fun startSearch(url: String, depth: Int, strategy: SearchStrategy) {
        println("URL: $url DEPTH: $depth STRATEGY: $strategy")
        when (strategy) {
            SearchStrategy.COROUTINES -> {
                CoroutineSearch().crawl(url, depth, {
                    view.displaySearchResult(it)
                }, {
                    view.onFinishResult()
                })
            }
            SearchStrategy.FORK_JOIN -> {
                ForkJoinCrawler().crawl(url, depth, {
                    view.displaySearchResult(it)
                }, {
                    view.onFinishResult()
                })
            }
            SearchStrategy.REACTIVE -> {
                FlowableRxCrawler().crawl(url, depth)
                    .onBackpressureBuffer(BUFFER_SIZE) { println("Error") }
                    .doOnComplete { view.onFinishResult() }
                    .subscribeOn(Schedulers.computation())
                    .subscribe {
                        view.displaySearchResult(it)
                    }
            }
            SearchStrategy.VERTX -> {
                VertxCrawler().crawl(url, depth, {
                    view.displaySearchResult(it)
                }, {
                    view.onFinishResult()
                })
            }
        }
    }

    override fun attachView(view: Contract.View) {
        this.view = view
    }
}
