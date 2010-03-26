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

import java.util.ArrayList;
import java.util.List;

import choco.kernel.memory.IStateInt;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * A class the selects the variables which minimizes a heuristic
 *  (such classes support ties)
 */
public abstract class IntHeuristicIntVarSelector extends HeuristicIntVarSelector {

	public IntHeuristicIntVarSelector(Solver solver) {
		super(solver);
	}

	public IntHeuristicIntVarSelector(Solver solver, IntDomainVar[] vars) {
		super(solver, vars);
	}



	/**
	 * the heuristic that is minimized in order to find the best IntVar
	 */
	public abstract int getHeuristic(IntDomainVar v);

	public final int getHeuristic(AbstractIntSConstraint c, int i) {
		return getHeuristic(c.getVar(i));
	}

	/**
	 * @param vars the set of vars among which the variable is returned
	 * @return the first variable minimizing a given heuristic
	 */
	@Override
	public final IntDomainVar getMinVar(List<IntDomainVar> vars) {
		int minValue = IStateInt.MAXINT;
		IntDomainVar v0 = null;
		for (IntDomainVar v:vars) {
			if (!v.isInstantiated()) {
				int val = getHeuristic(v);
				if (val < minValue)  {
					minValue = val;
					v0 = v;
				}
			}
		}
		return v0;
	}
	/**
	 * @param vars the set of vars among which the variable is returned
	 * @return the first variable minimizing a given heuristic
	 */
	@Override
	public final IntDomainVar getMinVar(IntDomainVar[] vars) {
		int minValue = IStateInt.MAXINT;
		IntDomainVar v0 = null;
		for (IntDomainVar v:vars) {
			if (!v.isInstantiated()) {
				int val = getHeuristic(v);
				if (val < minValue)  {
					minValue = val;
					v0 = v;
				}
			}
		}
		return v0;
	}

	

	@Override
	public IntDomainVar getMinVar(AbstractIntSConstraint c) {
		double minValue = Double.POSITIVE_INFINITY;
		IntDomainVar v0 = null;
		for (int i=0; i<c.getNbVars(); i++) {
			IntDomainVar v = c.getVar(i);
			if (!v.isInstantiated()) {
				double val = getHeuristic(c,i);
				if (val < minValue)  {
					minValue = val;
					v0 = v;
				}
			}
		}
		return v0;
	}



	@Override
	public List<IntDomainVar> getAllMinVars(IntDomainVar[] vars) {
		List<IntDomainVar> res = new ArrayList<IntDomainVar>();
		int minValue = IStateInt.MAXINT;
		for (IntDomainVar v:vars) {
			if (!v.isInstantiated()) {
				int val = getHeuristic(v);
				if (val < minValue)  {
					res.clear();
					res.add(v);
					minValue = val;
				} else if (val == minValue) {
					res.add(v);
				}
			}
		}
		return res;
	}

	@Override
	public final List<IntDomainVar> getAllMinVars(AbstractIntSConstraint c) {
		List<IntDomainVar> res = new ArrayList<IntDomainVar>();
		int minValue = IStateInt.MAXINT;
		for (int i = 0; i < c.getNbVars(); i++) {
			IntDomainVar v = c.getVar(i);
			if (!v.isInstantiated()) {
				int val = getHeuristic(v);
				if (val < minValue)  {
					res.clear();
					res.add(v);
					minValue = val;
				} else if (val == minValue) {
					res.add(v);
				}
			}
		}
		return res;
	}
}
