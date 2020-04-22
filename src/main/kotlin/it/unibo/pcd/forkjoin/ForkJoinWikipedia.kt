package it.unibo.pcd.forkjoin

import it.unibo.pcd.data.WikiPage
import org.jgrapht.Graphs
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
import org.jgrapht.traverse.BreadthFirstIterator

import java.util.concurrent.ForkJoinPool

fun main()  {

    val graph = SimpleDirectedGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)
    val search = LinkSearchAction(graph, depth = 2, startURL = "https://it.wikipedia.org/wiki/Bertinoro")

    val commonPool = ForkJoinPool()
    commonPool.invoke(search)

    val gIterator = BreadthFirstIterator(graph, graph.vertexSet().find { it.entryNode })

    while (gIterator.hasNext()) {
        val vertex = gIterator.next()
        println(vertex.toString() + " depth: " + gIterator.getDepth(vertex))
    }

    graph.vertexSet().forEach {
        println("Links of ${it.baseURL} = " + Graphs.successorListOf(graph, it) + "\n")
    }
}

