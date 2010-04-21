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
package choco.cp.solver.search.real;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.branch.AbstractIntBranchingStrategy;
import choco.kernel.solver.branch.VarSelector;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.search.ValIterator;
import choco.kernel.solver.variables.real.RealMath;
import choco.kernel.solver.variables.real.RealVar;

/**
 * A binary branching assigning interval to subinterval.
 */

public final class AssignInterval extends AbstractIntBranchingStrategy {

	protected VarSelector<RealVar> varSelector;
	protected ValIterator valIterator;

	protected static final String[] LOG_DECISION_MSG = new String[]{"in first half of ", "in second half of "};

	public AssignInterval(VarSelector<RealVar> varSelector, ValIterator valIterator) {
		this.varSelector = varSelector;
		this.valIterator = valIterator;
	}

	public Object selectBranchingObject() throws ContradictionException {
		return varSelector.selectVar();
	}

	@Override
	public void goDownBranch(final IntBranchingDecision decision) throws ContradictionException {
		final RealVar x = decision.getBranchingRealVar();
		if ( decision.getBranchingValue() == 1) {
			x.intersect(RealMath.firstHalf(x));
			//manager.solver.propagate(); //FIXME is propagate useful ?
		} else if( decision.getBranchingValue() == 2) {
			x.intersect(RealMath.secondHalf(x));
			//manager.solver.propagate(); //FIXME is propagate useful ?
		} else {
			throw new SolverException("invalid real branching value");
		}
	}

	/**
	 * do nothing
	 */
	@Override
	public void goUpBranch(final IntBranchingDecision decision) throws ContradictionException {
		//do nothing
	}


    public void setFirstBranch(final IntBranchingDecision decision) {
		decision.setBranchingValue( valIterator.getFirstVal(decision.getBranchingRealVar()));
	}

	public void setNextBranch(final IntBranchingDecision decision) {
		decision.setBranchingValue( valIterator.getNextVal(decision.getBranchingRealVar(), decision.getBranchingValue()));
	}

	public boolean finishedBranching(final IntBranchingDecision decision) {
		return  ! valIterator.hasNextVal(decision.getBranchingRealVar(), decision.getBranchingValue());
	}

	@Override
	public String getDecisionLogMessage(IntBranchingDecision decision) {
		return  LOG_DECISION_MSG[decision.getBranchIndex()] + decision.getBranchingObject();
	}


}
