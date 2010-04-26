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
package samples.tutorials.seminar.tsp;

import choco.kernel.solver.search.integer.AbstractIntVarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.LinkedList;


public class MyVarSelector extends AbstractIntVarSelector {

    protected IntDomainVar[] vars;
    protected IntDomainVar objective;
    protected int src;
    protected int dest;

    public MyVarSelector(IntDomainVar objective, IntDomainVar[] vars, int src, int dest) {
        super(null, vars);
    	this.objective = objective;
    	this.src = src;
        this.dest = dest;
    }

    public MyVarSelector(IntDomainVar[] vars, int src, int dest) {
    	super(null, vars);
        this.src = src;
        this.dest = dest;
        this.objective = null;
    }

    public IntDomainVar selectVar() {
        int next = dfs();
        if (next == dest && vars[dest].isInstantiated()) {
            if (objective == null) return null;
            else {
                if (objective.isInstantiated()) return null;
                else return objective;
            }
        } else {
            return vars[next];
        }
    }

    private int dfs() {
        LinkedList toVisit = new LinkedList();
        LinkedList visited = new LinkedList();
        visited.offer(String.valueOf(src));
        toVisit.addFirst(String.valueOf(src));
        int lastVisited = src;
        while (!toVisit.isEmpty()) {
            int current = Integer.parseInt((String) toVisit.poll());
            lastVisited = current;
            if (vars[current].isInstantiated()) {
                int j = vars[current].getVal();
                if (!visited.contains(String.valueOf(j))) {
                    visited.offer(String.valueOf(j));
                    toVisit.addFirst(String.valueOf(j));
                }
            }
        }
        return lastVisited;
    }

}
