package it.unibo.pcd.presenter.crawler.network

interface WikiParser {
    fun getLinksInAbstract(body: String): Collection<String>
    fun getDescription(json: String): String
}