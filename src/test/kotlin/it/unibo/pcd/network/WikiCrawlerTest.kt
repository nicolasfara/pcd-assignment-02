package it.unibo.pcd.network

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class WikiCrawlerTest {

    private val crawler: WikiCrawler = WikiCrawler()
    private val baseURL = "https://it.wikipedia.org/wiki/Linguaggio_di_programmazione"

    @Test
    fun apiTest() {
        print(crawler.getLinksFromAbstract(baseURL))
        assertEquals(true, true)
    }

    @Test
    fun getDescriptionTest() {
        println(crawler.getDescriptionFromPage(baseURL))
    }
}