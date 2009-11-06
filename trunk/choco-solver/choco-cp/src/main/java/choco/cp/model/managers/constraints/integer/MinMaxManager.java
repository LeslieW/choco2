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
import choco.cp.model.managers.MixedConstraintManager;
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
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetVar;

import java.util.HashSet;


/**
 * A manager to build min or max constraints
 */
public class MinMaxManager extends MixedConstraintManager {



	public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, HashSet<String> options) {
		if (solver instanceof CPSolver) {
			if (parameters instanceof Boolean) {
				Boolean min = (Boolean) parameters;
				final int n = variables.length-1;
				IntDomainVar varOpt = solver.getVar((IntegerVariable) variables[n]);
                IntegerVariable[] vars;
                SetVar set = null;
                if (variables[0] instanceof SetVariable) {
                    set = solver.getVar((SetVariable) variables[0]);
                    vars = new IntegerVariable[n-1];
                    //noinspection SuspiciousSystemArraycopy
                    System.arraycopy(variables, 1, vars, 0, n-1);
                }else {
                    vars = new IntegerVariable[n];
                    //noinspection SuspiciousSystemArraycopy
                    System.arraycopy(variables, 0, vars, 0, n);
                }
                return buildConstraint(min, varOpt, set, solver.getVar(vars));
			}
		}
		if (Choco.DEBUG) {
			throw new RuntimeException("Could not found an implementation of min or max !");
		}
		return null;
	}


    /**
     * Build a constraint and its opposite for the given solver and "model variables"
     *
     * @param solver
     * @param variables
     * @param parameters
     * @param options
     * @return array of 2 SConstraint object, the constraint and its opposite
     */
//    @Override
    public SConstraint[] makeConstraintAndOpposite(Solver solver, Variable[] variables, Object parameters, HashSet<String> options) {
        SConstraint[] cs = new SConstraint[2];
        if (solver instanceof CPSolver) {
			if (parameters instanceof Boolean) {
				Boolean min = (Boolean) parameters;
				final int n = variables.length-1;
                IntDomainVar Y;
                final IntDomainVar X = solver.getVar((IntegerVariable) variables[n]);
                // Introduces a intermediary variable
                if(X.hasBooleanDomain()){
                    Y = solver.createBooleanVar("Y_opp");
                }else if(X.hasEnumeratedDomain()){
                    Y = solver.createEnumIntVar("Y_opp", X.getInf(), X.getSup());
                }else{
                    Y = solver.createBoundIntVar("Y_opp", X.getInf(), X.getSup());
                }
                IntegerVariable[] vars;
                SetVar set = null;
                if (variables[0] instanceof SetVariable) {
                    set = solver.getVar((SetVariable) variables[0]);
                    vars = new IntegerVariable[n-1];
                    //noinspection SuspiciousSystemArraycopy
                    System.arraycopy(variables, 1, vars, 0, n-1);
                }else {
                    vars = new IntegerVariable[n];
                    //noinspection SuspiciousSystemArraycopy
                    System.arraycopy(variables, 0, vars, 0, n);
                }
                solver.post(buildConstraint(min, Y, set, solver.getVar(vars)));
                cs[0] = solver.eq(Y, X);
                cs[1] = solver.neq(Y, X);
                return cs;
			}
		}
		if (Choco.DEBUG) {
			throw new RuntimeException("Could not found an implementation of min or max !");
		}
		return null;
    }


    /**
     * Build the correct constraint based on paramters
     * @param isMin indicates minimization
     * @param varOpt min variable
     * @param set set variable
     * @param vars pool of variables
     * @return SConstraint
     */
    private static SConstraint buildConstraint(Boolean isMin, IntDomainVar varOpt, SetVar set, IntDomainVar[] vars) {
        if (isMin) {
            if(vars.length == 2){
                return new MinXYZ(vars[0], vars[1], varOpt);
            }else if(set!=null){
                IntDomainVar[] tmpVars = new IntDomainVar[vars.length+1];
                tmpVars[0] = varOpt;
                System.arraycopy(vars, 0, tmpVars, 1, vars.length);
                return new MinOfASet(tmpVars, set);
            }else{
                IntDomainVar[] tmpVars = new IntDomainVar[vars.length+1];
                tmpVars[0] = varOpt;
                System.arraycopy(vars, 0, tmpVars, 1, vars.length);
                return new MinOfAList(tmpVars);
            }
        } else {
            if(vars.length == 2){
                return new MaxXYZ(vars[0], vars[1], varOpt);
            }else if(set!=null){
                IntDomainVar[] tmpVars = new IntDomainVar[vars.length+1];
                tmpVars[0] = varOpt;
                System.arraycopy(vars, 0, tmpVars, 1, vars.length);
                return new MaxOfASet(tmpVars, set);
            }else{
                IntDomainVar[] tmpVars = new IntDomainVar[vars.length+1];
                tmpVars[0] = varOpt;
                System.arraycopy(vars, 0, tmpVars, 1, vars.length);
                return new MaxOfAList(tmpVars);
            }
        }
    }
}
