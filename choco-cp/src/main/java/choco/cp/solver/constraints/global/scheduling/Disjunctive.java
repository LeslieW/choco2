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


import choco.cp.solver.SettingType;
import static choco.cp.solver.SettingType.*;
import choco.kernel.common.util.comparator.IPermutation;
import choco.kernel.common.util.tools.PermutationUtils;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

import java.util.Arrays;


/**
 * @author Arnaud Malapert</br> 
 * @since version 2.0.0</br>
 * @version 2.0.3</br>
 */
public class Disjunctive extends AbstractResourceSConstraint {

	public static enum Rule {NONE, NOT_FIRST, NOT_LAST, DP_EST, DP_LCT, EF_EST, EF_LCT}

	protected Rule single=Rule.NONE;

	protected IDisjRules rules;

	private boolean noFixPoint;


	protected Disjunctive(String name, TaskVar[] taskvars,
			IntDomainVar uppBound, IntDomainVar... otherVars) {
		super(name, taskvars, uppBound, otherVars);
	}

	public Disjunctive(String name, TaskVar[] taskvars, IntDomainVar makespan) {
		super(name, taskvars, makespan);
		this.rules = new DisjRules(this.rtasks);
	}

	public final void setSingleRule(final Rule rule) {
		flags.unset(DEFAULT_FILTERING, VILIM_FILTERING);
		flags.set(SINGLE_RULE_FILTERING);
		this.single=rule;
	}

	public final void noSingleRule() {
		this.single=Rule.NONE;
	}


	protected final boolean applySingleRule() throws ContradictionException {
		switch (single) {
		case NOT_FIRST: return rules.notFirst();
		case NOT_LAST: return rules.notLast();
		case DP_EST: return rules.detectablePrecedenceEST();
		case DP_LCT: return rules.detectablePrecedenceLCT();
		case EF_EST: return rules.edgeFindingEST();
		case EF_LCT: return rules.edgeFindingLCT();
		default:
			throw new IllegalArgumentException("no rule activated in Disjunctive constraint");
		}
	}

	//****************************************************************//
	//********* EVENTS - PROPAGATION *********************************//
	//****************************************************************//


	protected void singleRuleFiltering() throws ContradictionException {
		do {
			rules.fireDomainChanged();
			noFixPoint = applySingleRule();
			updateMakespan(rules.getMakespanLB());
		} while(noFixPoint);
	}

	protected boolean hasOverloadChecking() {
		return !flags.contains(EDGE_FINDING_D) && flags.contains(OVERLOAD_CHECKING); 
	}

	protected final void defaultFiltering() throws ContradictionException {
		do {
			noFixPoint=false;
			rules.fireDomainChanged();
			if( flags.contains(EDGE_FINDING_D) ) {
				noFixPoint |= rules.edgeFinding();
			} 
			if(flags.contains(NF_NL)) {
				noFixPoint |= rules.notLast();
				if(flags.contains(DETECTABLE_PRECEDENCE)) {
					noFixPoint |= rules.detectablePrecedenceEST();
					noFixPoint |= rules.notFirst();
					noFixPoint |= rules.detectablePrecedenceLCT();
				} else {
					noFixPoint |= rules.notFirst();
				}
			} else if(flags.contains(DETECTABLE_PRECEDENCE)) {
				noFixPoint |= rules.detectablePrecedenceEST();
				noFixPoint |= rules.detectablePrecedenceLCT();
			}
			if ( hasOverloadChecking() && rules.overloadChecking()) {fail();}
			updateMakespan(rules.getMakespanLB());
		}while(noFixPoint);


	}


	protected final void vilimFiltering() throws ContradictionException {
		boolean noGlobalFixPoint;
		do {
			noGlobalFixPoint=false;
			if( flags.contains(EDGE_FINDING_D)) {
				do {
					rules.fireDomainChanged();
					noFixPoint= rules.edgeFinding();
					updateMakespan(rules.getMakespanLB());
				} while (noFixPoint);
			}
			if(hasOverloadChecking() && rules.overloadChecking()) {fail();}

			if(flags.contains(SettingType.NF_NL)) {
				do {
					rules.fireDomainChanged();
					noFixPoint = rules.notFirstNotLast();
					updateMakespan(rules.getMakespanLB());
					noGlobalFixPoint |= noFixPoint;
				} while (noFixPoint);
			}

			if(flags.contains(SettingType.DETECTABLE_PRECEDENCE)) {
				do {
					rules.fireDomainChanged();
					noFixPoint = rules.detectablePrecedence();
					updateMakespan(rules.getMakespanLB());
					noGlobalFixPoint |= noFixPoint;
				} while (noFixPoint);
			}

		} while (noGlobalFixPoint);
	}

	/**
	 * Propagate.
	 * called for any bound events.
	 * @throws ContradictionException the contradiction exception
	 *
	 * @see choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint#propagate()
	 */
	@Override
	public void propagate() throws ContradictionException {
		//Solver.flushLogs();
		if(rules.isActive()) {
			if(flags.contains(DEFAULT_FILTERING)) {
				defaultFiltering();
			}else if(flags.contains(VILIM_FILTERING)) {
				vilimFiltering();
			}else if(flags.contains(SINGLE_RULE_FILTERING)) {
				singleRuleFiltering();
			} else {
				throw new SolverException("No filtering algorithm ?");
			}
		}
	}

	@Override
	public boolean isSatisfied(int[] tuple) {
		IPermutation permutation = PermutationUtils.getSortingPermuation(Arrays.copyOf(tuple, getNbTasks()));
		int last = 0;
		for (int i = 0; i < getNbTasks(); i++) {
			int idx = permutation.getPermutationIndex(i);
			if( tuple[idx] < last || //overlap with the previous task
					tuple[idx] + tuple[idx + 2 * getNbTasks()] != tuple[idx + getNbTasks()] //s_i + p_i != e_i !
			) {
				return false;
			}
			last = tuple[idx];
		}
		if(last > tuple[tuple.length - 2]) {return false;}
		return true;
	}


}











