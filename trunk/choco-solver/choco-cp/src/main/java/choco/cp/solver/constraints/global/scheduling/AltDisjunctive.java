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
package choco.cp.solver.constraints.global.scheduling;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.TaskVar;

/**
 * @author Arnaud Malapert</br> 
 * @since 2 mars 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public class AltDisjunctive extends Disjunctive {

	public AltDisjunctive(final String name, final TaskVar[] taskvars, final IntDomainVar[] usages, final IntDomainVar makespan, Solver solver) {
		super(solver, name, taskvars, usages.length, true, ArrayUtils.append(usages, new IntDomainVar[]{makespan}));
		rules = new AltDisjRules(rtasks, this.makespan, solver.getEnvironment());
	}

	@Override
	public void fireTaskRemoval(IRTask rtask) {
		rules.remove(rtask);
	}


	
	
	

}
