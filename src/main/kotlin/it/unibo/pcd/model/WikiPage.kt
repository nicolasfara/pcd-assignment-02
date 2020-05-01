package it.unibo.pcd.model

import java.util.*

data class WikiPage(val parent: Optional<String>, val baseURL: String, val description: String, var links: Set<String>, val entryNode: Boolean = false) {
    override fun toString(): String {
        return "WikiPage(parent: $parent, url: ${baseURL.substringAfter("wiki/")}, links: ${links.size})"
    }
}