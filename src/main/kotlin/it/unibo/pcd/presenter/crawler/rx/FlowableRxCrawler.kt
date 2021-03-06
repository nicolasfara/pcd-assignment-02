package it.unibo.pcd.presenter.crawler.rx

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.Crawler
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import java.util.Optional

class FlowableRxCrawler : Crawler.RxCrawler {

    private val crawler = WikiCrawler()

    override fun crawl(url: String, depth: Int): Flowable<WikiPage> {
        val rootNode = WikiPage(
            Optional.empty(),
            url,
            crawler.getDescriptionFromPage(url),
            crawler.getLinksFromAbstract(url).toSet(),
            entryNode = true
        )

        return searchLinks(rootNode, depth)
    }

    private fun searchLinks(root: WikiPage, depth: Int): Flowable<WikiPage> {
        return if (depth > 0) {
            Flowable.merge(Flowable.just(root),
            Flowable.fromIterable(root.links)
                .flatMap {
                    val node = WikiPage(
                        Optional.of(root.baseURL),
                        it,
                        crawler.getDescriptionFromPage(it),
                        crawler.getLinksFromAbstract(it).toSet()
                    )
                    searchLinks(node, depth - 1).observeOn(Schedulers.io())
                }).observeOn(Schedulers.io())
        } else {
            Flowable.just(root)
        }
    }
}
