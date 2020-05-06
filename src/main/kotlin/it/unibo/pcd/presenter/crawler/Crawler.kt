package it.unibo.pcd.presenter.crawler

import io.reactivex.rxjava3.core.Flowable
import it.unibo.pcd.model.WikiPage

interface Crawler {
    interface BasicCrawler {
        fun crawl(url: String, depth: Int, onNewPage: (WikiPage) -> Unit, onFinish: () -> Unit)
    }

    interface RxCrawler {
        fun crawl(url: String, depth: Int): Flowable<WikiPage>
    }
}
