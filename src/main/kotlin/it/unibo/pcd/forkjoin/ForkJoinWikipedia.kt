package it.unibo.pcd.forkjoin

import it.unibo.pcd.data.Graph
import it.unibo.pcd.data.WikiPage
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.util.concurrent.ForkJoinPool


fun main(args: Array<String>) {

//    val graph = Graph<WikiPage>()
//    val search = LinkSearchAction(graph, depth = 3, startURL = "https://it.wikipedia.org/wiki/Citt%C3%A0_del_Vaticano")
//
//    val commonPool = ForkJoinPool()
//    commonPool.invoke(search)
//
//    print(graph)

    val doc: Document = Jsoup.connect("https://it.wikipedia.org/api/rest_v1/page/html/COVID-19").get()
    val newsHeadlines: Elements = doc.select("section:first-child p a:not([href*=#])  ")
    print(newsHeadlines)
}

