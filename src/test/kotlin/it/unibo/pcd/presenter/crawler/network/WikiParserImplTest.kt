package it.unibo.pcd.presenter.crawler.network

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.net.URL

internal class WikiParserImplTest {

    private val parser = WikiParserImpl()

    @Test
    fun getLinksInAbstract() {
        val body = URL("https://it.wikipedia.org/api/rest_v1/page/html/Bertinoro").readText()
        val links = parser.getLinksInAbstract(body)
        assertEquals(5, links.size)
    }

    @Test
    fun getDescription() {
        val json = URL("https://it.wikipedia.org/api/rest_v1/page/summary/Bertinoro").readText()
        val description = parser.getDescription(json)
        assertEquals("Bertinoro è un comune italiano di 10 947 abitanti della provincia di Forlì-Cesena." +
                " Il centro abitato è posto a circa 12 km dal centro della città di Cesena e a 15 km dal" +
                " capoluogo di provincia, Forlì.", description)
    }
}