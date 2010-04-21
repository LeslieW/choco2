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
import choco.kernel.solver.SolverException;
import choco.kernel.solver.branch.VarSelector;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.search.ValSelector;
import choco.kernel.solver.search.integer.VarValPairSelector;

/**
 * A class for branching schemes that consider two branches: - one assigning a
 * value to an IntVar (X == v) - and the other forbidding this assignment (X != v)
 */

public class AssignOrForbidIntVarVal extends AbstractAssignOrForbidBranching {
	
	private VarSelector varHeuristic;

	public AssignOrForbidIntVarVal(VarSelector varHeuristic,
			ValSelector valSHeuristic) {
		super(valSHeuristic);
		this.varHeuristic = varHeuristic;
	}

	/** replaced by {@link AssignOrForbidIntVarValPair} */
	@Deprecated
	public AssignOrForbidIntVarVal(VarValPairSelector pairh) {
		super(null);
		throw new SolverException("replaced by AssignOrForbidIntVarValPair");
	}


	@Override
	public void goDownBranch(final IntBranchingDecision ctx) throws ContradictionException {
		if (ctx.getBranchIndex() == 0) {
			ctx.setIntVal();
		} else {
			ctx.remIntVal();
		}
		// Calls to propagate() are useless since it is done in the SearchLoop
	}

	
    /**
     * selecting the object under scrutiny (that object on which an alternative will be set)
     *
     * @return the object on which an alternative will be set (often  a variable)
     */
    public Object selectBranchingObject() throws ContradictionException {
		return varHeuristic.selectVar();
	}
}
