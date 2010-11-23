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
package choco.cp.solver.search.integer.varselector.ratioselector.ratios.task;

import choco.cp.solver.search.integer.varselector.ratioselector.IntVarRatioSelector;
import choco.cp.solver.search.task.OrderingValSelector;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.search.integer.IntVarValPair;
import choco.kernel.solver.search.integer.VarValPairSelector;


public final class CompositePrecValSelector implements VarValPairSelector {

	private final ITemporalRatio[] precRatios;

	private IntVarRatioSelector varSel;
	
	private OrderingValSelector valSel;

	public CompositePrecValSelector(ITemporalRatio[] precRatios, IntVarRatioSelector varSel, OrderingValSelector valSel) {
		this.precRatios = precRatios;
		this.varSel = varSel;
		this.valSel = valSel;
	}
	
	@Override
	public IntVarValPair selectVarValPair() throws ContradictionException {
		final int idx = varSel.selectIntRatioIndex();
		return idx >= 0 ?new IntVarValPair(precRatios[idx].getIntVar(), valSel.getBestVal(precRatios[idx].getTemporalRelation())) : null;
	}
}
