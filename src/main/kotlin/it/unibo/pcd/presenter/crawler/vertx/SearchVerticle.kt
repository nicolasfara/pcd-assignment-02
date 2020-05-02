package it.unibo.pcd.presenter.crawler.vertx

import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.client.WebClient
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import it.unibo.pcd.presenter.crawler.network.WikiParserImpl
import java.util.*

class SearchVerticle(private val baseUrl: String, private val depth: Int) : AbstractVerticle() {
    private val parser = WikiParserImpl()
    private val crawler = WikiCrawler()

    override fun start() {
        vertx.eventBus().consumer<String>("chanel.search-links") {
            val message = it.body().split("!")
            val m = message[1].split("|")
            val node = WikiPage(Optional.of(message[0]), m[0], "", mutableSetOf())
            traverse(node, m[1].toInt())
        }
        val rootNode = WikiPage(
            Optional.empty(),
            baseUrl,
            crawler.getDescriptionFromPage(baseUrl),
            crawler.getLinksFromAbstract(baseUrl).toSet(), entryNode = true
        )
        traverse(rootNode, depth)
    }

    private fun traverse(root: WikiPage, depth: Int) {

        WebClient.create(vertx).getAbs(normalizeUrlForApi(root.baseURL)).send {
            if (it.succeeded()) {
                val parsed = parser.parseForLinks(it.result().body().toString())
                root.links = parsed.toMutableSet()
                parsed.forEach { el ->
                    if (depth > 0) {
                        vertx.eventBus().send("chanel.search-links", "${root.baseURL}!$el|${depth - 1}")
                        val node = WikiPage(Optional.of(root.baseURL), el, "", mutableSetOf())
                        vertx.eventBus().send("chanel.new-link", node)
                    } else {
                        vertx.eventBus().send("chanel.finish", "DONE")
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