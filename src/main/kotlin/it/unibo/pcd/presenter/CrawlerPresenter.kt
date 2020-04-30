package it.unibo.pcd.presenter

import it.unibo.pcd.contract.Contract
import it.unibo.pcd.presenter.crawler.coroutines.CoroutineSearch
import it.unibo.pcd.presenter.crawler.forkjoin.ForkJoinSearch
import it.unibo.pcd.presenter.crawler.rx.RxSearch

class CrawlerPresenter: Contract.Presenter {

    private lateinit var view: Contract.View

    override fun startSearch(url: String, depth: Int, strategy: SearchStrategy) {
        println("URL: $url DEPTH: $depth STRATEGY: $strategy")
        when (strategy) {
            SearchStrategy.COROUTINES -> {
                CoroutineSearch().crawl(url, depth, {
                    println("New Item")
                    view.displaySearchResult(it)
                }, {
                    view.onFinishResult()
                    println("finish")
                })
            }
            SearchStrategy.FORK_JOIN -> {
                ForkJoinSearch().crawl(url, depth, {
                    println("New Item")
                    view.displaySearchResult(it)
                }, {
                    view.onFinishResult()
                    println("Finish")
                })
            }
            SearchStrategy.REACTIVE -> {
                RxSearch().crawl(url, depth, {
                    println("New Item")
                    view.displaySearchResult(it)
                }, {
                    view.onFinishResult()
                    println("finish")
                })
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