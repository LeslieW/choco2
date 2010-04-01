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
package choco.cp.solver.search.integer.branching.domwdeg;

import choco.cp.solver.search.integer.varselector.ratioselector.ratios.IntRatio;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.IntBranchingDecision;

public abstract class AbstractDomOverWDegBinBranching extends
		AbstractDomOverWDegBranching {

	public AbstractDomOverWDegBinBranching(Solver solver, IntRatio[] varRatios, Number seed) {
		super(solver, varRatios, seed);
	}

	public final void getNextBranch(final IntBranchingDecision decision) {
		assert decision.getBranchIndex() == 0;
	
	}

	public final boolean finishedBranching(final IntBranchingDecision decision) {
		return decision.getBranchIndex() > 0;
	}

	@Override
	public String getDecisionLogMessage(IntBranchingDecision decision) {
		return getDefaultAssignOrForbidMsg(decision);
	}

	/**
	 * nothing to do
	 */
	@Override
	public void goUpBranch(IntBranchingDecision decision) throws ContradictionException {
		//nothing to do	
	}

	/**
	 * nothing to do
	 */
	@Override
	public final void setNextBranch(IntBranchingDecision decision) {
		//nothing to do
	}

}