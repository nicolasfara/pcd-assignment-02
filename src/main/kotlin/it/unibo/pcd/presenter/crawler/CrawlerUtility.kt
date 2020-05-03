package it.unibo.pcd.presenter.crawler

import it.unibo.pcd.model.WikiPage
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import java.util.Optional

object CrawlerUtility {
    fun addVertexToGraph(graph: Graph<WikiPage, DefaultEdge>, vertex: WikiPage): Optional<Set<WikiPage>> {
        var ret = Optional.empty<Set<WikiPage>>()
        vertex.parent.ifPresent { e ->
            val parentNode = graph.vertexSet().find { v -> v.baseURL == e }
            if (!graph.vertexSet().map { v -> v.baseURL }.contains(vertex.baseURL)) {
                graph.addVertex(vertex)
                graph.addEdge(parentNode, vertex)
                ret = Optional.of(HashSet(graph.vertexSet()))
            }
        }
        return ret
    }
}
