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
package choco.cp.solver.search.integer.varselector.ratioselector;

import choco.cp.solver.search.integer.varselector.ratioselector.ratios.IntRatio;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.SimpleRatio;
import choco.kernel.solver.Solver;

public class RandMinRatioSelector extends AbstractRandomizedRatioSelector {

	public RandMinRatioSelector(Solver solver, IntRatio[] ratios, long seed) {
		super(solver, ratios, seed);
	}

	@Override
	protected final void reset(SimpleRatio bestR) {
		bestR.setMaxRatioValue();	
	}

	@Override
	protected final boolean isUp(long leftM, long rightM) {
		return leftM > rightM;
	}
}