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
package choco.cp.solver.configure;

import choco.Choco;
import choco.cp.solver.constraints.global.scheduling.IncrementalPertSConstraint;
import choco.cp.solver.constraints.global.scheduling.PertSConstraint;
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
public class SchedulerConfiguration {

	
	private int origin = 0;
	
	private int horizon = Choco.MAX_UPPER_BOUND;
	/**
	 * The variable modelling the makespan
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



	public SchedulerConfiguration() {
		super();
	}

	public final int getMakespanValue() {
		return makespan == null ? Choco.MAX_UPPER_BOUND: makespan.getVal();
	}

	public final IntDomainVar getMakespan() {
		return makespan;
	}

	public final IntDomainVar createMakespan(Solver solver) {
		if(makespan == null) {
			this.makespan = solver.createBoundIntVar("makespan", origin, horizon);
		} 
		return makespan;
	}

	/**
	 * set a makespan variable by hand. 
	 */
	public final void setMakespan(IntDomainVar makespan) {
		if(this.makespan != null) {
			throw new SolverException("duplicate makespan variable");
		}
		this.makespan = makespan;
	}

	
	public final int getOrigin() {
		return origin;
	}

	public final void setOrigin(int origin) {
		this.origin = origin;
	}

	public final int getHorizon() {
		return horizon;
	}
	
	public final void setHorizon(int horizon) {
		this.horizon = horizon;
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

	public final IPrecedenceNetwork createPrecedenceNetwork(Solver solver) {
		if(usePrecedenceNetwork) {
			if( precedenceNetwork == null) {
				final TaskVar[] tasks = new TaskVar[solver.getNbTaskVars()];
				for (int i = 0; i < solver.getNbTaskVars(); i++) {
					tasks[i] = solver.getTaskVar(i);
				}
				createMakespan(solver);
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
