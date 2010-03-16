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
package choco.cp.model.managers.constraints.global;

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.scheduling.precedence.PrecedenceReified;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

public class PrecedenceReifiedManager extends AbstractPrecedenceManager {



	@Override
	protected SConstraint makeIntConstraintB0(CPSolver s, IntDomainVar x1,
			int k1, IntDomainVar x2, int k2) {
		return s.gt( s.plus(x1, k1), x2);
	}


	@Override
	protected SConstraint makeIntConstraint(CPSolver s, IntDomainVar x1,
			int k1, IntDomainVar x2, int k2, IntDomainVar dir) {
		return new PrecedenceReified(x1, k1, x2, dir);
	}

	@Override
	protected SConstraint makeTaskConstraintB0(CPSolver s, TaskVar t1, int k1,
			TaskVar t2, int k2) {
		return s.gt(
				(t1.duration().isInstantiated() ? s.plus(t1.start(), t1.duration().getVal() + k1) : s.plus(t1.end(), k1)),
				t2.start()
		);
	}


	@Override
	protected SConstraint makeTaskConstraint(CPSolver s, TaskVar t1, int k1,
			TaskVar t2, int k2, IntDomainVar dir) {
		final PrecedenceReified c =  ( 
				t1.duration().isInstantiated() ? 
						new PrecedenceReified(t1.start(), t1.duration().getVal() + k1, t2.start(), dir) :
							new PrecedenceReified(t1.end(), k1, t2.start(), dir)
		);
		c.setTasks(t1, t2);
		return c;
	}



}
