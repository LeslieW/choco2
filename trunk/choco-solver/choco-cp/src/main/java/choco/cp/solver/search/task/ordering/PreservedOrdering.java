/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
 *         |  �(..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */

package choco.cp.solver.search.task.ordering;

import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.preserved.MaxPreservedRatio;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.preserved.MinPreservedRatio;
import choco.kernel.solver.variables.scheduling.TaskVar;

public final class PreservedOrdering extends RandomOrdering {

	private final MinPreservedRatio ratio;
	
	 public PreservedOrdering(boolean minOrMax, long seed) {
		super(seed);
		ratio = minOrMax ?  new MinPreservedRatio(null) : new MaxPreservedRatio(null);
	 }

	@Override
	public int getBestVal(TaskVar t1, TaskVar t2) {
		return ratio.getBestVal(randomBreakTie, t1, t2);
	}
}
