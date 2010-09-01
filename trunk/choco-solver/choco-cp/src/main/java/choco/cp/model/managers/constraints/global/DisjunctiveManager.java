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
import choco.cp.solver.SettingType;
import choco.cp.solver.constraints.global.scheduling.disjunctive.AltDisjunctive;
import choco.cp.solver.constraints.global.scheduling.disjunctive.Disjunctive;
import choco.cp.solver.constraints.global.scheduling.disjunctive.ForbiddenIntervals;
import choco.kernel.model.variables.Variable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.global.scheduling.RscData;

import java.util.Set;
import java.util.logging.Level;

/**
 * @author Arnaud Malapert
 *
 */
public final class DisjunctiveManager extends AbstractResourceManager {


	@Override
	protected void makeDecompositionConstraint(CPSolver solver,
			Variable[] variables, RscData rdata, Set<String> options) {
		if( rdata.isAlternative()) {
			LOGGER.log(Level.INFO, "no decomposition available: use {0} instead",  SettingType.MIXED);
			makeMixedConstraint(solver, variables, rdata, options);
		}else {
			makeDecompositionDisjunctive(solver, rdata);
		}
	}

	@Override
	protected void makeGlobalConstraint(CPSolver solver,
			Variable[] variables, RscData rdata, Set<String> options) {
		makeDisjunctive(solver, rdata, options);
	}

	@Override
	protected void makeMixedConstraint(CPSolver solver,
			Variable[] variables, RscData rdata, Set<String> options) {
		makeGlobalConstraint(solver, variables, rdata, options);
		makeDecompositionDisjunctive(solver, rdata);
	}
	
	
	protected final void makeDisjunctive(Solver solver, RscData rdata, Set<String> options) {
		final Disjunctive cstr = (
				rdata.isAlternative() ? 
						new AltDisjunctive((CPSolver) solver, rdata.getRscName(), tasks, usages, uppBound) :
							new Disjunctive(rdata.getRscName(), tasks, uppBound, solver)
		);
		cstr.getFlags().readDisjunctiveOptions(options);
		constraints.addFirst(cstr);
	}
	
	protected void makeDecompositionDisjunctive(CPSolver s, RscData rdata) {
		final int n = rdata.getNbRegularTasks();
		for (int i = 0; i < n; i++) {
			for (int j = i+1; j < n; j++) {
				constraints.add( s.preceding(null, tasks[i], 0, tasks[j], 0));
			}
		}
	}
}
