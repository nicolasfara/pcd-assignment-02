package it.unibo.pcd.presenter.crawler.rx

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class Rx2SearchTest {

    private val rx = Rx2Search()

    @Test
    fun crawl() {
        rx.crawl("https://it.wikipedia.org/wiki/Bertinoro", 3, {

        }, {
            println("FINISH")
        })
    }
}