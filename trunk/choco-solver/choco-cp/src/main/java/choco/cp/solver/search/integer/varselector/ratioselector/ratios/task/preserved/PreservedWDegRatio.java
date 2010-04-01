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
package choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.preserved;

import choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.AbstractPrecedenceRatio;
import choco.kernel.common.util.tools.TaskUtils;
import choco.kernel.solver.constraints.global.scheduling.IPrecedence;
import choco.kernel.solver.variables.scheduling.TaskVar;

public class PreservedWDegRatio extends AbstractPrecedenceRatio {
	
	private int dividend;
	
	protected final static int DIVISOR = 1 << 10;
	
	public PreservedWDegRatio(IPrecedence precedence) {
		super(precedence);
	}

	@Override
	public int getDivisor() {
		return DIVISOR * DomWDegUtils.computeWeightedDegreeFromScratch(getIntVar());
	}

	@Override
	public final int getDividend() {
		return dividend;
	}

	@Override
	public final boolean isActive() {
		if(getIntVar().isInstantiated()) return false;
		else {
			final TaskVar t1 = precedence.getOrigin();
			final TaskVar t2 = precedence.getDestination();
			dividend =  (int) ( TaskUtils.getTotalPreserved(t1, t2) * DIVISOR); 
			return true;
		}
	}
	
	
	
}
