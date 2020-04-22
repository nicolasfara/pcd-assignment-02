package it.unibo.pcd.forkjoin

import it.unibo.pcd.data.WikiPage
import org.jgrapht.Graphs
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
import org.jgrapht.traverse.BreadthFirstIterator
import org.jgrapht.traverse.DepthFirstIterator
import java.awt.Dimension
import java.awt.GraphicsConfiguration
import com.mxgraph.layout.*;
import com.mxgraph.swing.*;
import org.jgrapht.*;
import org.jgrapht.ext.JGraphXAdapter
import org.jgrapht.graph.*;
import java.awt.FlowLayout

import java.util.concurrent.ForkJoinPool
import javax.swing.JApplet
import javax.swing.JFrame
import javax.swing.JPanel

fun main()  {

    val graph = SimpleDirectedGraph<WikiPage, DefaultEdge>(DefaultEdge::class.java)
    val applet = Adapter(graph)

    applet.title = "Graph"
    applet.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    applet.pack()
    applet.isVisible = true

    val search = LinkSearchAction(graph, depth = 1, startURL = "https://it.wikipedia.org/wiki/Bertinoro")

    val commonPool = ForkJoinPool()
    commonPool.invoke(search)

    applet.init()
}

class Adapter(private val graph: SimpleDirectedGraph<WikiPage, DefaultEdge>) : JFrame() {

    private var jfxAdapter: JGraphXAdapter<WikiPage, DefaultEdge>? = null

    companion object {
        val dimension: Dimension = Dimension(1920, 1080)
    }

    fun init() {
        jfxAdapter = JGraphXAdapter(graph)
        preferredSize = dimension
        val component = mxGraphComponent(jfxAdapter)
        component.isConnectable = false
        component.graph.isAllowDanglingEdges = false
        contentPane.add(component)
        resize(dimension)

        val layout = mxCircleLayout(jfxAdapter)
        val radius = 50.0
        layout.x0 = (dimension.width / 2.0) - radius;
        layout.y0 = (dimension.height / 2.0) - radius;
        layout.radius = radius;
        layout.isMoveCircle = true;

        layout.execute((jfxAdapter as JGraphXAdapter<WikiPage, DefaultEdge>).defaultParent);
    }

}

