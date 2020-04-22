package it.unibo.pcd.data

data class WikiPage(val baseURL: String, val description: String, val links: MutableSet<String> = mutableSetOf())