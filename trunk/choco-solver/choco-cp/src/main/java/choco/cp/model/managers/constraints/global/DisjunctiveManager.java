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
public class DisjunctiveManager extends AbstractResourceManager {


	@Override
	protected void makeDecompositionConstraint(CPSolver solver,
			Variable[] variables, RscData rdata, Set<String> options) {
		if( rdata.isAlternative()) {
			LOGGER.log(Level.INFO, "no decomposition available: use {0} instead",  SettingType.MIXED);
			makeMixedConstraint(solver, variables, rdata, options);
		}else {
			makeForbiddenIntervalConstraint(rdata, options, solver);
			makeDecompositionDisjunctive(solver, rdata);
		}
	}

	@Override
	protected void makeGlobalConstraint(CPSolver solver,
			Variable[] variables, RscData rdata, Set<String> options) {
		makeForbiddenIntervalConstraint(rdata, options, solver);
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
	
	protected void makeForbiddenIntervalConstraint(RscData rdata, Set<String> options, Solver solver) {
		if( options.contains(SettingType.FORBIDDEN_INTERVALS.getOptionName()) ) {
			if ( rdata.getNbOptionalTasks() > 0 ) {
				LOGGER.log(Level.WARNING, "no Forbidden intervals with alternative resources {0}", rdata);
			} 
			constraints.add(new ForbiddenIntervals(solver, "ForbInt-"+rdata.getRscName(), tasks, uppBound));
		}
	}
//
//	@Override
//	public SConstraint makeConstraint(Solver solver, Variable[] variables,
//			Object parameters, Set<String> options) {
//		if (solver instanceof CPSolver) {
//			final RscData param = (RscData) parameters;
//			final CPSolver s = (CPSolver) solver;
//			final TaskVar[] tasks = getTaskVar(s, variables, 0, param.getNbTasks());
//			final IntDomainVar uppBound =  getUppBound(s, param);
//			if( options.contains(SettingType.FORBIDDEN_INTERVALS.getOptionName()) ) {
//				if( uppBound == null ) {
//					throw new SolverException("you must set the makespan variable or manually set an upper bound variable");
//				} if( param.getNbOptionalTasks() > 0 ) {
//					throw new SolverException("no Forbidden intervals with alternative resources.");
//				} else {
//					s.post(new ForbiddenIntervals("ForbInt-"+param.getRscName(), tasks, uppBound));
//				}
//			}
//
//			Disjunctive cstr;
//			if(param.getNbOptionalTasks() > 0) {
//				IntDomainVar[] usages = getIntVar(s, variables, param.getNbTasks() , param.getNbOptionalTasks());
//				cstr = new AltDisjunctive(param.getRscName(), tasks, usages, uppBound);
//			}else {
//				cstr = new Disjunctive(param.getRscName(), tasks, uppBound);
//			}
//			cstr.getFlags().readDisjunctiveOptions(options);
//			return cstr;
//		}
//		return null;
//	}

}
