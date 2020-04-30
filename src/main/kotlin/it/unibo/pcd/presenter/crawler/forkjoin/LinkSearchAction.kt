package it.unibo.pcd.presenter.crawler.forkjoin

import io.reactivex.rxjava3.subjects.PublishSubject
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import java.util.concurrent.RecursiveAction
import javax.security.auth.Subject

class LinkSearchAction(
    private val graph: DirectedAcyclicGraph<WikiPage, DefaultEdge>,
    private val depth: Int = 5,
    private val startURL: String,
    private val subject: PublishSubject<Graph<WikiPage, DefaultEdge>>
): RecursiveAction() {

    private val crawler: WikiCrawler =
        WikiCrawler()

    override fun compute() {
        if (depth > 0) {
            createSubAction()
        }
    }

    private fun createSubAction() {
        var currentVertex = graph.vertexSet().find { it.baseURL == startURL }

        if (crawler.getDescriptionFromPage(startURL) != "Url not found") {
            // If no vertex are present in the graph, we create the entry point
            if (currentVertex == null) {
                currentVertex = WikiPage(startURL, crawler.getDescriptionFromPage(startURL), entryNode = true)
            }

            currentVertex.links.addAll(crawler.getLinksFromAbstract(startURL)) // Create all links inside the page
            graph.addVertex(currentVertex) // Add the page (vertex) to the graph
            if (currentVertex.links.isNotEmpty()) {
                currentVertex.links.forEach {
                    val linkVertex = WikiPage(it, crawler.getDescriptionFromPage(it), mutableSetOf())
                    // If the page is already present will not be add
                    if (!graph.vertexSet().map { e -> e.baseURL }.contains(linkVertex.baseURL)) {
                        graph.addVertex(linkVertex)
                        graph.addEdge(currentVertex, linkVertex)
                        subject.onNext(graph)
                    }
                }
            }

            val list = mutableListOf<LinkSearchAction>()

            currentVertex.links.forEach {
                val lsa = LinkSearchAction(graph, depth - 1, it, subject)
                lsa.fork()
                list.add(lsa)
                //lsa.join()
            }

            list.forEach { it.join() }
        }
    }
}