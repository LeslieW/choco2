package choco.cp.solver.constraints.global.fast_regular.structure;

import org.jgrapht.EdgeFactory;
import choco.kernel.memory.structure.IndexedObject;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Oct 30, 2009
 * Time: 3:48:11 PM
 */
public class Arc implements IndexedObject {

    public int id;
    public Node orig;
    public Node dest;
    public int value;


    public Arc(Node orig, Node dest, int value, int id)
    {
        this.id = id;
        this.orig = orig;
        this.dest = dest;
        this.value = value;
    }


    public String toString()
    {
        return value+"";
    }

    @Override
    public int getObjectIdx() {
        return orig.state;
    }


    public static class ArcFacroty implements EdgeFactory<Node, Arc> {

        public Arc createEdge(Node node, Node node1) {
            return new Arc(node,node1,0,0);
        }
    }

}
