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

import choco.cp.solver.constraints.global.scheduling.precedence.ITemporalSRelation;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.IntRatio;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.ITemporalRatio;
import choco.cp.solver.search.task.OrderingValSelector;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class TaskOverWDegBinBranching extends AbstractDomOverWDegBinBranching {

	private final OrderingValSelector precValSelector;

	public TaskOverWDegBinBranching(Solver solver, ITemporalRatio[] varRatios, OrderingValSelector valHeuri, Number seed) {
		super(solver, varRatios, seed);
		this.precValSelector = valHeuri;
	}

	public void setFirstBranch(final IntBranchingDecision decision) {
		final ITemporalSRelation brObj =  (ITemporalSRelation) decision.getBranchingObject();
		decision.setBranchingValue(precValSelector.getBestVal( brObj));
		decreaseVarWeights( brObj.getDirection());
	}

	@Override
	public void setNextBranch(IntBranchingDecision decision) {
		if( updateWeightsCount == getExpectedUpdateWeightsCount() ) {
			increaseVarWeights( ((ITemporalSRelation) decision.getBranchingObject()).getDirection());
		} else updateWeightsCount = Integer.MIN_VALUE;
		super.setNextBranch(decision);
	}



	@Override
	public void goDownBranch(final IntBranchingDecision decision) throws ContradictionException {
		final IntDomainVar v = ( (ITemporalSRelation) decision.getBranchingObject()).getDirection();
		if (decision.getBranchIndex() == 0) {
			v.setVal(decision.getBranchingValue());
		} else {
			assert decision.getBranchIndex() == 1;
			v.remVal(decision.getBranchingValue());
		}
	}

	@Override
	public Object selectBranchingObject() throws ContradictionException {
		reinitBranching();
		IntRatio best = getRatioSelector().selectIntRatio();
		return best == null ? null :  ( (ITemporalRatio) best).getTemporalRelation();
	}


}
