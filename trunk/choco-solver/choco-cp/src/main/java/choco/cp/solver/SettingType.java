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
package choco.cp.solver;

import choco.cp.CPOptions;


public enum SettingType {

	//////////////////////// PACK ////////////////////////
	/**
	 * more filtering rules (recommended)
	 */
	ADDITIONAL_RULES("additional rules","AR", "cp:pack:ar"),
	/**
	 * feasibility test based on a dynamic lower bound
	 */
	DYNAMIC_LB("dynamic lower bound","dLB", "cp:pack:dlb"),
	/**
	 * <b>dominance rule:</b> Fill a bin when an item fit into pertfectly. equal-sized items and bins must be equivalent
	 */
	FILL_BIN("fill bins","FB", "cp:pack:fill"),

	LAST_BINS_EMPTY("Empty bins are the last ones","LBE", "cp:pack:lbe"),

	//////////////////////// CUMULATIVE ////////////////////////

	TASK_INTERVAL("task interval", "TI","cp:cumul:ti"),

	TASK_INTERVAL_SLOW("task interval", "sTI","cp:cumul:sti"),
	/**
	 * Vilim theta lambda tree +
	 * lazy computation of the inner maximization of the edge finding rule of
	 * Van hentenrick and Mercier
	 */
	VILIM_CEF_ALGO("cumulative edge finding", "cEF","cp:cumul:cef"),
	/**
	 *  Simple n^2 \times k algorithm (lazy for R) (CalcEF in the paper of Van Hentenrick)
	 */
	VHM_CEF_ALGO_N2K("cumulative edge finding", "scEF","cp:cumul:scef"),


	//////////////////////// DISJUNCTIVE ////////////////////////
	/**
	 * Overload checking rule ( O(n*log(n)), Vilim), also known as task interval.
	 */
	OVERLOAD_CHECKING("overload checking","OC","cp:unary:oc"),
	/**
	 * NotFirst/NotLast rule ( O(n*log(n)), Vilim). recommended.
	 */
	NF_NL("not first/not last","NFNL","cp:unary:nfnl"),
	/**
	 * Detectable Precedence rule ( O(n*log(n)), Vilim)
	 *
	 */
	DETECTABLE_PRECEDENCE("detectable precedences","DP","cp:unary:dp"),
	/**
	 * disjunctive Edge Finding rule ( O(n*log(n)), Vilim). recommended
	 */
	EDGE_FINDING_D("disjunctive edge finding", "dEF","cp:unary:ef"),


	/**
	 * use filtering algorithm proposed by Vilim. nested loop, each rule is applied until it reach it fixpoint.
	 */
	DEFAULT_FILTERING("Default filtering algorithm", "dF","cp:unary:df"),

	/**
	 * use filtering algorithm proposed by Vilim. nested loop, each rule is applied until it reach it fixpoint.
	 */
	VILIM_FILTERING("Vilim filtering algorithm", "vF","cp:unary:vf"),

	/**
	 * use filtering algorithm proposed by Vilim. nested loop, each rule is applied until it reach it fixpoint.
	 */
	SINGLE_RULE_FILTERING("A single filtering rule (Debug only)", "srF","cp:unary:srf"),


	//////////////////////// PRECEDENCE ////////////////////////

	/**
	 * pert constraint settings. computes shortest path of the entire PERT graph instead of the subgraph touched by events.
	 */
	NOT_INCREMENTAL_PERT("basic pert propagation","NIP","cp:pert:noincr"),

	//////////////////////// ////////////////////////

	DECOMP("Decomposition Of Global Constraint","DGC", CPOptions.E_DECOMP),
	
	GLOBAL("Global Constraint Only","GCO","cp:global"),
	
	MIXED("Mixed Decomposition and Global Constraint","MDGC","cp:mixed");
	
	private final String optionName;

	private final String name;

	private final String label;

	private final long mask;
	
		
	SettingType(String name, String label, String optionName) {
		this.name=name;
		this.label=label;
		this.optionName=optionName;
		this.mask =  Offset.OFFSET;
		Offset.OFFSET = Offset.OFFSET << 1;
	}

	public final String getOptionName() {
		return optionName;
	}

	public final String getName() {
		return name;
	}

	public final String getLabel() {
		return label;
	}

	public final long getBitMask() {
		return mask;
	}

	private static class Offset
	  {
	    // The static OFFSET from option to mask
	    private static int OFFSET = 1;
	  }
}
