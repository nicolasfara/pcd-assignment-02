package it.unibo.pcd.vertx
import io.vertx.core.AsyncResult
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import it.unibo.pcd.data.WikiPage
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph

fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    val depth = 2
    val start = "https://it.wikipedia.org/wiki/Lingua_italiana"
    val graph = SimpleDirectedGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)
    val myVerticle  = VerticleLinkAnalizer(graph,depth,start)
    var deploymentID = ""
    val options = DeploymentOptions().setWorker(true)
    vertx.deployVerticle(myVerticle,options){res ->
        if (res.succeeded()) {
            System.out.println("Deployment id is: " + res.result())
            deploymentID = res.result()
            vertx.undeploy(deploymentID) { res: AsyncResult<Void?> ->
                if (res.succeeded()) {
                    System.out.println("Undeployed ok");
                } else {
                    println("Undeploy failed!")
                }
            }
        } else {
            System.out.println("Deployment failed!");
        }
    }


}


