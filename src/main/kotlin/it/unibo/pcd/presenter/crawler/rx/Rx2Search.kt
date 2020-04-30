package it.unibo.pcd.presenter.crawler.rx

import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.Crawler
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import java.util.concurrent.Executors

class Rx2Search: Crawler {

    private val crawler = WikiCrawler()

    override fun crawl(
        url: String,
        depth: Int,
        objectEmit: (Graph<WikiPage, DefaultEdge>) -> Unit,
        onComplete: () -> Unit
    ) {
        val processor = PublishProcessor.create<WikiPage>().toSerialized()
        processor
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.computation())
            .doOnComplete {
                println("onComplete")
                onComplete()
            }
            .subscribe {
                println("onNext ${Thread.currentThread()}")
            }

        //Executors.newSingleThreadExecutor().submit {
            recursiveSearch(url, depth).forEach {
                processor.onNext(it)
            }
            processor.onComplete()
        //}
    }

    private fun recursiveSearch(url: String, depth: Int): Collection<WikiPage> {
        val list = mutableListOf<WikiPage>()
        val page = WikiPage(url, crawler.getDescriptionFromPage(url), crawler.getLinksFromAbstract(url).toMutableSet())
        list.add(page)
        if (depth > 0) {
            page.links.forEach {
                list.addAll(recursiveSearch(it, depth-1))
            }
        }
        return list
    }
}