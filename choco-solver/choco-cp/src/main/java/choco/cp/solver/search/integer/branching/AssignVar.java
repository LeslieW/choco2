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
import choco.kernel.solver.branch.AbstractLargeIntBranchingStrategy;
import choco.kernel.solver.branch.VarSelector;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.search.ValIterator;
import choco.kernel.solver.search.ValSelector;

public class AssignVar extends AbstractLargeIntBranchingStrategy {
	private final VarSelector varHeuristic;
	private final ValueChooserWrapper wrapper;

	public AssignVar(VarSelector varSel, ValIterator valIterator) {
		varHeuristic = varSel;
		wrapper = new ValIteratorWrapper(valIterator);
	}

	public AssignVar(VarSelector varSel, ValSelector valSelector) {
		varHeuristic = varSel;
		wrapper = new ValSelectorWrapper(valSelector);
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

	protected interface ValueChooserWrapper  {
		public boolean finishedBranching(IntBranchingDecision decision);

		public int getFirstBranch(IntBranchingDecision decision);


		public int getNextBranch(IntBranchingDecision decision);
	}

    @SuppressWarnings({"unchecked"})
	protected static final class ValIteratorWrapper  implements ValueChooserWrapper {
        private final ValIterator valHeuristic;

        public ValIteratorWrapper(final ValIterator valHeuristic) {
            this.valHeuristic = valHeuristic;
        }

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

    @SuppressWarnings({"unchecked"})
	protected static final class ValSelectorWrapper implements ValueChooserWrapper {
		private final ValSelector valSelector;

        public ValSelectorWrapper(final ValSelector valSelector) {
            this.valSelector = valSelector;
        }

        public boolean finishedBranching(final IntBranchingDecision decision) {
			return decision.getBranchingIntVar().getDomainSize() == 0;
		}

		public int getFirstBranch(final IntBranchingDecision decision) {
			return valSelector.getBestVal(decision.getBranchingIntVar());
		}

		public int getNextBranch(final IntBranchingDecision decision) {
			return valSelector.getBestVal(decision.getBranchingIntVar());
		}
	}

	@Override
	public void initConstraintForBranching(SConstraint c) {
		if (varHeuristic instanceof DomOverWDeg) {
			((DomOverWDeg) varHeuristic).initConstraintForBranching(c);
		}
	}
}
