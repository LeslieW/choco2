package choco.kernel.solver.constraints.global.automata.fast_costregular.structure;

import org.jgrapht.EdgeFactory;
import choco.kernel.memory.structure.IndexedObject;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Oct 30, 2009
 * Time: 3:48:11 PM
 */
public class Arc extends DefaultWeightedEdge implements IndexedObject {

    public int id;
    public Node orig;
    public Node dest;
    public int value;
    public double cost;


    public Arc(Node orig, Node dest, int value, int id, double cost)
    {
        this.id = id;
        this.orig = orig;
        this.dest = dest;
        this.value = value;
        this.cost = cost;
    }
    public Arc(Node orig, Node dest, int value)
    {
        this(orig,dest,value,Integer.MIN_VALUE,Double.POSITIVE_INFINITY);
    }

    public double getWeight()
    {
        return this.cost;
    }


    public String toString()
    {
        return value+"";
    }

    public final void setId(int id)
    {
        this.id = id;
    }

    @Override
    public int getObjectIdx() {
        return orig.state;
    }


    public static class ArcFacroty implements EdgeFactory<Node, Arc> {

        public Arc createEdge(Node node, Node node1) {
            return new Arc(node,node1,0,0,0.0);
        }
    }

}
