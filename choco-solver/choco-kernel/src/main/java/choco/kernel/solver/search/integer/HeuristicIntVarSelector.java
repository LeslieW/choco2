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

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.List;


/**
 * A class the selects the variables which minimizes a heuristic
 *  (such classes support ties)
 */
public abstract class HeuristicIntVarSelector extends AbstractIntVarSelector implements TiedIntVarSelector {

	public HeuristicIntVarSelector(Solver solver) {
		super(solver);
	}
		
	public HeuristicIntVarSelector(Solver solver, IntDomainVar[] vars) {
		super(solver, vars);
	}
	/**
	 * @param vars the set of vars among which the variable is returned
	 * @return the first variable minimizing a given heuristic
	 */
	public abstract IntDomainVar getMinVar(List<IntDomainVar> vars);

	/**
	 * @param vars the set of vars among which the variable is returned
	 * @return the first variable minimizing a given heuristic
	 */
	public abstract IntDomainVar getMinVar(IntDomainVar[] vars);

	
	public IntDomainVar selectIntVar() {
		return getMinVar(vars);
	}

	public IntDomainVar getMinVar(AbstractIntSConstraint c) {
		IntDomainVar[] vars = new IntDomainVar[c.getNbVars()];
		for(int i = 0; i < c.getNbVars(); i++) {
			vars[i] = c.getVar(i);
		}
		return getMinVar(vars);
	}

	public abstract List<IntDomainVar> getAllMinVars(IntDomainVar[] vars);

	public abstract List<IntDomainVar> getAllMinVars(AbstractIntSConstraint c);



	public List<IntDomainVar> selectTiedIntVars() {
		return getAllMinVars(vars);
	}

}
