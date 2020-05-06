package it.unibo.pcd.contract

interface BaseContract {
    interface Presenter<in T> {
        fun attachView(view: T)
    }

    interface View
}
