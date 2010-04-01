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
package choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.slack;

import choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.AbstractPrecedenceRatio;
import choco.kernel.common.util.tools.TaskUtils;
import choco.kernel.solver.constraints.global.scheduling.IPrecedence;


public final class IncSlackWDegRatio extends AbstractPrecedenceRatio {

	public IncSlackWDegRatio(IPrecedence precedence) {
		super(precedence);
	}

	@Override
	public int getDividend() {
		return TaskUtils.getTotalSlack(precedence);
	}
		
	@Override
	public int getDivisor() {
		return DomWDegUtils.getVarExtension(getIntVar()).get();
	}
}
