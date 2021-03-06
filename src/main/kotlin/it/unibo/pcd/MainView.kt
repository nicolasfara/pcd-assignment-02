package it.unibo.pcd

import it.unibo.pcd.contract.Contract
import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.CrawlerPresenter
import it.unibo.pcd.presenter.SearchStrategy
import it.unibo.pcd.view.LinkListViewCell
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextField
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.BorderPane
import tornadofx.App
import tornadofx.View
import tornadofx.launch

class MainView : View("Wiki Link Search "), Contract.View {
    override val root: BorderPane by fxml("/MainView.fxml")
    private val wikiUrl: TextField by fxid()
    private val depth: TextField by fxid()
    private val progress: ProgressIndicator by fxid()
    private val searchBtn: Button by fxid()
    private val combo: ComboBox<SearchStrategy> by fxid()
    private val graphSize: Label by fxid()
    private val itemsList = FXCollections.observableArrayList<WikiPage>()
    private val listView: ListView<WikiPage> by fxid()
    private val presenter: CrawlerPresenter

    init {
        listView.setCellFactory { LinkListViewCell() }
        listView.items = itemsList
        combo.items.addAll(SearchStrategy.values())
        presenter = CrawlerPresenter()
        presenter.attachView(this)
    }

    override fun displaySearchResult(vertex: WikiPage) {
        Platform.runLater {
            itemsList.add(vertex)
        }
    }

    override fun displayGraphSize(size: Int) {
        Platform.runLater {
            graphSize.text = "Graph size: $size"
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
        presenter.clearGraph()
        onStartSearch()
        presenter.startSearch(wikiUrl.text, depth.text.toInt(), combo.value)
    }

    private fun onStartSearch() {
        progress.isVisible = true
        searchBtn.isDisable = true
    }

    private fun onFinishSearch() {
        progress.isVisible = false
        searchBtn.isDisable = false
    }
}

class MyApp : App(MainView::class)

fun main(args: Array<String>) {
    launch<MyApp>(args)
}
