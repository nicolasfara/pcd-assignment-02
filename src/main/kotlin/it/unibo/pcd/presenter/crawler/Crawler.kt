package it.unibo.pcd.presenter.crawler

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.FlowableProcessor
import it.unibo.pcd.model.WikiPage
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge

interface Crawler {
    fun crawl(url: String, depth: Int): FlowableProcessor<Set<WikiPage>>
}