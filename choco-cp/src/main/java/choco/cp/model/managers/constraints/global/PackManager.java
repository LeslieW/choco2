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

import choco.Choco;
import choco.cp.solver.CPSolver;
import choco.cp.solver.SettingType;
import choco.cp.solver.constraints.BitFlags;
import choco.cp.solver.constraints.global.pack.PrimalDualPack;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetVar;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 8 août 2008
 * Time: 20:08:04
 */
public class PackManager extends AbstractResourceManager {

	/**
	 * Build a constraint for the given solver and "model variables"
	 *
	 * @param solver
	 * @param variables
	 * @param parameters : a "hook" to attach any kind of parameters to constraints
	 * @param options
	 * @return
	 */
	public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, HashSet<String> options) {

		if(solver instanceof CPSolver){
			CPSolver s = (CPSolver) solver;
			if(parameters instanceof Object[]){
				Object[] params = (Object[])parameters;
				final int n = (Integer)params[0]; //items
				final int m = (Integer)params[1]; //bins
				SetVar[] itemSets = readSetVar(s, variables, 0, m);
				IntDomainVar[] loads = readIntVar(s, variables, m, m);
				IntDomainVar[] bins = readIntVar(s, variables, 2*m,n);
				IntDomainVar[] sizes = readIntVar(s, variables, 2*m+n,n);
				IntDomainVar  nbNonEmpty = solver.getVar( (IntegerVariable) variables[2*(m+n)]);
				return new PrimalDualPack(itemSets, loads, sizes, bins, nbNonEmpty, readPackSettings(options));
			}
		}
		if(Choco.DEBUG){
			System.err.println("Could not found implementation for BinPacking1D");
		}
		return null;
	}


}
