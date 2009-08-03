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
package choco.kernel.solver.search.set;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractBinIntBranching;
import choco.kernel.solver.search.IntBranchingDecision;

//**************************************************
//*                   J-CHOCO                      *
//*   Copyright (C) F. Laburthe, 1999-2003         *
//**************************************************
//*  an open-source Constraint Programming Kernel  *
//*     for Research and Education                 *
//**************************************************


public abstract class AbstractSetBranching extends AbstractBinIntBranching {

	public final static String[] LOG_DECISION_MSG = new String[]{"contains ", "contains not "};
	
	
	@Override

	public void goDownBranch(final IntBranchingDecision decision) throws ContradictionException {
		if (decision.getBranchIndex() == 0) {
			decision.setValInSet();
			manager.solver.propagate(); // is propagate useful ?
		} else {
			decision.setValOutSet();
			manager.solver.propagate(); // is propagate useful ?
		}
	}

	/**
	 * nothing to do
	 * @see choco.kernel.solver.branch.AbstractIntBranching#goUpBranch(java.lang.Object, int)
	 */
	@Override
	public void goUpBranch(final IntBranchingDecision ctx) throws ContradictionException {
		//nothing to do
	}

	public String getDecisionLogMessage(IntBranchingDecision decision) {
		return decision.getBranchingSetVar() + LOG_DECISION_MSG[decision.getBranchIndex()] + decision.getBranchingValue();
	}

}
