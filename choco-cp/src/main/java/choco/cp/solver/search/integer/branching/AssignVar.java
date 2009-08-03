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


import choco.cp.solver.search.integer.varselector.DomOverWDeg;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractLargeIntBranching;
import choco.kernel.solver.branch.VarSelector;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.search.integer.ValIterator;
import choco.kernel.solver.search.integer.ValSelector;

public class AssignVar extends AbstractLargeIntBranching {
	private VarSelector varHeuristic;
	private ValIterator valHeuristic;
	private ValSelector valSHeuristic;
	protected ValueChooserWrapper wrapper;

	public AssignVar(VarSelector varSel, ValIterator valHeuri) {
		varHeuristic = varSel;
		valHeuristic = valHeuri;
		wrapper = new ValIteratorWrapper();
	}

	public AssignVar(VarSelector varSel, ValSelector valHeuri) {
		varHeuristic = varSel;
		valSHeuristic = valHeuri;
		wrapper = new ValSelectorWrapper();
	}

	/**
	 * selecting the object under scrutiny (that object on which an alternative will be set)
	 *
	 * @return the object on which an alternative will be set (often  a variable)
	 */
	public Object selectBranchingObject() throws ContradictionException {
		return varHeuristic.selectVar();
	}


	public boolean finishedBranching(final IntBranchingDecision decision) {
		return wrapper.finishedBranching(decision);
	}

	

	public void setFirstBranch(final IntBranchingDecision decision) {
		decision.setBranchingValue(wrapper.getFirstBranch(decision));
	}

	public void setNextBranch(final IntBranchingDecision decision) {
		decision.setBranchingValue(wrapper.getNextBranch(decision));
	}

	@Override
	public void goDownBranch(final IntBranchingDecision decision) throws ContradictionException {
		decision.setIntVal();
	}

	@Override
	public void goUpBranch(final IntBranchingDecision decision) throws ContradictionException {
		decision.remIntVal();
	}

	@Override
	public String getDecisionLogMessage(IntBranchingDecision decision) {
		return getDefaultAssignMsg(decision);
	}

	protected interface ValueChooserWrapper {
		public boolean finishedBranching(IntBranchingDecision decision);

		public int getFirstBranch(IntBranchingDecision decision);


		public int getNextBranch(IntBranchingDecision decision);
	}

	protected class ValIteratorWrapper implements ValueChooserWrapper {
		public boolean finishedBranching(final IntBranchingDecision decision) {
			return ( ! valHeuristic.hasNextVal(decision.getBranchingIntVar(), decision.getBranchingValue()));
		}

		public int getFirstBranch(final IntBranchingDecision decision) {
			return valHeuristic.getFirstVal(decision.getBranchingIntVar());
		}

		public int getNextBranch(final IntBranchingDecision decision) {
			return valHeuristic.getNextVal(decision.getBranchingIntVar(), decision.getBranchingValue());
		}
	}


	protected class ValSelectorWrapper implements ValueChooserWrapper {
		public boolean finishedBranching(final IntBranchingDecision decision) {
			return decision.getBranchingIntVar().getDomainSize() == 0;
		}

		public int getFirstBranch(final IntBranchingDecision decision) {
			return valSHeuristic.getBestVal(decision.getBranchingIntVar());
		}

		public int getNextBranch(final IntBranchingDecision decision) {
			return valSHeuristic.getBestVal(decision.getBranchingIntVar());
		}
	}

	@Override
	public void initConstraintForBranching(SConstraint c) {
		if (varHeuristic instanceof DomOverWDeg) {
			((DomOverWDeg) varHeuristic).initConstraintForBranching(c);
		}
	}
}
