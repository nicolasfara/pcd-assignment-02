package it.unibo.pcd

enum class SearchStrategy(val value: String) {
    FORK_JOIN("Fork Join"),
    VERTX("Vertx"),
    COROUTINES("Coroutines"),
    REACTIVE("Reactive");

    override fun toString(): String {
        return this.value
    }
}