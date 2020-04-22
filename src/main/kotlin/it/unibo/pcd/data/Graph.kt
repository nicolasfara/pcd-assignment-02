package it.unibo.pcd.data

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.IllegalArgumentException
import kotlin.collections.HashSet

class Graph<T> {

    private val adjacencyMap: ConcurrentHashMap<T, HashSet<T>> = ConcurrentHashMap()

    companion object {
        /**
         * Depth first traversal leverages a [Stack] (LIFO).
         *
         * It's possible to use recursion instead of using this iterative
         * implementation using a [Stack].
         * Also, this algorithm is almost the same as [breadthFirstTraversal],
         * except that [Stack] (LIFO) is replaced w/ a [Queue] (FIFO).
         *
         * [More info](https://stackoverflow.com/a/35031174/2085356).
         */
        fun <T> depthFirstTraversal(graph: Graph<T>, startNode: T): String {
            // Mark all the vertices / nodes as not visited.
            val visitedMap = mutableMapOf<T, Boolean>().apply {
                graph.adjacencyMap.keys.forEach { node -> put(node, false) }
            }

            // Create a stack for DFS. Both ArrayDeque and LinkedList implement Deque.
            val stack: Deque<T> = LinkedList()

            // Initial step -> add the startNode to the stack.
            stack.push(startNode)

            // Store the sequence in which nodes are visited, for return value.
            val traversalList = mutableListOf<T>()

            // Traverse the graph.
            while (stack.isNotEmpty()) {
                // Pop the node off the top of the stack.
                val currentNode = stack.pop()

                if (!visitedMap[currentNode]!!) {

                    // Store this for the result.
                    traversalList.add(currentNode)

                    // Mark the current node visited and add to the traversal list.
                    visitedMap[currentNode] = true

                    // Add nodes in the adjacency map.
                    graph.adjacencyMap[currentNode]?.forEach { node ->
                        stack.push(node)
                    }
                }
            }
            return traversalList.joinToString()
        }
    }

    fun isVertexPresent(vertex: T): Boolean {
        return adjacencyMap.values.flatten().contains(vertex)
    }

    fun addEdge(sourceVertex: T, destinationVertex: T) {
        // Add edge to source vertex / node.
        adjacencyMap
            .computeIfAbsent(sourceVertex) { HashSet() }
            .add(destinationVertex)
        // Add edge to destination vertex / node.
        adjacencyMap
            .computeIfAbsent(destinationVertex) { HashSet() }
            .add(sourceVertex)
    }

    fun addUniqueEdge(sourceVertex: T, destinationVertex: T) {
        when(!isVertexPresent(destinationVertex)) {
            true -> addEdge(sourceVertex, destinationVertex)
            else -> throw IllegalArgumentException("The vertex `$destinationVertex` is already present in the graph")
        }
    }

    fun getAllVertex(): Set<T> {
        return adjacencyMap.values.flatten().toSet()
    }




    override fun toString(): String = StringBuffer().apply {
        for (key in adjacencyMap.keys) {
            append("$key -> ")
            append(adjacencyMap[key]?.joinToString(", ", "[", "]\n"))
        }
    }.toString()
}