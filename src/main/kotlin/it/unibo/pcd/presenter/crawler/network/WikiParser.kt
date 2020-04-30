package it.unibo.pcd.presenter.crawler.network

interface WikiParser {
    fun getLinksInAbstract(url: String): Collection<String>
    fun getDescription(url: String): String
}