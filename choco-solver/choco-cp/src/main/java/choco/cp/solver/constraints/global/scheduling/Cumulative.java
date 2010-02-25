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

import static choco.cp.solver.SettingType.*;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

import java.util.Arrays;



/**
 * @author Arnaud Malapert
 *
 */
public class Cumulative extends AbstractCumulativeSConstraint  {

	protected ICumulSweep cumulSweep;

	protected ICumulRules cumulRules;

	protected boolean noFixPoint;

	
	protected Cumulative(Solver solver, String name, TaskVar[] taskvars,
			int nbOptionalTasks, IntDomainVar consumption,
			IntDomainVar capacity, IntDomainVar uppBound,
			IntDomainVar... otherVars) {
		super(solver, name, taskvars, nbOptionalTasks, consumption, capacity, uppBound,
				otherVars);
	}


	public Cumulative(Solver solver, String name, final TaskVar[] taskvars, final IntDomainVar[] heights, IntDomainVar consumption, IntDomainVar capacity, IntDomainVar uppBound) {
		super(solver, name,taskvars,0, consumption,capacity, uppBound, heights);
		cumulSweep = new CumulSweep(this, Arrays.asList(rtasks));
		cumulRules = new CumulRules(this);
	}


	public final ICumulSweep getSweep() {
		return cumulSweep;
	}

	public final ICumulRules getRules() {
		return cumulRules;
	}



	protected void checkRulesRequirement() {
		if( !hasOnlyPosisiveHeights()) {
			throw new SolverException("Task interval and Edge Finding for producer/consumer cumulative resource is not supported.");
		}
	}
	//****************************************************************//
	//********* Propgation Events ************************************//
	//****************************************************************//


	/**
	 * Main loop to achieve the fix point over the
	 * sweep and edge-finding algorithms
	 *
	 * @throws ContradictionException
	 */
	public final void filter() throws ContradictionException {
		noFixPoint = true;
		final boolean hasTaskInterval = flags.or(TASK_INTERVAL, VHM_CEF_ALGO_N2K, VILIM_CEF_ALGO, TASK_INTERVAL_SLOW) ;
		final boolean hasEdgeFinding = flags.or(VHM_CEF_ALGO_N2K, VILIM_CEF_ALGO) ;
		if( hasTaskInterval) { checkRulesRequirement();}
		while (noFixPoint) {  // apply the sweep process until saturation
			noFixPoint = false;
			noFixPoint |= cumulSweep.sweep();
			if ( hasTaskInterval) {
				//initial sorting of the tasks
				cumulRules.initializeEdgeFindingStart();
				//1-) Ensure first E-feasability, also called overload checking (Vilim)
				if(flags.contains(TASK_INTERVAL_SLOW) ) {
					cumulRules.slowTaskIntervals();
					//( (CumulRules) cumulRules).oldSlowTaskIntervals();
				}else {
					cumulRules.taskIntervals();
				}
				//2-) then compute the set different heights dynamically
				if ( hasEdgeFinding) {
					if (isInstantiatedHeights()) {
						cumulRules.reinitConsumption();
					} else {
						cumulRules.initializeEdgeFindingData();
					}

					//3-) Prune the starting dates with edge finding rule
					if (flags.contains(VILIM_CEF_ALGO)) {
						noFixPoint |= cumulRules.vilimStartEF();   // in O(n^2 \times k)
					} else if (flags.contains(VHM_CEF_ALGO_N2K)) {
						noFixPoint |= cumulRules.calcEF_start();    // in O(n^2 \times k)
					}

					//4-) reset the flags of dynamic computation
					cumulRules.reinitConsumption();

					//5-) Prune ending dates with edge finding rule
					cumulRules.initializeEdgeFindingEnd();

					if (flags.contains(VILIM_CEF_ALGO)) {
						noFixPoint |= cumulRules.vilimEndEF();    //O(n^2 \times k)
					} else if (flags.contains(VHM_CEF_ALGO_N2K)) {
						noFixPoint |= cumulRules.calcEF_end();    // in O(n^2 \times k)
					}
				}
			}
		}
	}


	@Override
	public void awake() throws ContradictionException {
		if (flags.or(VHM_CEF_ALGO_N2K, VILIM_CEF_ALGO) && isInstantiatedHeights() && hasOnlyPosisiveHeights()) {
			checkRulesRequirement();
			cumulRules.initializeEdgeFindingData();
		}
		super.awake();
	}


	/**
	 * @see choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint#propagate()
	 */
	@Override
	public void propagate() throws ContradictionException {
		filter();
	}



}
