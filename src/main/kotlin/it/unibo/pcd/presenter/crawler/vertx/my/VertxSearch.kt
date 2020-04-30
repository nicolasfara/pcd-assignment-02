package it.unibo.pcd.presenter.crawler.vertx.my

import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.client.WebClient
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import it.unibo.pcd.presenter.crawler.network.WikiParserImpl

class VertxSearch(private val baseUrl: String, private val depth: Int): AbstractVerticle() {

    private val parser = WikiParserImpl()
    private val crawler = WikiCrawler()

    override fun start() {
        vertx.eventBus().consumer<String>("chanel.search-links") {
            val message = it.body().split("|")
            val node = WikiPage(message[0], "")
            traverse(node, message[1].toInt())
        }
        val rootNode = WikiPage(baseUrl, "", crawler.getLinksFromAbstract(baseUrl).toMutableSet(), entryNode = true)
        traverse(rootNode, depth)
    }

    private fun traverse(root: WikiPage, depth: Int) {
        WebClient.create(vertx).getAbs(normalizeUrlForApi(root.baseURL)).send {
            if (it.succeeded()) {
                val parsed = parser.parseForLinks(it.result().body().toString())
                root.links = parsed.toMutableSet()
                parsed.forEach { el ->
                    if (depth > 0) {
                        vertx.eventBus().send("chanel.search-links", "$el|${depth-1}")
                        val node = WikiPair(root, WikiPage(el, "", mutableSetOf()))
                        println(node)
                        vertx.eventBus().send("chanel.new-link", node)
                    } else {
                        vertx.eventBus().send("chanel.finish", 1)
                    }
                }
            } else {
                error("Error")
            }
        }
    }

    private fun normalizeUrlForApi(url: String): String {
        val baseUrl = "https://it.wikipedia.org/api/rest_v1/page/html/"
        return baseUrl + url.substringAfter("wiki/")
    }
}
