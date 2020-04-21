package it.unibo.pcd

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.net.HttpURLConnection
import java.net.URL

data class Node(var title: String = "", var pageID: String = "", val link: MutableList<String> = mutableListOf()) {

    override fun toString(): String {
        return "Node(title='$title', pageID='$pageID', link=$link)\n"
    }

    fun fromUrl(s: String): Node {
        val string = sendGet(s)
        return parseUrl(string)
    }

    private fun sendGet(request: String): String {
        val topic = request.substringAfter("wiki/")
        val urlGET = URL("https://it.wikipedia.org/w/api.php?action=parse&page=$topic&format=json&section=0&prop=links")
        val sBuilder = StringBuilder()
        with(urlGET.openConnection() as HttpURLConnection) {
            requestMethod = "GET"  // optional default is GET
            println("\nSent 'GET' request to URL : $urlGET; Response Code : $responseCode")
            inputStream.bufferedReader().use {
                it.lines().forEach { line ->
                    sBuilder.append(line);
                }
            }
        }
        return sBuilder.toString()
    }

    private fun parseUrl(url: String): Node {
        val firstLevel = Gson()
            .fromJson(url, JsonObject::class.java)
            .get("parse")
            .asJsonObject

        val node = Node(firstLevel.get("title").toString(), firstLevel.asJsonObject.get("pageid").toString())

        firstLevel.get("links").asJsonArray.filter { it.asJsonObject.get("ns").asInt == 0 }
            .map { it.asJsonObject.get("*") }
            .forEach { node.link.add(it.toString()) }

        return node
    }
}
