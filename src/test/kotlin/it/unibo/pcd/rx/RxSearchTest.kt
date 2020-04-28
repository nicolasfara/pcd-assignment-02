package it.unibo.pcd.rx

import it.unibo.pcd.presenter.crawler.rx.RxSearch
import org.junit.jupiter.api.Test

internal class RxSearchTest {

    private val rxSearch = RxSearch()

    @Test
    fun search() {
        rxSearch.search("https://it.wikipedia.org/wiki/Bertinoro", 2)
    }
}