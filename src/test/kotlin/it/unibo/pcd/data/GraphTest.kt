package it.unibo.pcd.data

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class GraphTest {

    private val graph: Graph<String> = Graph()

    @Test
    fun getAllVertex() {
        graph.addUniqueEdge("E1", "E2")
        graph.addUniqueEdge("E2", "E3")

        println(graph.getAllVertex().find { it == "E1" })

    }
}