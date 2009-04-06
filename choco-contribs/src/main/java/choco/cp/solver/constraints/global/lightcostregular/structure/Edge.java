package choco.cp.solver.constraints.global.lightcostregular.structure;


import java.util.Comparator;


/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Nov 24, 2008
 * Time: 12:59:05 PM
 */
public class Edge implements Comparable<Edge>{


    public int j;
    int index;
    int indexRev;
    public Node orig;
    public Node dest;


    public Edge(Node orig, Node dest, int j)
    {
        this.orig = orig;
        this.dest = dest;
        this.j = j;
    }


    public int compareTo(Edge edge) {
        return new Integer(this.dest.id).compareTo(edge.dest.id);
    }

    public static class OutComparator implements Comparator<Edge> {



        public int compare(Edge edge, Edge edge1) {
            return new Integer(edge.orig.id).compareTo(edge1.orig.id);
        }
    }
    public String toString()
    {
        return ""+index+" | "+j;
    }
}
