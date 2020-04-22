package it.unibo.pcd.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder

class WikiCrawler {

    private val baseWikiURL = "https://it.wikipedia.org/wiki/"

    fun getLinksFromAbstract(pageURL: String) : Collection<String> {
        return parseUrl(sendGet(pageURL))
    }

    fun getDescriptionFromPage(pageURL: String): String {
        val topic = pageURL.substringAfter("wiki/")
        val urlGET = URL("https://it.wikipedia.org/w/api.php?action=parse&page=$topic&format=json&prop=description")
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
        return sBuilder.toString()
    }

    private fun sendGet(request: String): String {
        val topic = request.substringAfter("wiki/")
        val urlGET = URL(URLDecoder.decode("https://it.wikipedia.org/w/api.php?action=parse&page=$topic&format=json&section=0&prop=links", "UTF-8"))
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
        return sBuilder.toString()
    }

    private fun parseUrl(url: String): Collection<String> {
        val firstLevel = Gson()
            .fromJson(url, JsonObject::class.java)
            .get("parse")
            .asJsonObject

        return firstLevel.get("links").asJsonArray.filter { it.asJsonObject.get("ns").asInt == 0 }
            .map { baseWikiURL + it.asJsonObject.get("*").asString.replace(" " , "_") }
    }
}