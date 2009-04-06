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
package choco.cp.solver.constraints.global.tree.filtering.structuralFiltering.timeWindows;

import choco.cp.solver.constraints.global.tree.filtering.AbstractPropagator;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;



public class TimeWindow extends AbstractPropagator {

    /**
     * current traveltime matrix associated with the graph to partition
     */
    protected IStateInt[][] travelTime;

    /**
     * current cost of the minimum travel time matrix associated with the graph to partition
     */
    protected IStateInt[][] minTravelTime;

    /**
     * propagator that contains the filetring rules directly derived from the graph to partition
     */
    protected DirectedPropag propagatePossGraph;

    /**
     * propagator that contains the filtering rules derived from the interaction with the precedence constraints
     */
    protected OrderedGraphPropag propagateOrderedGraph;


    /**
     * Constructor: build a framework to propagate filtering rules related to the time windows constraints.
     *
     * @param params a set of parameters describing each part of the global tree constraint
     *
     */
    public TimeWindow(Object[] params) {
        super(params);
        this.travelTime = costStruct.getCost();
        this.minTravelTime = costStruct.getMinCost();
        this.propagatePossGraph = new DirectedPropag(travelTime,inputGraph, nodes, propagateStruct);
        this.propagateOrderedGraph = new OrderedGraphPropag(travelTime, minTravelTime, precs, nodes, propagateStruct);
    }

    public boolean feasibility() throws ContradictionException {
        return true;
    }

    public void filter() throws ContradictionException {
        // propagate the different filtering rules
        this.propagatePossGraph.applyTWfiltering();
        this.propagateOrderedGraph.applyTWfiltering();
        this.propagatePossGraph.applyGraphFiltering();
    }

    public String getTypePropag() {
        return "Time Window propagation";
    }

}
