package it.unibo.pcd.view

import it.unibo.pcd.model.WikiPage
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.layout.GridPane

class LinkListViewCell : ListCell<WikiPage>() {

    private val baseUrl = Label()
    private val description = Label()
    private val links = Label()

    private val linkSet = mutableSetOf<String>()

    companion object {
        private const val DESCRIPTION_LENGTH = 100
        private const val COL = 0
        private const val FIRST_ROW = 0
        private const val SECOND_ROW = 1
        private const val THIRD_ROW = 2
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

            grid.add(baseUrl, COL, FIRST_ROW)
            grid.add(description, COL, SECOND_ROW)
            grid.add(links, COL, THIRD_ROW)
            graphic = grid
        }
    }
}
