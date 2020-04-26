package it.unibo.pcd.vertx
import io.vertx.core.AbstractVerticle
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import it.unibo.pcd.data.WikiPage
import it.unibo.pcd.network.WikiCrawler
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph

class VerticleLinkAnalizer(
    private var graph: SimpleDirectedGraph<WikiPage, DefaultEdge>,
    val depth: Int = 1,
    val startURL: String
) : AbstractVerticle() {
    private val log: Logger = LoggerFactory.getLogger(VerticleLinkAnalizer::class.java)
    private val crawler: WikiCrawler = WikiCrawler()
    override fun start() {
        graph = SimpleDirectedGraph(DefaultEdge::class.java)
        val rootNode = WikiPage(
            startURL,
            crawler.getDescriptionFromPage(startURL),
            crawler.getLinksFromAbstract(startURL).toMutableSet(),
            entryNode = true
        )

        if (depth > 0)
            traverseLink(rootNode, depth)
    }

    private fun traverseLink(rootNode: WikiPage, depth: Int): SimpleDirectedGraph<WikiPage, DefaultEdge> {

        if (depth > 0) {
            graph.addVertex(rootNode)
            if (rootNode.links.isNotEmpty()) {
                // Call some blocking API that takes a significant amount of time to return
                vertx.executeBlocking<Any>({ promise ->
                    rootNode.links.forEach {
                        val linkVertex = WikiPage(it, crawler.getDescriptionFromPage(it), mutableSetOf())
                        // If the page is already present will not be add
                        if (!graph.vertexSet().map { e -> e.baseURL }.contains(linkVertex.baseURL)) {
                            graph.addVertex(linkVertex)
                            graph.addEdge(rootNode, linkVertex)
                            traverseLink(linkVertex, depth - 1)
                        }
                    }
                    promise.complete(graph)
                }, { res ->
                    println("The result is: ${res.result()}")
                })
            }
        }
        return graph
    }

}

