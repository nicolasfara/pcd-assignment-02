package it.unibo.pcd.rx

import io.reactivex.rxjava3.core.Observable
import it.unibo.pcd.data.WikiPage
import it.unibo.pcd.network.WikiCrawler

class RxSearch {

    private val crawler: WikiCrawler = WikiCrawler()

    fun searchLinks(rootPage: WikiPage, depth: Int): Observable<Pair<WikiPage, WikiPage>> {
        TODO()
    }
}