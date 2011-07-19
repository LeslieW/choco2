/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package choco.cp.model.managers.constraints.integer;

import choco.cp.model.managers.MixedConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.integer.MaxOfAList;
import choco.cp.solver.constraints.integer.MaxXYZ;
import choco.cp.solver.constraints.integer.MinOfAList;
import choco.cp.solver.constraints.integer.MinXYZ;
import choco.cp.solver.constraints.set.MaxOfASet;
import choco.cp.solver.constraints.set.MinOfASet;
import choco.kernel.common.util.tools.VariableUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetVar;

import java.util.List;


/**
 * A manager to build min or max constraints
 */
public final class MinMaxManager extends MixedConstraintManager {



	@Override
	public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, List<String> options) {
		if (solver instanceof CPSolver) {
			final IntDomainVar bvar = solver.getVar((IntegerVariable) variables[0]);
			return buildConstraint(solver, parameters, bvar, variables);
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
	public SConstraint[] makeConstraintAndOpposite(Solver solver, Variable[] variables, Object parameters, List<String> options) {
		if (solver instanceof CPSolver) {
			final SConstraint[] cs = new SConstraint[2];
			if (parameters instanceof Boolean) {
				IntDomainVar Y;
				final IntDomainVar X = solver.getVar((IntegerVariable) variables[0]);
				// Introduces a intermediary variable
				if(X.hasBooleanDomain()){
					Y = solver.createBooleanVar("Y_opp");
				}else if(X.hasEnumeratedDomain()){
					Y = solver.createEnumIntVar("Y_opp", X.getInf(), X.getSup());
				}else{
					Y = solver.createBoundIntVar("Y_opp", X.getInf(), X.getSup());
				}
				solver.post(buildConstraint(solver, parameters, Y, variables));
				cs[0] = solver.eq(Y, X);
				cs[1] = solver.neq(Y, X);
				return cs;
			}
		}
		return null;
	}


	/**
	 * Build the correct constraint based on paramters
	 * @param isMin indicates minimization
	 * @param varM min variable
	 * @param set set variable
	 * @param vars pool of variables
	 * @param environment
	 * @return SConstraint
	 */
	private static SConstraint buildConstraint(Solver solver, Object parameters, IntDomainVar varM,  Variable[] variables) {
		final IEnvironment env = solver.getEnvironment();
		final int setIdx = variables.length - 1;
		//TODO change the min/max variables
		if (variables[setIdx] instanceof SetVariable) {
			final SetVar set = solver.getVar((SetVariable) variables[setIdx]);
			final IntDomainVar[] vars = VariableUtils.getIntVar(solver, variables, 0, setIdx);
			vars[0] = varM;
			Boolean isMin = null;
			Integer defVal = null;
			if(parameters instanceof Boolean) isMin = (Boolean) parameters;
			else if (parameters instanceof Object[]) {
				final Object[] params = (Object[]) parameters;
				if(checkParameter(params, 0) && params[0] instanceof Boolean) {
					isMin = (Boolean) params[0];
					if(checkParameter(params, 1) && params[1] instanceof Integer) {
						defVal = (Integer) params[1];
					}
				}
			}
			if(isMin != null) {
				return isMin  ? new MinOfASet(env, vars, set, defVal) : new MaxOfASet( env, vars, set, defVal);
			}

		}else if (parameters instanceof Boolean) {
			final IntDomainVar[] vars = VariableUtils.getIntVar(solver, variables, 0, variables.length);
			vars[0] = varM;
			final Boolean isMin = (Boolean) parameters;
			if(vars.length == 3) {
				return isMin ? new MinXYZ(vars[1], vars[2], vars[0]) : new MaxXYZ(vars[1], vars[2], vars[0]);
			} else {
				return isMin ? new MinOfAList(env, vars) : new MaxOfAList(env, vars);
			}
		}
		return null;
	}


	//	/**
	//	 * Build the correct constraint based on paramters
	//	 * @param isMin indicates minimization
	//	 * @param varOpt min variable
	//	 * @param set set variable
	//	 * @param vars pool of variables
	//	 * @param environment
	//	 * @return SConstraint
	//	 */
	//	private static SConstraint buildConstraint(Boolean isMin, IntDomainVar varOpt, SetVar set, IntDomainVar[] vars, IEnvironment environment) {
	//		if (isMin) {
	//			if(vars.length == 2){
	//				return new MinXYZ(vars[0], vars[1], varOpt);
	//			}else if(set!=null){
	//				IntDomainVar[] tmpVars = new IntDomainVar[vars.length+1];
	//				tmpVars[0] = varOpt;
	//				System.arraycopy(vars, 0, tmpVars, 1, vars.length);
	//				return new MinOfASet(environment, tmpVars, set, null);
	//			}else{
	//				IntDomainVar[] tmpVars = new IntDomainVar[vars.length+1];
	//				tmpVars[0] = varOpt;
	//				System.arraycopy(vars, 0, tmpVars, 1, vars.length);
	//				return new MinOfAList(environment, tmpVars);
	//			}
	//		} else {
	//			if(vars.length == 2){
	//				return new MaxXYZ(vars[0], vars[1], varOpt);
	//			}else if(set!=null){
	//				IntDomainVar[] tmpVars = new IntDomainVar[vars.length+1];
	//				tmpVars[0] = varOpt;
	//				System.arraycopy(vars, 0, tmpVars, 1, vars.length);
	//				return new MaxOfASet(environment, tmpVars, set, null);
	//			}else{
	//				IntDomainVar[] tmpVars = new IntDomainVar[vars.length+1];
	//				tmpVars[0] = varOpt;
	//				System.arraycopy(vars, 0, tmpVars, 1, vars.length);
	//				return new MaxOfAList(environment, tmpVars);
	//			}
	//		}
	//	}
}
