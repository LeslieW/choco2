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
import choco.cp.solver.constraints.global.pack.PackSConstraint;
import choco.kernel.common.util.bitmask.BitMask;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetVar;

import java.util.List;

import static choco.kernel.common.util.tools.VariableUtils.getIntVar;
import static choco.kernel.common.util.tools.VariableUtils.getSetVar;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 8 août 2008
 * Time: 20:08:04
 */
public final class PackManager extends MixedConstraintManager {

	/**
	 * Build a constraint for the given solver and "model variables"
	 *
	 * @param solver
	 * @param variables
	 * @param parameters : a "hook" to attach any kind of parameters to constraints
	 * @param options
	 * @return
	 */
	@Override
	public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, List<String> options) {
		if(solver instanceof CPSolver){
			CPSolver s = (CPSolver) solver;
			if(parameters instanceof Object[]){
				Object[] params = (Object[])parameters;
				final int n = (Integer) params[0]; //nb items
				final int m = (Integer) params[1]; //nb bins
				final int v1 = 2 * m;
				final int v2 = v1 + n;
				final int v3 = v2 + n;
				final SetVar[] itemSets = getSetVar(s, variables, 0, m);
				final IntDomainVar[] loads = getIntVar(s, variables, m, v1);
				final IntDomainVar[] bins = getIntVar(s, variables, v1, v2);
				final IntDomainVar[] sizes = getIntVar(s, variables, v2, v3);
				final IntDomainVar  nbNonEmpty = solver.getVar((IntegerVariable)variables[v3]);
				final PackSConstraint ct = new PackSConstraint(s.getEnvironment(), itemSets, loads, sizes, bins, nbNonEmpty);
				ct.readOptions(options);	
				return ct;
			}
		}
		return fail("pack");
	}

	@Override
	public int[] getFavoriteDomains(final List<String> options) {
		return getBCFavoriteIntDomains();
	}

}
