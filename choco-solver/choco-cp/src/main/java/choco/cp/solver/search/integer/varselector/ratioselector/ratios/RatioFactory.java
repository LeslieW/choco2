/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
 *         |  �(..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.search.integer.varselector.ratioselector.ratios;

import choco.cp.solver.constraints.global.scheduling.precedence.ITemporalSRelation;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.degree.DomDegRatio;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.degree.DomDynDegRatio;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.degree.DomWDegRatio;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.degree.IncDomWDegRatio;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.ITemporalRatio;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.preserved.IncPreservedWDegRatio;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.preserved.MaxPreservedRatio;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.preserved.MinPreservedRatio;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.preserved.PreservedWDegRatio;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.slack.IncSlackWDegRatio;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.slack.SlackWDegRatio;
import choco.kernel.solver.variables.integer.IntDomainVar;

public final class RatioFactory {

	private RatioFactory() {
		super();
	}

	public static SimpleRatio[] createDefaultRatio(int[] dividends, int[] divisors) {
		final int n = dividends.length;
		if( n != divisors.length) throw new IllegalArgumentException("the sizes are different.");
		final SimpleRatio[] ratios = new SimpleRatio[n];
		for (int i = 0; i < n; i++) {
			ratios[i] = new SimpleRatio(dividends[i], divisors[i]);
		}
		return ratios;
	}

	public static IntRatio[] createDomDegRatio(IntDomainVar[] vars) {
		final int n = vars.length;
		final IntRatio[] ratios = new IntRatio[n];
		for (int i = 0; i < n; i++) {
			ratios[i] = new DomDegRatio(vars[i]);
		}
		return ratios;
	}

	public static IntRatio[] createDomDynDegRatio(IntDomainVar[] vars) {
		final int n = vars.length;
		final IntRatio[] ratios = new IntRatio[n];
		for (int i = 0; i < n; i++) {
			ratios[i] = new DomDynDegRatio(vars[i]);
		}
		return ratios;
	}

	public static IntRatio[] createDomWDegRatio(IntDomainVar[] vars, boolean incremental) {
		final int n = vars.length;
		final IntRatio[] ratios = new IntRatio[n];
		if(incremental) {
			for (int i = 0; i < n; i++) {
				ratios[i] = new IncDomWDegRatio(vars[i]);
			}	
		}else {
			for (int i = 0; i < n; i++) {
				ratios[i] = new DomWDegRatio(vars[i]);
			}
		}

		return ratios;
	}


	public static ITemporalRatio[] createSlackWDegRatio(ITemporalSRelation[] precedences, boolean incremental) {
		final int n = precedences.length;
		final ITemporalRatio[] ratios = new ITemporalRatio[n];
		if(incremental) {
			for (int i = 0; i < n; i++) {
				ratios[i] = new IncSlackWDegRatio(precedences[i]);
			}	
		}else {
			for (int i = 0; i < n; i++) {
				ratios[i] = new SlackWDegRatio(precedences[i]);
			}
		}
		return ratios;
	}

	public static ITemporalRatio[] createPreservedWDegRatio(ITemporalSRelation[] precedences,boolean incremental) {
		final int n = precedences.length;
		final ITemporalRatio[] ratios = new ITemporalRatio[n];
		if(incremental) {
			for (int i = 0; i < n; i++) {
				ratios[i] = new IncPreservedWDegRatio(precedences[i]);
			}	
		}else {
			for (int i = 0; i < n; i++) {
				ratios[i] = new PreservedWDegRatio(precedences[i]);
			}
		}
		return ratios;
	}

	public static MaxPreservedRatio[] createMaxPreservedRatio(ITemporalSRelation[] precedences) {
		final int n = precedences.length;
		final MaxPreservedRatio[] ratios = new MaxPreservedRatio[n];
		for (int i = 0; i < n; i++) {
			ratios[i] = new MaxPreservedRatio(precedences[i]);
		}
		return ratios;
	}

	public static MinPreservedRatio[] createMinPreservedRatio(ITemporalSRelation[] precedences) {
		final int n = precedences.length;
		final MinPreservedRatio[] ratios = new MinPreservedRatio[n];
		for (int i = 0; i < n; i++) {
			ratios[i] = new MinPreservedRatio(precedences[i]);
		}
		return ratios;
	}

}
