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
package choco.cp.solver.search.integer.varselector;

import java.util.List;

import choco.kernel.solver.branch.IntBranching;
import choco.kernel.solver.search.integer.HeuristicIntVarSelector;
import choco.kernel.solver.search.integer.IntVarSelector;
import choco.kernel.solver.search.integer.TiedIntVarSelector;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * A class that applies two heuristics lexicographically for selecting a variable:
 *   a first heuristic is applied finding the best constraint
 *   ties are broken with the second heuristic
 */
public final class LexIntVarSelector implements IntVarSelector {
	
	TiedIntVarSelector h1;
	
	HeuristicIntVarSelector h2;

	public LexIntVarSelector(TiedIntVarSelector h1, HeuristicIntVarSelector h2) {
		this.h1 = h1;
		this.h2 = h2;
	}

	public IntDomainVar selectIntVar() {
		List<IntDomainVar> ties = h1.selectTiedIntVars();
		switch (ties.size()) {
		case 0: return null;
		case 1: return ties.get(0);
		default: return h2.getMinVar(ties);
		}
	}

	@Override
	public IntBranching getBranching() {
		return null;
	}

	@Override
	public Var selectVar() {
		return selectIntVar();
	}

}
