package it.unibo.pcd.presenter.crawler.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.jsoup.Jsoup

class WikiParserImpl: WikiParser {

    private val jsonParser = Gson()
    private val baseWikiUrl = "https://it.wikipedia.org/wiki/"

    override fun getLinksInAbstract(body: String): Collection<String> {
        return Jsoup.parse(body)
            .select("section:first-child p > a:not([href*=#])")
            .map { baseWikiUrl + it.attr("title").replace(" ", "_") }
    }

    override fun getDescription(json: String): String {
        return jsonParser.fromJson(json, JsonObject::class.java)["extract"].asString
    }
}