package it.unibo.pcd.presenter.crawler.vertx.my

import io.vertx.core.Vertx
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class VertxSearchTest {

    private val vertx = Vertx.vertx()

    @Test
    fun testVerticle() {
        vertx.eventBus().consumer<String>("chanel.finish") {
            println("Finish")
        }
        vertx.eventBus().consumer<String>("chanel.new-link") {
            println(it.body())
        }
        vertx.deployVerticle(VertxSearch("https://it.wikipedia.org/wiki/Bertinoro", 2))
    }
}