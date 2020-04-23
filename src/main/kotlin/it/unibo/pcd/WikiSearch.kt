package it.unibo.pcd

import it.unibo.pcd.data.WikiPage
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph

class WikiSearch(var depth: Int, var url: String) {

    fun search(function: (Int, String) -> SimpleDirectedGraph<WikiPage, DefaultEdge>, onResult: (res: SimpleDirectedGraph<WikiPage, DefaultEdge>) -> Unit) {
       Thread { onResult(function(depth, url)) }.start()
    }
}