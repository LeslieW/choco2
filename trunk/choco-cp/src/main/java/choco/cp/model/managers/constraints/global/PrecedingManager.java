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
import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.scheduling.Precedence;
import choco.cp.solver.constraints.global.scheduling.PrecedenceDisjoint;
import choco.cp.solver.constraints.global.scheduling.PrecedenceReified;
import choco.cp.solver.constraints.global.scheduling.VariablePrecedenceDisjoint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.global.scheduling.IPrecedenceNetwork;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

import java.util.HashSet;



/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 11 août 2008
 * Time: 16:49:22
 */
public class PrecedingManager extends IntConstraintManager {



	protected final SConstraint createSolverConstraint(CPSolver s, TaskVar t1, TaskVar t2, IntDomainVar dir) {
		if(dir.isInstantiated()) {
			//the precedence is fixed, add a linear constraint
			return dir.isInstantiatedTo(1) ? s.leq(t1.end(), t2.start()) : s.leq(t2.end(), t1.start()); 
		}else {
			return new VariablePrecedenceDisjoint(dir, t1.start(), t1.duration(), t2.start(), t2.duration());
		}
	}




	protected final SConstraint createNetworkConstraint(IPrecedenceNetwork network, TaskVar t1, TaskVar t2, IntDomainVar dir) {
		if(dir.isInstantiated()) {
			//add a static precedence
			if(dir.isInstantiatedTo(1)) {network.addStaticPrecedence(t1, t2);}
			else {network.addStaticPrecedence(t2, t1);}
			return CPSolver.TRUE;
		} else {return new Precedence(network, t1, t2, dir);}
	}

	protected final SConstraint createIntVarsConstraint(CPSolver solver, Variable[] variables) {
		if (variables.length==4){
			IntDomainVar x0 = solver.getVar((IntegerVariable)variables[0]);
			int k = ((IntegerConstantVariable)variables[1]).getValue();
			IntDomainVar x1 = solver.getVar((IntegerVariable)variables[2]);
			IntDomainVar b = solver.getVar((IntegerVariable)variables[3]);
			return new PrecedenceReified(x0, k, x1, b);
		}else if(variables.length==5){
			IntDomainVar v1 = solver.getVar((IntegerVariable)variables[0]);
			int d1 = ((IntegerConstantVariable)variables[1]).getValue();
			IntDomainVar v2 = solver.getVar((IntegerVariable)variables[2]);
			int d2 = ((IntegerConstantVariable)variables[3]).getValue();
			IntDomainVar bool = solver.getVar((IntegerVariable)variables[4]);
			return new PrecedenceDisjoint(v1, d1, v2, d2, bool);
		} else {
			throw new SolverException("invalid precedence signature");
		}
	}


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
			final CPSolver s = (CPSolver) solver;
			if(parameters == Boolean.TRUE) {
				//precedence between tasks
				final TaskVar t1 = solver.getVar((TaskVariable) variables[0]);
				final TaskVar t2 = solver.getVar((TaskVariable) variables[1]);
				final IntDomainVar dir = solver.getVar((IntegerVariable)variables[2]);
				final IPrecedenceNetwork network = s.getScheduler().createPrecedenceNetwork();
				if(network == null ) {
					//scheduler does not use a global precedence network
					return createSolverConstraint(s, t1, t2, dir);
				}else {
					//a global network handles precedence
					return createNetworkConstraint(network, t1, t2, dir);
				}
			}else {
				//a precedence between int vars
				return createIntVarsConstraint(s, variables);
			}
		}
		if (Choco.DEBUG) {
			LOGGER.severe("Could not found an implementation of preceding/precedingReifeid !");
		}
		return null;
	}
}
