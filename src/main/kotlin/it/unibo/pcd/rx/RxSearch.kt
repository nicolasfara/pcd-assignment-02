package it.unibo.pcd.rx

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.internal.subscribers.SubscriberResourceWrapper
import io.reactivex.rxjava3.schedulers.Schedulers
import it.unibo.pcd.data.WikiPage
import it.unibo.pcd.network.WikiCrawler
import org.reactivestreams.Subscriber
import java.util.concurrent.Flow

class RxSearch {

    private val crawler: WikiCrawler = WikiCrawler()

    fun search(url: String, depth: Int) {
        /*val rootNode = WikiPage(url, crawler.getDescriptionFromPage(url), crawler.getLinksFromAbstract(url).toMutableSet(), entryNode = true)
        return searchLinks(rootNode, depth)*/
        val rootNode = WikiPage(url, crawler.getDescriptionFromPage(url), crawler.getLinksFromAbstract(url).toMutableSet())

        searchLinks2(rootNode, depth)
            .subscribe {
                println(Thread.currentThread().name)
                println(it)
            }

        println("Finish")
    }

    private fun searchLinks2(rootPage: WikiPage, depth: Int): Observable<WikiPage> {
        return if (depth > 0) {
            Observable.fromIterable(rootPage.links)
                .map {
                    WikiPage(
                        it, crawler.getDescriptionFromPage(it),
                        crawler.getLinksFromAbstract(it).toMutableSet()
                    )
                }
                .flatMap {
                    searchLinks2(it, depth - 1)
                }
                .observeOn(Schedulers.io())
        } else {
            Observable.just(rootPage)
        }
    }

    private fun getObservableLinks(url: String): Observable<String> {
        return Observable.fromIterable(crawler.getLinksFromAbstract(url))

    }

    private fun searchLinks(rootPage: WikiPage, depth: Int): Observable<Pair<WikiPage, WikiPage>> {
        if (depth > 0) {
            return Observable.merge(
                Observable.just(Pair(rootPage, WikiPage("", ""))),
                Observable.fromIterable(rootPage.links)
                    .observeOn(Schedulers.io())
                    .flatMap { getWikiPageFromUrl(rootPage, it, depth) }
                    .flatMap { searchLinks(it.second, depth-1) }
            )
        }
        return Observable.just(Pair(rootPage, WikiPage("", "")))
    }

    private fun getWikiPageFromUrl(parent: WikiPage, url: String, depth: Int): Observable<Pair<WikiPage, WikiPage>> {
        return Observable.just(
            Pair(parent, WikiPage(url, crawler.getDescriptionFromPage(url),
                if (depth == 1) mutableSetOf() else crawler.getLinksFromAbstract(url).toMutableSet()))
        )
    }
}