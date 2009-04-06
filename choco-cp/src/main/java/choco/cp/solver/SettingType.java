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



public enum SettingType {

	
	//////////////////////// PACK ////////////////////////
	/**
	 * more filtering rules (recommended)
	 */
	ADDITIONAL_RULES("additional rules","AR", "cp:pack:ar", 1<<0),
	/**
	 * feasibility test based on a dynamic lower bound
	 */
	DYNAMIC_LB("dynamic lower bound","dLB", "cp:pack:dlb", 1<< 1),
	/**
	 * <b>dominance rule:</b> Fill a bin when an item fit into pertfectly. equal-sized items and bins must be equivalent
	 */
	FILL_BIN("fill bins","FB", "cp:pack:fill", 1<< 2),

	//////////////////////// CUMULATIVE ////////////////////////

	TASK_INTERVAL("task interval", "TI","cp:cumul:ti", 1<< 3),

	TASK_INTERVAL_SLOW("task interval", "sTI","cp:cumul:sti", 1<< 4),
	/**
	 * Vilim theta lambda tree +
	 * lazy computation of the inner maximization of the edge finding rule of
	 * Van hentenrick and Mercier
	 */
	VILIM_CEF_ALGO("cumulative edge finding", "cEF","cp:cumul:cef", 1<< 5),
	/**
	 *  Simple n^2 \times k algorithm (lazy for R) (CalcEF in the paper of Van Hentenrick)
	 */
	VHM_CEF_ALGO_N2K("cumulative edge finding", "scEF","cp:cumul:scef", 1<< 6),
	
	/**
	 * Automatic extraction of a disjunctive constraint (if any) from a cumulative constraint
	 */
	EXTRACT_DISJ("automatic disjunctive extraction","ADE","cp:cumul:ade", 1<< 7),
	
	//////////////////////// DISJUNCTIVE ////////////////////////
	/**
	 * Overload checking rule ( O(n*log(n)), Vilim), also known as task interval.
	 */
	OVERLOAD_CHECKING("overload checking","OC","cp:unary:oc", 1<< 8),
	/**
	 * NotFirst/NotLast rule ( O(n*log(n)), Vilim). recommended.
	 */
	NF_NL("not first/not last","NFNL","cp:unary:nfnl", 1<< 9),
	/**
	 * Detectable Precedence rule ( O(n*log(n)), Vilim)
	 *
	 */
	DETECTABLE_PRECEDENCE("detectable precedences","DP","cp:unary:dp", 1<< 10),
	/**
	 * disjunctive Edge Finding rule ( O(n*log(n)), Vilim). recommended
	 */
	EDGE_FINDING_D("disjunctive edge finding", "dEF","cp:unary:ef", 1<< 11),
	

	/**
	 * use filtering algorithm proposed by Vilim. nested loop, each rule is applied until it reach it fixpoint.
	 */
	DEFAULT_FILTERING("Default filtering algorithm", "dF","cp:unary:df", 1<< 12),

	/**
	 * use filtering algorithm proposed by Vilim. nested loop, each rule is applied until it reach it fixpoint.
	 */
	VILIM_FILTERING("Vilim filtering algorithm", "vF","cp:unary:vf", 1<< 13),

	/**
	 * use filtering algorithm proposed by Vilim. nested loop, each rule is applied until it reach it fixpoint.
	 */
	SINGLE_RULE_FILTERING("A single filtering rule (Debug only)", "srF","cp:unary:srf", 1<< 14),
	
	/**
	 * use Gueret-Prins forbidden intervals techniques.
	 */
	FORBIDDEN_INTERVALS("Forbidden intervals","FI","cp:unary:fi",1 << 15),

	//////////////////////// PRECEDENCE ////////////////////////

	/**
	 * pert constraint settings. computes shortest path of the entire PERT graph instead of the subgraph touched by events.
	 */
	NOT_INCREMENTAL_PERT("basic pert propagation","NIP","cp:pert:noincr", 1<< 16);


		
	private final String optionName;

	private final String name;

	private final String label;
	
	private final long mask;
	
	SettingType(String name, String label, String optionName, int mask) {
		this.name=name;
		this.label=label;
		this.optionName=optionName;
		this.mask = mask;
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

}
