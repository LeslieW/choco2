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
package choco.kernel.solver.search.integer;

import choco.kernel.common.util.tools.VariableUtils;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.AbstractSearchHeuristic;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;

public abstract class AbstractIntVarSelector extends AbstractSearchHeuristic implements IntVarSelector {

	/**
	 * a specific array of IntVars from which the object seeks the one with smallest domain
	 */
	protected final IntDomainVar[] vars;

	public AbstractIntVarSelector(Solver solver) {
		this(solver, VariableUtils.getIntVars(solver));
	}
	
	public AbstractIntVarSelector(Solver solver, IntDomainVar[] vars) {
		super(solver);
		this.vars = vars;
	}

	
	/**
	 * the IVarSelector can be asked to return a variable
	 *
	 * @return a variable on whose domain an alternative can be set (such as a non instantiated search variable)
	 */
	public final Var selectVar() {
		return  selectIntVar();
	}

	/**
	 * Get decision vars
	 * @return decision vars
	 */
	public IntDomainVar[] getVars() {
		return vars;
	}
	
	@Deprecated
	public IntDomainVar[] setVars(IntDomainVar[] vars) {
		//FIXME remove
		throw new IllegalArgumentException("vars are now final");
	}
}
