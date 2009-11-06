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
import choco.cp.solver.constraints.global.scheduling.Precedence;
import choco.cp.solver.constraints.global.scheduling.PrecedenceDisjoint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.global.scheduling.IPrecedenceNetwork;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;



/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 11 août 2008
 * Time: 16:49:22
 */
public class PrecedenceDisjointManager extends AbstractPrecedenceManager {

	

	
	@Override
	protected SConstraint makeIntConstraintB0(CPSolver s, IntDomainVar x1,
			int k1, IntDomainVar x2, int k2) {
		return s.leq( s.plus(x2,k2), x1);
	}

	@Override
	protected SConstraint makeIntConstraint(CPSolver s, IntDomainVar x1,
			int k1, IntDomainVar x2, int k2, IntDomainVar dir) {
		return new PrecedenceDisjoint(x1, k1, x2, k2, dir);
	}
	

	@Override
	protected SConstraint makeTaskConstraintB0(CPSolver s, TaskVar t1, int k1,
			TaskVar t2, int k2) {
		if( s.getSchedulerConfiguration().isUsingPrecedenceNetwork() && k2 == 0) {
			s.getSchedulerConfiguration().makePrecedenceNetwork(s).addStaticPrecedence(t2, t1);
			return CPSolver.TRUE;
		}
		return s.preceding(t2, k2, t1);
	}
	
	@Override
	protected SConstraint makeTaskConstraintB1(CPSolver s, TaskVar t1, int k1,
			TaskVar t2, int k2) {
		if( s.getSchedulerConfiguration().isUsingPrecedenceNetwork() && k1 == 0) {
			s.getSchedulerConfiguration().makePrecedenceNetwork(s).addStaticPrecedence(t1, t2);
			return CPSolver.TRUE;
		}
		return s.preceding(t1, k1, t2);
	}

	
	@Override
	protected SConstraint makeTaskConstraint(CPSolver s, TaskVar t1, int k1,
			TaskVar t2, int k2, IntDomainVar dir) {
		if( s.getSchedulerConfiguration().isUsingPrecedenceNetwork() && k1 == 0 && k2 == 0) {
			//post into the precedence network
			return new Precedence(s.getSchedulerConfiguration().makePrecedenceNetwork(s), t1, t2, dir);
		}else {
			//post into solver constraints;
			return s.preceding(dir, t1, k1, t2, k2);
		} 
	}

}
