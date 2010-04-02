package choco.kernel.model.variables.flow;

import choco.kernel.model.variables.integer.IntegerVariable;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Mar 31, 2010
 * Time: 1:11:20 PM
 */
public class CostCapaEdge extends CapaEdge
{

/**
 * The cost of the edge.
 */
public final int cost;

/**
 * Constructs an edge with a capacity, a null cost and a destination.
 * @param capa
 * @param dest
 */
public CostCapaEdge(IntegerVariable capa, int dest) {
        this(capa, dest, 0);
}

/**
 * Constructs an edge with a capacity, a cost and a destination.
 * @param capa
 * @param dest
 * @param cost
 */
public CostCapaEdge(IntegerVariable capa, int dest, int cost) {
        super(capa, dest);
        this.cost = cost;
}

/**
 * @return A string containing a representation of the current edge.
 */
public String toSTring() {
        return "Edge to vertex " + dest + " with a cost of " + cost + " and the capacity " + capa;
}
}
