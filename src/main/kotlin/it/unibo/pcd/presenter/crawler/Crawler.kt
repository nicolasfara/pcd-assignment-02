package it.unibo.pcd.presenter.crawler

import io.reactivex.rxjava3.core.Flowable
import it.unibo.pcd.model.WikiPage

interface Crawler {
    fun crawl(url: String, depth: Int): Flowable<Set<WikiPage>>
}
