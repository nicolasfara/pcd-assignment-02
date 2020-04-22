package it.unibo.pcd.network

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class WikiCrawler {

    private val baseWikiUrl = "https://it.wikipedia.org/wiki/"

    fun getLinksFromAbstract(pageURL: String) : Collection<String> {
        val doc: Document = Jsoup.connect(normalizeUrlForApi(pageURL)).get()
        val newsHeadlines: Elements = doc.select("section:first-child p > a:not([href*=#])")
        return newsHeadlines.map { baseWikiUrl + it.attr("title").replace(" ", "_") }
    }

    fun getDescriptionFromPage(pageURL: String): String {
        /*val topic = pageURL.substringAfter("wiki/")
        val urlGET = URL("https://en.wikipedia.org/w/api.php?action=parse&page=$topic&format=json&prop=description")
        val sBuilder = StringBuilder()
        with(urlGET.openConnection() as HttpURLConnection) {
            requestMethod = "GET"  // optional default is GET
            //println("\nSent 'GET' request to URL : $urlGET; Response Code : $responseCode")
            inputStream.bufferedReader().use {
                it.lines().forEach { line ->
                    sBuilder.append(line);
                }
            }
        }
        return sBuilder.toString()*/
        return "empty"
    }

    private fun normalizeUrlForApi(url: String): String {
        val baseUrl = "https://it.wikipedia.org/api/rest_v1/page/html/"
        return baseUrl + url.substringAfter("wiki/")
    }
}