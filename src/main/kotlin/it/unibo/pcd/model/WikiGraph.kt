package it.unibo.pcd.model

import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph

class WikiGraph private constructor() {

    private val graph = DirectedAcyclicGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)

    private object GetInstance {
        val INSTANCE = WikiGraph()
    }

    companion object {
        val instance: WikiGraph by lazy { GetInstance.INSTANCE }
    }

    fun addVertex(vertex: WikiPage) {
        if (!graph.containsVertex(vertex)) {
            graph.addVertex(vertex)
        } else {
            println("Vertex already present")
        }

        vertex.parent.ifPresent {
            val parentNode = graph.vertexSet().find { it.baseURL == vertex.parent.get() }
            if (parentNode != null) {
                addEdge(parentNode, vertex)
            }
        }
    }

    fun getVertexSet(): Set<WikiPage> {
        return graph.vertexSet()
    }

    fun clearGraph() {
        removeAllEdges()
        removeAllVertices()
    }

    fun getSize(): Int {
        return graph.vertexSet().size
    }

    private fun addEdge(rootPage: WikiPage, childPage: WikiPage) {
        try {
            graph.addEdge(rootPage, childPage)
        } catch (ex: IllegalArgumentException) {
            println("Vertex already present")
        }
    }

    private fun removeAllVertices() {
        val vertexCopy = mutableListOf<WikiPage>()
        vertexCopy.addAll(graph.vertexSet())
        graph.removeAllVertices(vertexCopy)
    }

    private fun removeAllEdges() {
        val edgesCopy = mutableListOf<DefaultEdge>()
        edgesCopy.addAll(graph.edgeSet())
        graph.removeAllEdges(edgesCopy)
    }
}
