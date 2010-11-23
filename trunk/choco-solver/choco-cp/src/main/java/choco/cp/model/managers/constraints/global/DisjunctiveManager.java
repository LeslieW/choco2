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

import static choco.kernel.common.util.tools.VariableUtils.getIntVar;
import static choco.kernel.common.util.tools.VariableUtils.getTaskVar;

import java.util.List;

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.scheduling.disjunctive.AltDisjunctive;
import choco.cp.solver.constraints.global.scheduling.disjunctive.Disjunctive;
import choco.kernel.model.variables.Variable;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.global.scheduling.ResourceParameters;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

/**
 * @author Arnaud Malapert
 *
 */
public final class DisjunctiveManager extends AbstractResourceManager {


	
	@Override
	protected SConstraint makeConstraint(CPSolver solver,
			Variable[] variables, ResourceParameters rdata, List<String> options) {
		final int n = rdata.getUsagesOffset();
		final TaskVar[] tasks = getTaskVar(solver, variables, 0, n);
		final IntDomainVar[] usages = getIntVar(solver, variables, n, rdata.getHeightsOffset());
		final IntDomainVar horizon = getHorizon(solver, variables, rdata);
		final Disjunctive cstr = (
				rdata.isAlternative() ? 
						new AltDisjunctive((CPSolver) solver, rdata.getRscName(), tasks, usages, horizon) :
							new Disjunctive(rdata.getRscName(), tasks, horizon, solver)
		);
		cstr.readOptions(options);
		return cstr;
	}
	
}
