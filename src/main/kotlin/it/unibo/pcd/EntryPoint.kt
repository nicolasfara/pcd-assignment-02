package it.unibo.pcd

import kotlin.IllegalArgumentException

fun main(args: Array<String>) {
    // an user example url passed
    val userUrl = "https://it.wikipedia.org/wiki/Linguaggio_di_programmazione"
    val nodeExample = Node()
    print(nodeExample.fromUrl(userUrl))

    val graph = Graph<String>()

    try {
        graph.addUniqueEdge("N1", "N2")
        graph.addUniqueEdge("N2", "N3")
        graph.addUniqueEdge("N3", "N4")
        graph.addUniqueEdge("N5", "N2")
    } catch (ex: IllegalArgumentException) {
        println(ex.message)
    }

    println(graph.toString())
    println(graph.isVertexPresent("N5"))
}

/*
    N1 -> a1, a3
    N2 -> a1, a2
    N3 -> a2, a3
 */
