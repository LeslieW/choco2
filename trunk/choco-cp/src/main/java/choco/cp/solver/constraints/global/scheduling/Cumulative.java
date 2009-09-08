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

import static choco.cp.solver.SettingType.TASK_INTERVAL;
import static choco.cp.solver.SettingType.TASK_INTERVAL_SLOW;
import static choco.cp.solver.SettingType.VHM_CEF_ALGO_N2K;
import static choco.cp.solver.SettingType.VILIM_CEF_ALGO;

import java.util.Arrays;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.TaskVar;



/**
 * @author Arnaud Malapert
 *
 */
public class Cumulative extends AbstractCumulativeSConstraint  {

	protected ICumulSweep cumulSweep;

	protected ICumulRules cumulRules;

	protected boolean noFixPoint;

	protected Cumulative(String name, final TaskVar[] taskvars,final IntDomainVar[] heights, IntDomainVar consumption, IntDomainVar capacity, IntDomainVar uppBound, IntDomainVar... otherVars) {
		super(name,taskvars,heights,consumption,capacity,uppBound,otherVars);
	}

	public Cumulative(String name, final TaskVar[] taskvars,final IntDomainVar[] heights, IntDomainVar consumption, IntDomainVar capacity, IntDomainVar uppBound) {
		super(name,taskvars,heights,consumption,capacity, uppBound);
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

	@Override
	public boolean isSatisfied(int tuple[]) {
		if(tuple[indexCapacity] < tuple[indexConsumption]) return false;

		int start = Integer.MAX_VALUE, end = Integer.MIN_VALUE;
		final int n = getNbTasks();
		//compute execution interval.
		for (int tidx = 0; tidx < n; tidx++) {
			if( ! isTaskSatisfied(tuple, tidx)) return false;
			else {
				start = Math.min(start,  tuple[tidx]);
				end = Math.max(end,  tuple[startOffset + tidx]);
			}
		}
		if(start < end) {
			//compute the profile
			int[] load = new int[end - start];
			for (int tidx = 0; tidx < n; tidx++) {
				if( isRegular(tuple, tidx)) {
					for(int i = tuple[tidx]; i < tuple[startOffset + tidx]; i++) {
						load[i - start] += tuple[taskIntVarOffset + tidx];
					}
				}
			}
			//check profile
			for (int aLoad : load) {
				if ( aLoad > tuple[indexCapacity] ||
						(aLoad != 0 && aLoad < tuple[indexConsumption]) ) {
					return false;
				}
			}
		}
		return true;
	}

//	@Override
//	public boolean isSatisfied() {
//		int start = Integer.MAX_VALUE, end = Integer.MIN_VALUE;
//		for ( IRTask crt : rtasks) {
//			if(crt.isRegular()) {
//				final TaskVar t = crt.getTaskVar();
//				start = Math.min(start,  t.start().getVal());
//				end = Math.max(end,  t.end().getVal());
//			}
//		}
//
//		int[] load = new int[end - start];
//		for (IRTask t : rtasks) {
//			if(t.isRegular()) {
//				for(int i = t.getTaskVar().start().getVal(); i < t.getTaskVar().end().getVal(); i++) {
//					load[i - start] += t.getHeight().getVal();
//				}
//			}
//		}
//		for (int aLoad : load) {
//			if (aLoad > this.getCapacity().getVal()) {
//				return false;
//			}
//		}
//		return true;
//	}


	@Override
	public Boolean isEntailed() {
		throw new UnsupportedOperationException("isEntailed not yet implemented on choco.global.scheduling.Cumulative");
	}


}
