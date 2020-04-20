package it.unibo.pcd

import java.net.HttpURLConnection
import java.net.URL

fun main(args: Array<String>) {
    // an user example url passed
    val userUrl = "https://it.wikipedia.org/wiki/Linguaggio_di_programmazione"
    sendGet(userUrl)
}

fun sendGet(userUrl: String) {
    val topic = userUrl.substringAfter("wiki/")
    val url = URL("https://it.wikipedia.org/w/api.php?action=parse&page=$topic&format=json&section=0&prop=links")

    with(url.openConnection() as HttpURLConnection) {
        requestMethod = "GET"  // optional default is GET

        println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")

        inputStream.bufferedReader().use {
            it.lines().forEach { line ->
                println(line)
            }
        }
    }
}