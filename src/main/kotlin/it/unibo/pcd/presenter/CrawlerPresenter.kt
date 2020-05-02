package it.unibo.pcd.presenter

import io.reactivex.rxjava3.schedulers.Schedulers
import it.unibo.pcd.contract.Contract
import it.unibo.pcd.presenter.crawler.coroutines.CoroutineSearch
import it.unibo.pcd.presenter.crawler.forkjoin.my.ForkJoinCrawler
import it.unibo.pcd.presenter.crawler.rx.RxCrawler
import it.unibo.pcd.presenter.crawler.vertx.VertxCrawler

class CrawlerPresenter : Contract.Presenter {

    private lateinit var view: Contract.View

    @ExperimentalStdlibApi
    override fun startSearch(url: String, depth: Int, strategy: SearchStrategy) {
        println("URL: $url DEPTH: $depth STRATEGY: $strategy")
        when (strategy) {
            SearchStrategy.COROUTINES -> {
                CoroutineSearch().crawl(url, depth)
                    .onBackpressureBuffer(5_000) { println("Backpressured") }
                    .doOnComplete { view.onFinishResult() }
                    .subscribeOn(Schedulers.computation())
                    .subscribe {
                        view.displaySearchResult(it)
                    }
            }
            SearchStrategy.FORK_JOIN -> {
                ForkJoinCrawler().crawl(url, depth)
                    .onBackpressureBuffer(5_000) { println("Backpressure") }
                    .subscribeOn(Schedulers.computation())
                    .doOnComplete { view.onFinishResult() }
                    .subscribe {
                        view.displaySearchResult(it)

                    }
            }
            SearchStrategy.REACTIVE -> {
                RxCrawler().crawl(url, depth)
                    .onBackpressureBuffer(5_000) { println("Error") }
                    .doOnComplete{ view.onFinishResult() }
                    .subscribeOn(Schedulers.computation())
                    .subscribe {
                        view.displaySearchResult(it)
                    }
                /*RxSearch().crawl(url, depth, {
                    println("New Item")
                    view.displaySearchResult(it)
                }, {
                    view.onFinishResult()
                    println("finish")
                })*/
            }
            SearchStrategy.VERTX -> {
                VertxCrawler().crawl(url, depth)
                    .onBackpressureBuffer(5_000) { println("Backpressure") }
                    .subscribeOn(Schedulers.computation())
                    .doOnComplete { view.onFinishResult() }
                    .subscribe {
                        view.displaySearchResult(it)
                    }
            }
        }
    }

    override fun attachView(view: Contract.View) {
        this.view = view
    }
}