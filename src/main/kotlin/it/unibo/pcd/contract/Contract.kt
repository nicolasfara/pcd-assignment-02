package it.unibo.pcd.contract

import it.unibo.pcd.model.WikiPage
import it.unibo.pcd.presenter.SearchStrategy

interface Contract {
    interface View : BaseContract.View {
        fun displaySearchResult(vertex: WikiPage)
        fun displayGraphSize(size: Int)
        fun onFinishResult()
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun startSearch(url: String, depth: Int, strategy: SearchStrategy)
        fun clearGraph()
    }
}
