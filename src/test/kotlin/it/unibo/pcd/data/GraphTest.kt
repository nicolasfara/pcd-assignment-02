package it.unibo.pcd.data

import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
import org.jgrapht.graph.SimpleGraph
import org.jgrapht.traverse.BreadthFirstIterator
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class GraphTest {

    private val graph: Graph<String> = Graph()
    private val graph2 = SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)

    @Test
    fun getAllVertex() {

        graph2.addVertex("V1")
        graph2.addVertex("V2")
        graph2.addVertex("V3")
        graph2.addVertex("V4")

        graph2.addEdge("V1", "V2")
        graph2.addEdge("V1", "V3")
        graph2.addEdge("V3", "V4")
        graph2.addEdge("V4", "V1")

        val iterator = BreadthFirstIterator(graph2, "V1")
        while (iterator.hasNext()) {
            val e = iterator.next()
            println(e + " " + iterator.getDepth(e))
        }
    }
}