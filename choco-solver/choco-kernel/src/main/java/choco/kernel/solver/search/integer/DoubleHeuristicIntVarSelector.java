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
import choco.kernel.solver.constraints.integer.IntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 23 ao�t 2006
 * Since : Choco 2.0.0
 *
 */
public abstract class DoubleHeuristicIntVarSelector extends
		HeuristicIntVarSelector {
	public DoubleHeuristicIntVarSelector(Solver solver) {
		super(solver);
	}

	/**
	 * the heuristic that is minimized in order to find the best IntVar
	 */
	public abstract double getHeuristic(IntDomainVar v)
			throws ContradictionException;

	public double getHeuristic(IntSConstraint c, int i)
			throws ContradictionException {
		return getHeuristic(c.getIntVar(i));
	}

	/**
	 * @param vars
	 *            the set of vars among which the variable is returned
	 * @return the first variable minimizing a given heuristic
	 */
	public IntDomainVar getMinVar(List<IntDomainVar> vars)
			throws ContradictionException {
		double minValue = Double.POSITIVE_INFINITY;
		IntDomainVar v0 = null;
		for (IntDomainVar v : vars) {
			if (!v.isInstantiated()) {
				double val = getHeuristic(v);
				if (val < minValue) {
					minValue = val;
					v0 = v;
				}
			}
		}
		return v0;
	}

	/**
	 * @param vars
	 *            the set of vars among which the variable is returned
	 * @return the first variable minimizing a given heuristic
	 */
	public IntDomainVar getMinVar(IntDomainVar[] vars)
			throws ContradictionException {
		double minValue = Double.POSITIVE_INFINITY;
		IntDomainVar v0 = null;
		for (IntDomainVar v : vars) {
			if (!v.isInstantiated()) {
				double val = getHeuristic(v);
				if (val < minValue) {
					minValue = val;
					v0 = v;
				}
			}
		}
		return v0;
	}

	/**
	 * @param solver
	 *            the model
	 * @return the first variable minimizing a given heuristic among all
	 *         variables of the model
	 */
	public IntDomainVar getMinVar(Solver solver) throws ContradictionException {
		double minValue = Double.POSITIVE_INFINITY;
		IntDomainVar v0 = null;
		int n = solver.getNbIntVars();
		for (int i = 0; i < n; i++) {
			IntDomainVar v = (IntDomainVar) solver.getIntVar(i);
			if (!v.isInstantiated()) {
				double val = getHeuristic(v);
				if (val < minValue) {
					minValue = val;
					v0 = v;
				}
			}
		}

		return v0;
	}

	public List<IntDomainVar> getAllMinVars(Solver solver)
			throws ContradictionException {
		List<IntDomainVar> res = new ArrayList<IntDomainVar>();
		double minValue = Double.POSITIVE_INFINITY;
		int n = solver.getNbIntVars();
		for (int i = 0; i < n; i++) {
			IntDomainVar v = (IntDomainVar) solver.getIntVar(i);
			if (!v.isInstantiated()) {
				double val = getHeuristic(v);
				if (val < minValue) {
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

    public List<IntDomainVar> getAllMinVars(IntDomainVar[] vars)
			throws ContradictionException {
		List<IntDomainVar> res = new ArrayList<IntDomainVar>();
		double minValue = Double.POSITIVE_INFINITY;
		int n = solver.getNbIntVars();
		for (IntDomainVar v : vars) {
			if (!v.isInstantiated()) {
				double val = getHeuristic(v);
				if (val < minValue) {
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

	public List<IntDomainVar> getAllMinVars(IntSConstraint c)
			throws ContradictionException {
		List<IntDomainVar> res = new ArrayList<IntDomainVar>();
		double minValue = Double.POSITIVE_INFINITY;
		for (int i = 0; i < c.getNbVars(); i++) {
			IntDomainVar v = c.getIntVar(i);
			if (!v.isInstantiated()) {
				double val = getHeuristic(v);
				if (val < minValue) {
					res.clear();
					res.add(v);
					minValue = val;
				} else if (val == minValue) { // <hca> add NaN to avoid bu with
												// alldiff) {
					res.add(v);
				}
			}
		}
		return res;
	}

	public IntDomainVar getMinVar(IntSConstraint c)
			throws ContradictionException {
		double minValue = Double.POSITIVE_INFINITY;
		IntDomainVar v0 = null;
		for (int i = 0; i < c.getNbVars(); i++) {
			IntDomainVar v = c.getIntVar(i);
			if (!v.isInstantiated()) {
				double val = getHeuristic(c, i);
				if (val < minValue) {
					minValue = val;
					v0 = v;
				}
			}
		}
		return v0;
	}

}
