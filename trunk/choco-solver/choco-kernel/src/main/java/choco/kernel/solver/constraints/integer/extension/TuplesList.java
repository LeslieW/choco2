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
package choco.kernel.solver.constraints.integer.extension;

import choco.kernel.solver.SolverException;

import java.util.Iterator;
import java.util.List;

/**
 * A simple way of storing the tuples as a list. This doesn't allow
 * consistency check (TuplesTable is made for that)
 * or iteration over supports of each value (IterTuplesTable is made for that)
 * This simple way of storing supports only allow fast iteration over the all
 * set of tuples and is used by STR gac scheme.
 */
public class TuplesList implements LargeRelation {

    /**
     * each tuple (a int[]) has its own index
     *
     * @return
     */
    protected int[][] tuplesIndexes;


    public TuplesList(List<int[]> tuples) {
        tuplesIndexes = new int[tuples.size()][];
		int cpt = 0;
		for (Iterator<int[]> it = tuples.iterator(); it.hasNext();) {
			tuplesIndexes[cpt] = it.next();
			cpt++;
		}
    }

    public int[] getTuple(int support) {
        return tuplesIndexes[support];
    }

    public int[][] getTupleTable() {
		return tuplesIndexes;
	}

    public boolean checkTuple(int[] tuple) {
        throw new SolverException("TuplesList is an unusual large relation...");
    }

    public boolean isConsistent(int[] tuple) {
        throw new SolverException("TuplesList is an unusual large relation...");
    }
}
