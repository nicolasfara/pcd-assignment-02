package it.unibo.pcd.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URL

class WikiCrawler {

    private val baseWikiUrl = "https://it.wikipedia.org/wiki/"

    fun getLinksFromAbstract(pageURL: String) : Collection<String> {
        val doc: Document = Jsoup.connect(normalizeUrlForApi(pageURL)).get()
        val newsHeadlines: Elements = doc.select("section:first-child p > a:not([href*=#])")
        return newsHeadlines.map { baseWikiUrl + it.attr("title").replace(" ", "_") }
    }

    fun getDescriptionFromPage(pageURL: String): String {
        var str = ""
        try {
            str = URL("https://it.wikipedia.org/api/rest_v1/page/summary/" + pageURL.substringAfter("wiki/")).readText()
        } catch (ex: FileNotFoundException) {
            println("Url not found")
            return "URL not found"
        }
        return Gson().fromJson(str, JsonObject::class.java)["extract"].asString
    }

    private fun normalizeUrlForApi(url: String): String {
        val baseUrl = "https://it.wikipedia.org/api/rest_v1/page/html/"
        return baseUrl + url.substringAfter("wiki/")
    }
}