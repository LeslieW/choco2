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
package choco.cp.solver.constraints.global.tree.filtering.structuralFiltering.tree;



import choco.kernel.memory.trailing.StoredBitSet;

import java.util.BitSet;


public class SearchInfeasible {

    protected int dom;
    protected StoredBitSet[] revGraph;
    protected BitSet reached;

    public SearchInfeasible(int dom, StoredBitSet[] revGraph) {
        this.dom = dom;
        this.revGraph = revGraph;
        reached = new BitSet(revGraph.length);
    }

    public BitSet getReached() {return reached;}

    public void dfsVisit(int u) {
        reached.set(u,true);
        StoredBitSet succ = revGraph[u];
        for (int v = succ.nextSetBit(0); v >= 0; v = succ.nextSetBit(v + 1)) {
             if (!reached.get(v) && v != dom) dfsVisit(v);
        }
    }
}
