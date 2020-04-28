package it.unibo.pcd.rx

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class RxSearchTest {

    private val rxSearch = RxSearch()

    @Test
    fun search() {
        rxSearch.search("https://it.wikipedia.org/wiki/Bertinoro", 2)
    }
}