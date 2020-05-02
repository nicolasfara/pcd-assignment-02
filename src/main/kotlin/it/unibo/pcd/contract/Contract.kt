package it.unibo.pcd.contract

import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.SearchStrategy
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge

class Contract {
    interface View: BaseContract.View {
        fun displaySearchResult(vertex: Set<WikiPage>)
        fun onFinishResult()
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun startSearch(url: String, depth:Int, strategy: SearchStrategy)
    }
}