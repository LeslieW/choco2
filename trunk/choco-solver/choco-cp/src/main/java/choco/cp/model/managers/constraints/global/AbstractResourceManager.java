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

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import choco.Options;
import choco.cp.model.managers.MixedConstraintManager;
import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.global.MetaSConstraint;
import choco.kernel.solver.constraints.global.scheduling.ResourceParameters;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;


/**
 * @author Arnaud Malapert</br> 
 * @since 27 janv. 2009 version 2.0.0</br>
 * @version 2.0.3</br>
 */
public abstract class AbstractResourceManager extends MixedConstraintManager {


	@Override
	public SConstraint makeConstraint(Solver solver, Variable[] variables,
			Object parameters, List<String> options) {
		if(solver instanceof CPSolver){
			CPSolver s = (CPSolver) solver;
			if (parameters instanceof ResourceParameters) {
				ResourceParameters rdata = (ResourceParameters) parameters;
				return makeConstraint(s, variables, rdata, options);
			}else {
				LOGGER.log(Level.WARNING, "unknown parameter for resource constraint: {0}", parameters);
			}
		}
		return null;
	}

	protected abstract SConstraint makeConstraint(CPSolver solver, Variable[] variables, ResourceParameters rdata, List<String> options);

	protected final IntDomainVar getHorizon(CPSolver s, Variable[] variables, ResourceParameters p) {
		return  p.isHorizonDefined() ? s.getVar((IntegerVariable) variables[variables.length-1]) : s.createMakespan();
	}

	/**
	 * Bounded.
	 * @see choco.kernel.model.constraints.ConstraintManager#getFavoriteDomains(java.util.List
	 */
	@Override
	public int[] getFavoriteDomains(final List<String> options) {
		return getBCFavoriteIntDomains();
	}


}
