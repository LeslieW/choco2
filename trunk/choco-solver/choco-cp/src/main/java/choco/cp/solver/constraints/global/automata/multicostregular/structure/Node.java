package choco.cp.solver.constraints.global.automata.multicostregular.structure;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Oct 30, 2009
 * Time: 3:46:54 PM
 */
public class Node {


    public int id;
    public int state;
    public int layer;

    public Node(int state, int layer, int id)
    {
        this.id = id;
        this.state = state;
        this.layer = layer;
    }

    public boolean equals(Object n)
    {
        return n instanceof Node && ((Node) n).state == state && ((Node) n).layer == layer;

    }

}
