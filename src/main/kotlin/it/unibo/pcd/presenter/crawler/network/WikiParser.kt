package it.unibo.pcd.presenter.crawler.network

interface WikiParser {
    fun parseForLinks(body: String): Collection<String>
    fun parseForDescription(json: String): String
}