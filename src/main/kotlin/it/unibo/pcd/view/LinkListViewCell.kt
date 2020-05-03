package it.unibo.pcd.view

import it.unibo.pcd.model.WikiPage
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.layout.GridPane

class LinkListViewCell : ListCell<WikiPage>() {

    private val baseUrl = Label()
    private val description = Label()
    private val links = Label()
    private var col = 0
    private var firstRow = 0
    private var secondRow = 1
    private var thirdRow = 2

    private val linkSet = mutableSetOf<String>()

    companion object {
        private const val DESCRIPTION_LENGTH = 100
    }

    override fun updateItem(wikiPage: WikiPage?, empty: Boolean) {
        super.updateItem(wikiPage, empty)
        if (empty || wikiPage == null) {
            text = null
            graphic = null
        } else {
            val grid = GridPane()
            baseUrl.text = wikiPage.baseURL.substringAfter("wiki/")
            baseUrl.style = "-fx-font-weight: bold"
            description.text = wikiPage.description.take(DESCRIPTION_LENGTH)
            description.style = "-fx-font-style: italic"
            wikiPage.links.forEach { linkSet.add(it.substringAfter("wiki/")) }
            links.text = linkSet.toString()

            grid.add(baseUrl, col, firstRow)
            grid.add(description, col, secondRow)
            grid.add(links, col, thirdRow)
            graphic = grid
        }
    }
}
