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
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.IPrecedenceRatio;
import choco.cp.solver.search.task.OrderingValSelector;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.global.scheduling.IPrecedence;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class TaskOverWDegBinBranching extends AbstractDomOverWDegBinBranching {
	
	protected OrderingValSelector precValSelector;
	
	public TaskOverWDegBinBranching(Solver solver, IPrecedenceRatio[] varRatios, OrderingValSelector valHeuri, Number seed) {
		super(solver, varRatios, seed);
		this.precValSelector = valHeuri;
	}
		
	public void setFirstBranch(final IntBranchingDecision decision) {
		decision.setBranchingValue(precValSelector.getBestVal( (IPrecedence) decision.getBranchingObject()));
	}

	@Override
	public void goDownBranch(final IntBranchingDecision decision) throws ContradictionException {
		final IntDomainVar v = ( (IPrecedence) decision.getBranchingObject()).getBoolVar();
		if (decision.getBranchIndex() == 0) {
			updateVarWeights( v, true);
			v.setVal(decision.getBranchingValue());
		} else {
			assert decision.getBranchIndex() == 1;
			updateVarWeights( v, false);
			v.remVal(decision.getBranchingValue());
		}
	}

	@Override
	public Object selectBranchingObject() throws ContradictionException {
		IntRatio best = getRatioSelector().selectIntRatio();
		return best == null ? null :  ( (IPrecedenceRatio) best).getPrecedence();
	}
	
	
	
}
