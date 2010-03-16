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
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.scheduling.precedence.network.IncrementalPertSConstraint;
import choco.cp.solver.constraints.global.scheduling.precedence.network.PertSConstraint;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.global.scheduling.IPrecedenceNetwork;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * @author Arnaud Malapert</br> 
 * @since 17 mars 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public class SchedulerConfiguration {


	private int origin = 0;
	
	protected boolean forceMakespan = false;
	
	protected int horizon = Choco.MAX_UPPER_BOUND;
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
	 * Decide if consistency constraints are always added or only if necessary: start + duration = end.
	 */
	protected boolean redundantReasonningsOnTasks = false;
	
	
	public SchedulerConfiguration() {
		super();
	}

	
	public final boolean isForceMakespan() {
		return forceMakespan;
	}


	public final void setForceMakespan(boolean forceMakespan) {
		this.forceMakespan = forceMakespan;
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

	public final boolean isSetPrecedenceNetwork() {
		return precedenceNetwork == null;
	}

	public final void setPrecedenceNetwork(IPrecedenceNetwork precedenceNetwork) {
		this.precedenceNetwork = precedenceNetwork;
	}

	public final IPrecedenceNetwork makePrecedenceNetwork(CPSolver solver) {
		if( precedenceNetwork == null) {
			createMakespan(solver);
			final PertSConstraint cstr = ( isIncrementalPert() ?
					new IncrementalPertSConstraint(solver, makespan) : 
						new PertSConstraint(solver, makespan)
			);
			solver.post(cstr);
			precedenceNetwork = cstr;
		}
		return precedenceNetwork;
	}

	public final boolean isRedundantReasonningsOnTasks() {
		return redundantReasonningsOnTasks;
	}

	public final void setRedundantReasonningsOnTasks(
			boolean redundantReasonningsOnTasks) {
		this.redundantReasonningsOnTasks = redundantReasonningsOnTasks;
	}


}
