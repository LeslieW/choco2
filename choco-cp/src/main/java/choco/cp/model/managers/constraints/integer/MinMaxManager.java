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
package choco.cp.model.managers.constraints.integer;

import choco.Choco;
import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.integer.MaxOfAList;
import choco.cp.solver.constraints.integer.MaxXYZ;
import choco.cp.solver.constraints.integer.MinOfAList;
import choco.cp.solver.constraints.integer.MinXYZ;
import choco.cp.solver.constraints.set.MaxOfASet;
import choco.cp.solver.constraints.set.MinOfASet;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.HashSet;


/**
 * A manager to build min or max constraints
 */
public class MinMaxManager extends IntConstraintManager {



	public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, HashSet<String> options) {
		if (solver instanceof CPSolver) {
			if (parameters instanceof Boolean) {
				Boolean min = (Boolean) parameters;
				final int n = variables.length-1;
				IntegerVariable varOpt = (IntegerVariable) variables[n];
				if (min) {
					if (variables.length == 3) {
						return new MinXYZ(solver.getVar((IntegerVariable) variables[0]),
								solver.getVar((IntegerVariable) variables[1]),
								solver.getVar(varOpt));
					} else if (variables[0] instanceof SetVariable) {
						final SetVariable set = (SetVariable) variables[0];
						final IntegerVariable[] tmp = new IntegerVariable[n];
						tmp[0]=varOpt;
						System.arraycopy(variables, 1, tmp, 1, n-1);
						return new MinOfASet(solver.getVar(tmp),solver.getVar(set));
					}else {
						IntegerVariable[] tmpVars = new IntegerVariable[variables.length];
						tmpVars[0] = varOpt;
						System.arraycopy(variables, 0, tmpVars, 1, variables.length-1);
						return new MinOfAList(solver.getVar(tmpVars));
					}
				} else {
					if (variables.length == 3) {
						return new MaxXYZ(solver.getVar((IntegerVariable) variables[0]),
								solver.getVar((IntegerVariable) variables[1]),
								solver.getVar(varOpt));
					} else if (variables[0] instanceof SetVariable) {
						final SetVariable set = (SetVariable) variables[0];
						final IntegerVariable[] tmp = new IntegerVariable[n];
						tmp[0]=varOpt;
						System.arraycopy(variables, 1, tmp, 1, n-1);
						return new MaxOfASet(solver.getVar(tmp),solver.getVar(set));
					}else {
						IntegerVariable[] tmpVars = new IntegerVariable[variables.length];
						tmpVars[0] = varOpt;
						System.arraycopy(variables, 0, tmpVars, 1, variables.length-1);
						return new MaxOfAList(solver.getVar(tmpVars));
					}
				}
			}
		}
		if (Choco.DEBUG) {
			throw new RuntimeException("Could not found an implementation of min or max !");
		}
		return null;
	}
}
