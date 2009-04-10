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
package choco.cp.solver.constraints.global.scheduling;

import choco.Choco;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.global.scheduling.IPrecedenceNetwork;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

/**
 * @author Arnaud Malapert</br> 
 * @since 17 mars 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public class SchedulerConfig {

	public final Solver solver;

	/**
	 * The variable modelling makespan
	 */
	protected IntDomainVar makespan;

	/**
	 * the precedence constraint network, if any;
	 */
	protected IPrecedenceNetwork precedenceNetwork;
	
	/**
	 * <code>true</code> ensure that a global constraint handle precedences, 
	 * otherwise precedence constraint are linear and reified constraints 
	 */
	protected boolean usePrecedenceNetwork = false;

	/**
	 * <code>true</code> ensure incremental propagation, otherwise full propagation. 
	 */
	protected boolean incrementalPert = true;

	/**
	 * Decide if redundant constraints are automatically to the model to reason
	 * on task consistency, i.e start + duration = end.
	 */
	protected boolean redundantReasonningsOnTasks = false;



	public SchedulerConfig(Solver solver) {
		super();
		this.solver = solver;
	}

	public final int getMakespanValue() {
		return makespan == null ? Integer.MAX_VALUE : makespan.getVal();
	}

	public final IntDomainVar getMakespan() {
		return makespan;
	}

	public final IntDomainVar createMakespan() {
		if(makespan == null) {
			this.makespan = solver.createBoundIntVar("makespan", Choco.MIN_LOWER_BOUND, Choco.MAX_UPPER_BOUND);
		}
		return makespan;
	}

	public final void setMakespan(IntDomainVar makespan) {
		if(this.makespan != null) {
			throw new SolverException("duplicate makespan variable");
		}
		this.makespan = makespan;
	}

	public final void setMakespan(int horizon) {
		if(makespan == null) {
			this.makespan = solver.createBoundIntVar("makespan", Integer.MIN_VALUE, horizon);
		}else {
			solver.post(solver.leq(makespan, horizon));
		}
	}


	public final IPrecedenceNetwork getPrecedenceNetwork() {
		return precedenceNetwork;
	}



	public final boolean isUsingPrecedenceNetwork() {
		return usePrecedenceNetwork;
	}

	public final void setPrecedenceNetwork(boolean usage) {
		this.usePrecedenceNetwork = usage;
	}

	public final boolean isIncrementalPert() {
		return incrementalPert;
	}

	public final void setIncrementalPert(boolean incrementalPert) {
		this.incrementalPert = incrementalPert;
	}

	public final IPrecedenceNetwork createPrecedenceNetwork() {
		if(usePrecedenceNetwork) {
			if( precedenceNetwork == null) {
				final TaskVar[] tasks = new TaskVar[solver.getNbTaskVars()];
				for (int i = 0; i < solver.getNbTaskVars(); i++) {
					tasks[i] = solver.getTaskVar(i);
				}
				createMakespan();
				PertSConstraint cstr = isIncrementalPert() ? new IncrementalPertSConstraint(solver, makespan) : new PertSConstraint(solver, makespan);
				solver.post(cstr);
				precedenceNetwork = cstr;
			}
			return precedenceNetwork;
		}else {return null;}
	}

	public final boolean isRedundantReasonningsOnTasks() {
		return redundantReasonningsOnTasks;
	}

	public final void setRedundantReasonningsOnTasks(
			boolean redundantReasonningsOnTasks) {
		this.redundantReasonningsOnTasks = redundantReasonningsOnTasks;
	}



}
