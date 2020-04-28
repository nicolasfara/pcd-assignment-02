package it.unibo.pcd.presenter

import it.unibo.pcd.contract.Contract
import it.unibo.pcd.presenter.crawler.coroutines.CoroutineSearch
import it.unibo.pcd.presenter.crawler.forkjoin.ForkJoinSearch

class CrawlerPresenter: Contract.Presenter {

    private lateinit var view: Contract.View

    override fun startSearch(url: String, depth: Int, strategy: SearchStrategy) {
        println("URL: $url DEPTH: $depth STRATEGY: $strategy")
        when (strategy) {
            SearchStrategy.COROUTINES -> {
                CoroutineSearch().crawl(url, depth) {
                    view.displaySearchResult(it)
                }
            }
            SearchStrategy.FORK_JOIN -> {
                ForkJoinSearch().crawl(url, depth) {
                    view.displaySearchResult(it)
                }
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