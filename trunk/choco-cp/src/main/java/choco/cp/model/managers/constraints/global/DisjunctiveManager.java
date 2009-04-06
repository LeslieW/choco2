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
import choco.cp.solver.constraints.global.scheduling.AltDisjunctive;
import choco.cp.solver.constraints.global.scheduling.Disjunctive;
import choco.cp.solver.constraints.global.scheduling.ForbiddenIntervals;
import choco.kernel.model.variables.Variable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.global.scheduling.RscData;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

import java.util.HashSet;


/**
 * @author Arnaud Malapert
 *
 */
public class DisjunctiveManager extends AbstractResourceManager {



	@Override
	public int[] getFavoriteDomains(HashSet<String> options) {
		return this.getBCFavoriteIntDomains();
	}

	@Override
	public SConstraint makeConstraint(Solver solver, Variable[] variables,
			Object parameters, HashSet<String> options) {
		if (solver instanceof CPSolver) {
			final RscData param = (RscData) parameters;
			final CPSolver s = (CPSolver) solver;
			final TaskVar[] tasks = readTaskVar(s, variables, 0, param.getNbTasks());
			final IntDomainVar uppBound =  getUppBound(s, param);
			if( options.contains(SettingType.FORBIDDEN_INTERVALS.getOptionName()) ) {
				if( uppBound == null ) {
					throw new SolverException("you must set the makespan variable or manually set an upper bound variable");
				} if( param.getNbOptionalTasks() > 0 ) {
					throw new SolverException("no Forbidden intervals with alternative resources.");
				} else {
					s.post(new ForbiddenIntervals("ForbInt-"+param.getRscName(), tasks, uppBound));
				}
			}

			Disjunctive cstr;
			if(param.getNbOptionalTasks() > 0) {
				IntDomainVar[] usages = readIntVar(s, variables, param.getNbTasks() , param.getNbOptionalTasks());
				cstr = new AltDisjunctive(param.getRscName(), tasks, usages, uppBound);
			}else {
				cstr = new Disjunctive(param.getRscName(), tasks, uppBound);
			}
			 
			readDisjunctiveSettings(options, cstr.getFlags());
			return cstr;
		}
		return null;
	}

}
