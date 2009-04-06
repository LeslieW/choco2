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
package choco.kernel.solver.branch;

public abstract class AbstractBinIntBranching extends AbstractIntBranching {
	public int getFirstBranch(Object x) {
		return 0;
	}

	public int getNextBranch(Object x, int i) {
		assert i == 0;
		return 1;
	}

	public boolean finishedBranching(Object x, int i) {
		return i > 0;
	}

	/**
	 * @deprecated replaced by the management incremental search (with a stack
	 *             of BranchingTrace storing the environment (local variables)
	 *             associated to each choice point
	 * @param n
	 * @return
	 */
	// public boolean explore(int n) {
	// AbstractGlobalSearchStrategy algo = manager;
	// AbstractModel pb = algo.model;
	// Object x = selectBranchingObject();
	// if (null != x) {
	// try {
	// return branchOn(x, n);
	// } catch (ContradictionException e) {
	// return false;
	// }
	// } else if (null != nextBranching) {
	// return ((IntBranching) nextBranching).explore(n);
	// } else {
	// algo.recordSolution();
	// algo.showSolution();
	// return algo.stopAtFirstSol;
	// }
	// }

}
