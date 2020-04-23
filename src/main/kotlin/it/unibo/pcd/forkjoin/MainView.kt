package it.unibo.pcd.forkjoin

import it.unibo.pcd.data.WikiPage
import javafx.application.Platform
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import org.jgrapht.Graphs
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
import tornadofx.*
import java.util.concurrent.ForkJoinPool

class MainView : View("My View") {
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

        Thread {
            val graph = SimpleDirectedGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)
            val search = LinkSearchAction(graph, depth = depth.text.toInt(), startURL = wikiUrl.text)
            val commonPool = ForkJoinPool()
            commonPool.invoke(search)
            val treeItem: TreeItem<String> = TreeItem()
            graphToTree(graph, graph.vertexSet().first { it.entryNode }, treeItem)
            val treeView: TreeView<String> = TreeView(treeItem)
            treeView.isShowRoot = false
            Platform.runLater {
                graphPane.children.add(treeView)
                progress.isVisible = false
                graphPane.isDisable = false
                searchBtn.isDisable = false
            }
        }.start()
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
