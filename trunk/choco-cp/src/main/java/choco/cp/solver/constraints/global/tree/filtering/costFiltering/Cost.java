/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.global.tree.filtering.costFiltering;

import choco.cp.solver.constraints.global.tree.filtering.AbstractPropagator;
import choco.kernel.common.util.IntIterator;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.trailing.StoredBitSet;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;


public class Cost extends AbstractPropagator {

    /**
     * current cost matrix associated with the graph to partition
     */
    protected IStateInt[][] cost;

    /**
     * current cost of the shortest path matrix associated with the graph to partition
     */
    protected IStateInt[][] minCost;

    /**
     * current cost of a forest covering the graph
     */
    protected IStateInt forestCost;

    public Cost(Object[] params) {
        super(params);
        this.cost = costStruct.getCost();
        this.minCost = costStruct.getMinCost();
        this.forestCost = costStruct.getForestCost();
    }

    public String getTypePropag() {
        return "Cost propagation";
    }

    public boolean feasibility() throws ContradictionException {
        forestCost = costStruct.getForestCost();
        return !(forestCost.get() > tree.getObjective().getSup() || forestCost.get() < tree.getObjective().getInf());
    }

    /**
     * remove each arc (i,j) of the graph such that the total cost a forest containing this arc exceed the objective
     * variable
     *
     * @throws ContradictionException
     */
    public void filter() throws ContradictionException {
        StoredBitSet[] numFromVertGt = struct.getInputGraph().getSure().getNumFromVertCC();
        IStateInt[] deltaCosts = costStruct.getDeltaCost();
        /*
        * (i,j) be a maybe arc, cc_i the component of i, if cost(i,j) + total - delta(cc_i) > objective,
        * (i,j) should be removed!
        */
        propagateStruct.setMinObjective(forestCost.get());
        for (int i = 0; i < nbVertices; i++) {
            IntDomainVar var = nodes[i].getSuccessors();
            if (!var.isInstantiated()) {
                int cc_i = numFromVertGt[i].nextSetBit(0);
                IntIterator values = var.getDomain().getIterator();
                while (values.hasNext()) {
                    int j = values.next();
                    if (forestCost.get() - deltaCosts[cc_i].get() + cost[i][j].get() > tree.getObjective().getSup() ||
                            forestCost.get() - deltaCosts[cc_i].get() + cost[i][j].get() < tree.getObjective().getInf()) {
                        int[] arc = {i, j};
                        propagateStruct.addRemoval(arc);
                    }
                }
            }
        }
    }

    public boolean allInstantiated() {
        for (int i = 0; i < nbVertices; i++) {
            if (!nodes[i].getSuccessors().isInstantiated()) return false;
        }
        return true;
    }
}
