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

import choco.cp.model.managers.MixedConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.SettingType;
import static choco.kernel.common.util.tools.VariableUtils.getIntVar;
import static choco.kernel.common.util.tools.VariableUtils.getTaskVar;
import choco.kernel.model.variables.Variable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.global.MetaSConstraint;
import choco.kernel.solver.constraints.global.scheduling.RscData;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;


/**
 * @author Arnaud Malapert</br> 
 * @since 27 janv. 2009 version 2.0.0</br>
 * @version 2.0.3</br>
 */
public abstract class AbstractResourceManager extends MixedConstraintManager {



	protected LinkedList<SConstraint> constraints = new LinkedList<SConstraint>();

	protected TaskVar[] tasks;

	protected IntDomainVar[] usages;

	protected IntDomainVar uppBound;

	protected void initialize(CPSolver solver, Variable[] variables, RscData rdata) {
		final int n = rdata.getNbTasks();
		constraints.clear();
		tasks = getTaskVar(solver, variables, 0, n);
		usages = getIntVar(solver, variables, n, n + rdata.getNbOptionalTasks());
		uppBound =  getUppBound(solver, rdata);

	}

	@Override
	public SConstraint makeConstraint(Solver solver, Variable[] variables,
			Object parameters, HashSet<String> options) {
		if(solver instanceof CPSolver){
			CPSolver s = (CPSolver) solver;
			if (parameters instanceof RscData) {
				RscData rdata = (RscData) parameters;
				initialize(s, variables, rdata);
				if(options.contains(SettingType.GLOBAL.getOptionName()) ) {
					makeGlobalConstraint(s, variables, rdata, options);
				} else if(options.contains(SettingType.MIXED.getOptionName()) ) {
					makeMixedConstraint(s, variables, rdata, options);
				} else if( options.contains(SettingType.DECOMP.getOptionName())) {
					makeDecompositionConstraint(s, variables, rdata, options);
				}else {
					makeDefaultConstraint(s, variables, rdata, options);
				}
			}else {
				LOGGER.log(Level.WARNING, "unknown parameter for resource constraint: {0}", parameters);
			}

		}
		final int n = constraints.size();
		if( n == 0) return fail("resource constraint");
		else if(n == 1) return constraints.getFirst();
		else return new MetaSConstraint( constraints.toArray(new SConstraint[n]), tasks, null);
	}

	protected abstract void makeDecompositionConstraint(CPSolver solver, Variable[] variables, RscData rdata, HashSet<String> options);

	protected abstract void makeGlobalConstraint(CPSolver solver, Variable[] variables, RscData rdata, HashSet<String> options);

	protected abstract void makeMixedConstraint(CPSolver solver, Variable[] variables, RscData rdata, HashSet<String> options);

	protected void makeDefaultConstraint(CPSolver solver, Variable[] variables, RscData rdata, HashSet<String> options) {
		 makeGlobalConstraint(solver, variables, rdata, options);
	}

	private IntDomainVar getUppBound(CPSolver s, RscData p) {
		return  p.getUppBound() == null ? s.getSchedulerConfiguration().createMakespan(s) : s.getVar( p.getUppBound());
	}


	/**
	 * @see choco.kernel.model.constraints.ConstraintManager#getFavoriteDomains(java.util.HashSet)
	 */
	@Override
	public int[] getFavoriteDomains(final HashSet<String> options) {
		return getBCFavoriteIntDomains();
	}


}
