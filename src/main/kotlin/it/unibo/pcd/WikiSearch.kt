package it.unibo.pcd

import it.unibo.pcd.data.WikiPage
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
import java.util.concurrent.CompletableFuture

class WikiSearch(var depth: Int, var url: String) {

    fun <T> search(function: (Int, String) -> T, onResult: (res: T) -> Unit) {
        CompletableFuture.supplyAsync { function(depth, url) }
            .thenAccept { onResult(it) }
    }

    fun <T> search(function: (Int, String) -> T) {
        function(depth, url)
    }
}