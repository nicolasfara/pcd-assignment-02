package it.unibo.pcd

fun main(args: Array<String>) {
    // an user example url passed
    val userUrl = "https://it.wikipedia.org/wiki/Linguaggio_di_programmazione"
    val nodeExample = Node()
    print(nodeExample.fromUrl(userUrl))
}
