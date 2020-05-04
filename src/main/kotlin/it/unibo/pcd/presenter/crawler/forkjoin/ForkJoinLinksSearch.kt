package it.unibo.pcd.presenter.crawler.forkjoin

import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.crawler.network.WikiCrawler
import java.util.Optional
import java.util.concurrent.RecursiveTask

class ForkJoinLinksSearch(
    private val rootPage: WikiPage,
    private val depth: Int,
    private val crawler: WikiCrawler,
    private val onNewPage: (WikiPage) -> Unit
) : RecursiveTask<Collection<WikiPage>>() {

    override fun compute(): Collection<WikiPage> {
        val list = mutableListOf<WikiPage>()
        val taskList = mutableListOf<ForkJoinLinksSearch>()
        if (depth > 0) {
            rootPage.links
                .map {
                    WikiPage(
                        Optional.of(rootPage.baseURL),
                        it,
                        crawler.getDescriptionFromPage(it),
                        crawler.getLinksFromAbstract(it).toSet()
                    )
                }
                .forEach {
                    onNewPage(it)
                    list.add(it)
                    val ele = ForkJoinLinksSearch(
                        it,
                        depth - 1,
                        crawler,
                        onNewPage
                    )
                    ele.fork()
                    taskList.add(ele)
                }
        } else {
            list.add(rootPage)
        }

        taskList.forEach {
            list.addAll(it.join())
        }

        return list
    }
}
