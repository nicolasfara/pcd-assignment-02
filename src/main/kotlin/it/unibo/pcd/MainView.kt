package it.unibo.pcd

import it.unibo.pcd.data.WikiPage
import it.unibo.pcd.forkjoin.LinkSearchAction
import javafx.application.Platform
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import org.jgrapht.Graphs
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
import tornadofx.*
import java.util.concurrent.ForkJoinPool

class MainView : View("Wiki Link Search ") {
    override val root: BorderPane by fxml("/MainView.fxml")
    private val graphPane: StackPane by fxid()
    private val wikiUrl: TextField by fxid()
    private val depth: TextField by fxid()
    private val progress: ProgressIndicator by fxid()
    private val searchBtn: Button by fxid()

    fun search() {
        progress.isVisible = true
        graphPane.isDisable = true
        searchBtn.isDisable = true

        val wikiSearch = WikiSearch(depth.text.toInt(), wikiUrl.text)
        wikiSearch.search({ depth: Int, url:String ->
            val graph = SimpleDirectedGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)
            val fjp = ForkJoinPool.commonPool()
            fjp.invoke(LinkSearchAction(graph, depth, url))
            graph
        }, {
            Platform.runLater {
                val treeItem: TreeItem<String> = TreeItem()
                graphToTree(it, it.vertexSet().first { el -> el.entryNode }, treeItem)
                val treeView: TreeView<String> = TreeView(treeItem)
                treeView.isShowRoot = false
                graphPane.children.add(treeView)
                progress.isVisible = false
                graphPane.isDisable = false
                searchBtn.isDisable = false
            }
        })
    }

    private fun graphToTree(graph: SimpleDirectedGraph<WikiPage, DefaultEdge>, vertex: WikiPage, parent: TreeItem<String>) {
        if (Graphs.successorListOf(graph, vertex).isNotEmpty()) {
            val treeItem: TreeItem<String> = TreeItem(vertex.baseURL)
            parent.children.add(treeItem)
            Graphs.successorListOf(graph, vertex).forEach { graphToTree(graph, it, treeItem) }
        } else {
            parent.children.add(TreeItem(vertex.baseURL))
        }
    }
}

class MyApp: App(MainView::class)

fun main(args: Array<String>) {
    launch<MyApp>(args)
}
