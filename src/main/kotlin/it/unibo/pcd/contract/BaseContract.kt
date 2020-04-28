package it.unibo.pcd.contract

class BaseContract {
    interface Presenter<in T> {
        fun attachView(view: T)
    }

    interface View { }
}