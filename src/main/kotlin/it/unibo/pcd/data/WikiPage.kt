package it.unibo.pcd.data

data class WikiPage(val baseURL: String, val description: String, val links: MutableSet<String> = mutableSetOf(), val entryNode: Boolean = false) {
    override fun toString(): String {
        return "WikiPage(url: $baseURL, desc: $description, links: ${links.size})"
    }
}