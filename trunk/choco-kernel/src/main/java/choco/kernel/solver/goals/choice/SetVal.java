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
package choco.kernel.solver.goals.choice;


import java.util.logging.Level;

import choco.kernel.common.logging.WorldFormatter;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.branch.AbstractBranching;
import choco.kernel.solver.goals.Goal;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 19 mai 2007
 * Time: 10:28:16
 * To change this template use File | Settings | File Templates.
 */
public class SetVal implements Goal {


	protected IntDomainVar var;
	protected int val;

	public SetVal(IntDomainVar var, int val) {
		this.var = var;
		this.val = val;
	}

	public String pretty() {
		return var.pretty() + " <= " + val;
	}

	public Goal execute(Solver s) throws ContradictionException {
		if (LOGGER.isLoggable(Level.INFO)) {
			final WorldFormatter wl = new WorldFormatter(s);
			if ( wl.isLoggable(s)) {
				LOGGER.log(Level.INFO, "{0} {1} {2} {3} {4}", new Object[]{wl, AbstractBranching.LOG_DOWN_MSG, var, " == ", val});
			}
		}
		var.setVal(val);
		return null;
	}
}