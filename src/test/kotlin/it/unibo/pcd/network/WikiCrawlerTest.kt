package it.unibo.pcd.network

import it.unibo.pcd.presenter.network.WikiCrawler
import org.junit.jupiter.api.Test

internal class WikiCrawlerTest {

    private val crawler: WikiCrawler =
        WikiCrawler()
    private val baseURL = "https://it.wikipedia.org/api/rest_v1/page/html/Bertinoro"

    @Test
    fun getLinksFromAbstractTest() {
        //assertEquals(5, crawler.getLinksFromAbstract(baseURL).size)
    }

    @Test
    fun getDescriptionTest() {
        //println(crawler.getDescriptionFromPage(baseURL))
    }
}