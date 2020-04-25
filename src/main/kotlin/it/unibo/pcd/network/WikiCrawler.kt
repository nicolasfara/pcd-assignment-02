package it.unibo.pcd.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URL

class WikiCrawler {

    private val baseWikiUrl = "https://it.wikipedia.org/wiki/"

    fun getLinksFromAbstract(pageURL: String) : Collection<String> {
        var headlines = emptyList<String>()
        try{
            val doc: Document = Jsoup.connect(normalizeUrlForApi(pageURL)).get()
            val newsHeadlines: Elements = doc.select("section:first-child p > a:not([href*=#])")
            headlines = newsHeadlines.map { baseWikiUrl + it.attr("title").replace(" ", "_") }
        } catch (ex: HttpStatusException){
            println("Url not exist")
        }
        return headlines
    }

    fun getDescriptionFromPage(pageURL: String): String {
        val str: String
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