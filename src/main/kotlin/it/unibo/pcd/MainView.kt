package it.unibo.pcd

import it.unibo.pcd.contract.Contract
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.CrawlerPresenter
import it.unibo.pcd.presenter.SearchStrategy
import javafx.application.Platform
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import org.jgrapht.Graph
import org.jgrapht.Graphs
import org.jgrapht.graph.DefaultEdge
import tornadofx.*

class MainView: View("Wiki Link Search "), Contract.View {
    override val root: BorderPane by fxml("/MainView.fxml")
    private val graphPane: StackPane by fxid()
    private val wikiUrl: TextField by fxid()
    private val depth: TextField by fxid()
    private val progress: ProgressIndicator by fxid()
    private val searchBtn: Button by fxid()
    private val combo: ComboBox<SearchStrategy> by fxid()

    private val presenter: CrawlerPresenter

    init {
        combo.items.addAll(SearchStrategy.values())
        presenter = CrawlerPresenter()
        presenter.attachView(this)
    }

    override fun displaySearchResult(graph: Graph<WikiPage, DefaultEdge>) {
        Platform.runLater {
            val tree = buildTreeView(graph)
            tree.isShowRoot = false
            graphPane.children.add(tree)
            onFinishSearch()
        }
    }

    fun search() {
        onStartSearch()
        presenter.startSearch(wikiUrl.text, depth.text.toInt(), combo.value)
    }

    private fun onStartSearch() {
        progress.isVisible = true
        graphPane.isDisable = true
        searchBtn.isDisable = true
    }

    private fun onFinishSearch() {
        progress.isVisible = false
        graphPane.isDisable = false
        searchBtn.isDisable = false
    }

    private fun buildTreeView(graph: Graph<WikiPage, DefaultEdge>): TreeView<String> {
        val treeItem: TreeItem<String> = TreeItem()
        graphToTree(graph, graph.vertexSet().first { el -> el.entryNode }, treeItem)
        return TreeView(treeItem)
    }

    private fun graphToTree(graph: Graph<WikiPage, DefaultEdge>, vertex: WikiPage, parent: TreeItem<String>) {
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
