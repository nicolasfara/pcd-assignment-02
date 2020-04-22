package it.unibo.pcd.forkjoin

import it.unibo.pcd.data.Graph
import it.unibo.pcd.data.WikiPage
import java.util.concurrent.ForkJoinPool


fun main(args: Array<String>) {

    val graph = Graph<WikiPage>()
    val search = LinkSearchAction(graph, depth = 3, startURL = "https://it.wikipedia.org/wiki/Citt%C3%A0_del_Vaticano")

    val commonPool = ForkJoinPool()
    commonPool.invoke(search)

    print(graph)
}

