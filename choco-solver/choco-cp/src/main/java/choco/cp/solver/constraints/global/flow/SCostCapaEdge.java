package choco.cp.solver.constraints.global.flow;

import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * An API class to model an edge with capacity (either a variable or a non negative integer upper bound) and cost.
 * The destination value indicates the number of the endpoint number in the graph.
 */
public class SCostCapaEdge extends SCapaEdge
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
  public SCostCapaEdge(IntDomainVar capa, int dest) {
    this(capa, dest, 0);
  }

  /**
   * Constructs an edge with a capacity, a cost and a destination.
   * @param capa
   * @param dest
   * @param cost
   */
  public SCostCapaEdge(IntDomainVar capa, int dest, int cost) {
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
