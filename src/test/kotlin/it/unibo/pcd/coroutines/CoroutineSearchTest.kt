package it.unibo.pcd.coroutines

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class CoroutineSearchTest {

    private val search: CoroutineSearch = CoroutineSearch()

    @Test
    fun searchLinks() {
        runBlocking {
            val graph = search.searchLinks("https://it.wikipedia.org/wiki/Bertinoro", 1)
            assertEquals(6, graph.vertexSet().size)
        }
    }
}