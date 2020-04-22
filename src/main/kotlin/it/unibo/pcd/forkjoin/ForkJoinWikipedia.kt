package it.unibo.pcd.forkjoin

import it.unibo.pcd.data.Graph
import it.unibo.pcd.data.WikiPage
import java.util.concurrent.ForkJoinPool


fun main(args: Array<String>) {

    val graph = Graph<WikiPage>()
    val search = LinkSearchAction(graph, depth = 2, startURL = "https://it.wikipedia.org/wiki/Bertinoro")

    val commonPool = ForkJoinPool()
    commonPool.invoke(search)

    print(graph)
}

