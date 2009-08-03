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

/**
 * A class for branching schemes that consider two branches: - one assigning a
 * value to an IntVar (X == v) - and the other forbidding this assignment (X !=
 * v)
 */
public final class AssignOrForbidIntVarVal extends AbstractBinIntBranching {

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

	@Override
	public void goDownBranch(Object x, int i) throws ContradictionException {
		IntVarValPair p = (IntVarValPair) x;
		if (i == 0) {
			p.var.setVal(p.val);
		} else {
			p.var.remVal(p.val);
		}
		// Calls to propagate() are useless since it is done in the SearchLoop
	}

    /**
     * Performs the action,
     * so that we go up the current branch to the father choice point.
     *
     * @param x the object on which the alternative has been set
     *          at the father choice point
     * @param i the label of the branch that has been travelled down
     *          from the father choice point
     * @throws choco.kernel.solver.ContradictionException
     *          if a domain empties or a contradiction is
     *          infered
     */
    @Override
    public void goUpBranch(Object x, int i) throws ContradictionException {}


    @Override
	protected final String getLogMessage() {
		return LOG_MSG_FORMAT_WITH_BRANCH;
	}

    /**
     * used for logging messages related to the search tree
     *
     * @param branchObject is the object of the branching
     * @param branchIndex  is the index of the branching
     * @return an string that will be printed between the branching object and the branch index
     *         Suggested implementations return LOG_DECISION_MSG[0] or LOG_DECISION_MSG[branchIndex]
     */
    @Override
    public final String getDecisionLogMsg(Object branchObject, int branchIndex) {
        IntVarValPair p = (IntVarValPair) branchObject;
        return p.var + LOG_DECISION_MSG + p.val;
    }


    /**
     * selecting the object under scrutiny (that object on which an alternative will be set)
     *
     * @return the object on which an alternative will be set (often  a variable)
     */
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
