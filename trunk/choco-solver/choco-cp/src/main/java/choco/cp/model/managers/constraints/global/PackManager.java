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
import choco.cp.solver.constraints.BitFlags;
import choco.cp.solver.constraints.global.pack.PackSConstraint;
import static choco.kernel.common.util.tools.VariableUtils.getIntVar;
import static choco.kernel.common.util.tools.VariableUtils.getSetVar;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetVar;

import java.util.Set;
/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 8 août 2008
 * Time: 20:08:04
 */
public class PackManager extends MixedConstraintManager {

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
	public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, Set<String> options) {
		if(solver instanceof CPSolver){
			CPSolver s = (CPSolver) solver;
			if(parameters instanceof Object[]){
				Object[] params = (Object[])parameters;
				final int n = (Integer) params[0]; //nb items
				final int m = (Integer) params[1]; //nb bins
				final int v1 = 2 * m;
				final int v2 = v1 + n;
				final int v3 = v2 + n;
				SetVar[] itemSets = getSetVar(s, variables, 0, m);
				IntDomainVar[] loads = getIntVar(s, variables, m, v1);
				IntDomainVar[] bins = getIntVar(s, variables, v1, v2);
				IntDomainVar[] sizes = getIntVar(s, variables, v2, v3);
				IntDomainVar  nbNonEmpty = solver.getVar((IntegerVariable)variables[v3]);
				BitFlags flags = new BitFlags();
				flags.readPackOptions(options);
				return new PackSConstraint(s.getEnvironment(), itemSets, loads, sizes, bins, nbNonEmpty, flags);
			}
		}
		return fail("pack");
	}

	@Override
	public int[] getFavoriteDomains(final Set<String> options) {
		return getBCFavoriteIntDomains();
	}

}
