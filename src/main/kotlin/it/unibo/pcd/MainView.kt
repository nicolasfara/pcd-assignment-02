package it.unibo.pcd

import it.unibo.pcd.contract.Contract
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.CrawlerPresenter
import it.unibo.pcd.presenter.SearchStrategy
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import tornadofx.*
import kotlin.collections.HashSet

class MainView: View("Wiki Link Search "), Contract.View {
    override val root: BorderPane by fxml("/MainView.fxml")
    private val graphPane: StackPane by fxid()
    private val wikiUrl: TextField by fxid()
    private val depth: TextField by fxid()
    private val progress: ProgressIndicator by fxid()
    private val searchBtn: Button by fxid()
    private val combo: ComboBox<SearchStrategy> by fxid()
    private val resSet = HashSet<String>()
    private val itemsList = FXCollections.observableArrayList<String>()
    private val listView = ListView(itemsList)

    private val presenter: CrawlerPresenter

    init {
        combo.items.addAll(SearchStrategy.values())
        presenter = CrawlerPresenter()
        presenter.attachView(this)
        graphPane.children.add(listView)
    }

    override fun displaySearchResult(vertex: Set<WikiPage>) {
        Platform.runLater {
            resSet.addAll(vertex.map { it.baseURL })
            itemsList.clear()
            itemsList.addAll(resSet)
        }
    }

    override fun onFinishResult() {
        Platform.runLater {
            onFinishSearch()
        }
    }

    @ExperimentalStdlibApi
    fun search() {
        itemsList.clear()
        onStartSearch()
        presenter.startSearch(wikiUrl.text, depth.text.toInt(), combo.value)
    }

    private fun onStartSearch() {
        progress.isVisible = true
        //graphPane.isDisable = true
        searchBtn.isDisable = true
    }

    private fun onFinishSearch() {
        progress.isVisible = false
        //graphPane.isDisable = false
        searchBtn.isDisable = false
    }

    /*@Synchronized private fun buildTreeView(graph: Set<WikiPage>): TreeView<String> {
        val treeItem: TreeItem<String> = TreeItem()
        val copyGraph = AsSynchronizedGraph.Builder<WikiPage, DefaultEdge>().build(graph)
        val entryNode = HashSet(copyGraph.vertexSet())
        graphToTree(graph, graph.find { it.entryNode }!!, treeItem)
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
    }*/
}

class MyApp: App(MainView::class)

fun main(args: Array<String>) {
    launch<MyApp>(args)
}
