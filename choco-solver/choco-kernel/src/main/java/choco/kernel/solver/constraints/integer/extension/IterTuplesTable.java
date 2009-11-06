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
 *
 **/
public class IterTuplesTable extends TuplesList implements IterLargeRelation, LargeRelation {

	/**
	 * table[i][j] gives the table of supports as an int[] for value j of variable i
	 */
	protected int[][][] table;

	/**
	 * number of variables
	 */
	protected int nbVar = 0;

	/**
	 * The sizes of the domains
	 */
	protected int[] dsizes;

	/**
	 * The lower bound of each variable
	 */
	protected int[] offsets;

	public IterTuplesTable(List<int[]> tuples, int[] offsets, int[] domSizes) {
		super(tuples);
        nbVar = domSizes.length;
		dsizes = domSizes;
		this.offsets = offsets;
		table = new int[nbVar][][];
		for (int i = 0; i < domSizes.length; i++) {
			table[i] = new int[domSizes[i]][];
			int[] nbsups = getNbSupportFor(tuples, i);
			for (int j = 0; j < nbsups.length; j++) {
				table[i][j] = new int[nbsups[j]];
			}
		}
		buildInitialListOfSupports(tuples);
	}

	/**
	 * return the number of tuples supporting each value of variable i
	 * @param tups
	 * @param i a variable
	 * @return
	 */
	public int[] getNbSupportFor(List<int[]> tups, int i) {
		int[] nbsup = new int[dsizes[i]];
		for (Iterator it = tups.iterator(); it.hasNext();) {
			int[] tuple = (int[]) it.next();
			nbsup[tuple[i] - offsets[i]]++;
		}
		return nbsup;
	}

	public void buildInitialListOfSupports(List<int[]> tuples) {
		int cpt = 0;
		int[][] level = new int[nbVar][];
		for (int i = 0; i < nbVar; i++) {
			level[i] = new int[dsizes[i]];
		}
		for (Iterator<int[]> it = tuples.iterator(); it.hasNext();) {
			int[] tuple = it.next();
			for (int i = 0; i < tuple.length; i++) {
				int value = tuple[i] - offsets[i];
				table[i][value][level[i][value]] = cpt;
				level[i][value]++;
			}
			cpt++;
		}
	}

	/**
	 * for fast access
	 * @return
	 */
	public int[][][] getTableLists() {
		return table;
	}

	/**
	 * This relation do not take advantage of the knowledge of the
	 * previous support ! so start from scratch
	 * @param oldidx
	 * @param var
	 * @param val is the value assuming the offset has already been
	 * removed
	 * @return
	 */
	public int seekNextTuple(int oldidx, int var, int val) {
		int nidx = oldidx++;
		if (nidx < table[var][val].length) {
			return table[var][val][nidx];
		} else {
			return -1;
		}
	}

	/**
	 * return the number of supports of the pair (var, val) assuming the
	 * offset has already been removed
	 * @param var
	 * @param val
	 * @return
	 */
	public int getNbSupport(int var, int val) {
		return table[var][val].length;
	}

    public int getRelationOffset(int var) {
		return offsets[var];
	}

    public boolean checkTuple(int[] tuple) {
		throw new SolverException("checkTuple should not be used on an IterRelation");
	}

	public boolean isConsistent(int[] tuple) {
		return checkTuple(tuple);
	}
}
