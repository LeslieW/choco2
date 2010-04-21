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
package choco.cp.solver.search.integer.branching;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractBinIntBranchingStrategy;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.search.ValSelector;

public abstract class AbstractAssignOrForbidBranching extends AbstractBinIntBranchingStrategy {


	// L'heuristique pour le svaleurs
	protected ValSelector valSelector;


	public AbstractAssignOrForbidBranching(ValSelector valSelector) {
		super();
		this.valSelector = valSelector;
	}

	@Override
	public void setFirstBranch(final IntBranchingDecision decision) {
		decision.setBranchingValue( valSelector.getBestVal(decision.getBranchingIntVar()));
	}


	@Override
	public String getDecisionLogMessage(final IntBranchingDecision decision) {
		return getDefaultAssignOrForbidMsg(decision);
	}

	/**
	 * nothing to do
	 */
	@Override
	public void goUpBranch(final IntBranchingDecision decision)	throws ContradictionException {
		//nothing to do
	}

}

