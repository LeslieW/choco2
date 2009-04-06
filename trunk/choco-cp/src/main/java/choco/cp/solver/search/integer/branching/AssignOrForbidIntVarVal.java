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
import choco.kernel.solver.branch.AbstractBinIntBranching;
import choco.kernel.solver.branch.VarSelector;
import choco.kernel.solver.search.integer.IntVarValPair;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.search.integer.VarValPairSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.logging.Level;

/**
 * A class for branching schemes that consider two branches: - one assigning a
 * value to an IntVar (X == v) - and the other forbidding this assignment (X !=
 * v)
 */
public class AssignOrForbidIntVarVal extends AbstractBinIntBranching {
	private VarSelector varHeuristic;

	private ValSelector valSHeuristic;

	private VarValPairSelector pairHeuristic;

	public AssignOrForbidIntVarVal(VarSelector varHeuristic,
			ValSelector valSHeuristic) {
		this.varHeuristic = varHeuristic;
		this.valSHeuristic = valSHeuristic;
	}

	public AssignOrForbidIntVarVal(VarValPairSelector pairh) {
		this.pairHeuristic = pairh;
	}

	public String getDecisionLogMsg(int branchIndex) {
		if (branchIndex == 0) {
			return "==";
		}
		return "!=";
	}

	public void goDownBranch(Object x, int i) throws ContradictionException {
		super.goDownBranch(x, i);
		IntVarValPair p = (IntVarValPair) x;
		if (i == 0) {
			p.var.setVal(p.val);
			// p.getSolver().propagate();
		} else {
			p.var.remVal(p.val);
			// p.getSolver().propagate();
		}
		// Calls to propagate() are useless since it is done in the SearchLoop
	}

	protected void logDownBranch(final Object x, final int i) {
		if (logger.isLoggable(Level.FINE)) {
			int n = manager.solver.getEnvironment().getWorldIndex();
			if (n <= manager.getLoggingMaxDepth()) {
				IntVarValPair p = (IntVarValPair) x;
				logger.log(Level.FINE, LOG_DOWN_MSG, new Object[] { n, p.var,
						getDecisionLogMsg(i), p.val });
			}
		}
	}

	protected void logUpBranch(final Object x, final int i) {
		if (logger.isLoggable(Level.FINE)) {
			int n = manager.solver.getEnvironment().getWorldIndex();
			if (n <= manager.getLoggingMaxDepth()) {
				IntVarValPair p = (IntVarValPair) x;
				logger.log(Level.FINE, LOG_UP_MSG, new Object[] { n + 1, p.var,
						getDecisionLogMsg(i), p.val });
			}
		}
	}

	public Object selectBranchingObject() throws ContradictionException {
		if (pairHeuristic != null) {
			return pairHeuristic.selectVarValPair();
		}
		IntDomainVar v = (IntDomainVar) varHeuristic.selectVar();
		if (v == null) {
			return null;
		}
		return new IntVarValPair(v, valSHeuristic.getBestVal(v));
	}
}
