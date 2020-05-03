package it.unibo.pcd.presenter.crawler.vertx

import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.client.WebClient
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import it.unibo.pcd.presenter.crawler.network.WikiParserImpl
import java.util.Optional

class SearchVerticle(private val baseUrl: String, private val depth: Int) : AbstractVerticle() {
    private val parser = WikiParserImpl()
    private val crawler = WikiCrawler()
    private val apiLink = "https://it.wikipedia.org/api/rest_v1/page/html/"
    private val apiDescription = "https://it.wikipedia.org/api/rest_v1/page/summary/"

    override fun start() {
        vertx.eventBus().consumer<String>("chanel.search-links") { message ->
            val messageChannel = message.body().split("|")
            val node = WikiPage(Optional.of(messageChannel[0]), messageChannel[1], "", mutableSetOf())
            traverse(node, messageChannel[2].toInt())
        }
        val rootNode = WikiPage(
            Optional.empty(),
            baseUrl,
            crawler.getDescriptionFromPage(baseUrl),
            mutableSetOf(), entryNode = true
        )
        if (depth > 0) {
            traverse(rootNode, depth)
        }
    }

    private fun traverse(root: WikiPage, depth: Int) {

        WebClient.create(vertx).getAbs(prepareUrl(root.baseURL, apiLink)).send { links ->
            if (links.succeeded()) {
                val parsed = parser.parseForLinks(links.result().body().toString())
                root.links = parsed.toMutableSet()
                parsed.forEach { link ->
                    if (depth > 0) {
                        vertx.eventBus().send("chanel.search-links", "${root.baseURL}|$link|${depth - 1}")
                        val node = WikiPage(Optional.of(root.baseURL), link, clientDescription(link), mutableSetOf())
                        vertx.eventBus().send("chanel.new-link", node)
                    } else {
                        vertx.eventBus().send("chanel.finish", "DONE")
                    }
                }
            } else {
                println("Url not exist")
            }
        }
    }

    private fun clientDescription(elem: String): String {
        var response = " "
        WebClient.create(vertx).getAbs(prepareUrl(elem, apiDescription)).send { desc ->
            if (desc.succeeded()) {
                response = parser.parseForDescription(desc.result().body().toString())
            } else {
                println("Description not exist")
            }
        }
        return response
    }

    private fun prepareUrl(url: String, baseUrl: String): String {
        return baseUrl + url.substringAfter("wiki/")
    }
}
