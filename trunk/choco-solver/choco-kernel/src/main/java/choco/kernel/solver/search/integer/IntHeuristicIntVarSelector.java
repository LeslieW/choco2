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

import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.ArrayList;
import java.util.List;

/**
 * A class the selects the variables which minimizes a heuristic
 *  (such classes support ties)
 */
public abstract class IntHeuristicIntVarSelector extends HeuristicIntVarSelector {
  public IntHeuristicIntVarSelector(Solver solver) {
    super(solver);
  }

  /**
   * the heuristic that is minimized in order to find the best IntVar
   */
  public abstract int getHeuristic(IntDomainVar v) throws ContradictionException;

  public int getHeuristic(AbstractIntSConstraint c, int i) throws ContradictionException {
    return getHeuristic(c.getVar(i));
  }

  /**
   * @param vars the set of vars among which the variable is returned
   * @return the first variable minimizing a given heuristic
   */
  public IntDomainVar getMinVar(List<IntDomainVar> vars) throws ContradictionException {
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
  public IntDomainVar getMinVar(IntDomainVar[] vars) throws ContradictionException {
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
   * @param solver the model
   * @return the first variable minimizing a given heuristic among all variables of the model
   */
  public IntDomainVar getMinVar(Solver solver) throws ContradictionException {
    int minValue = IStateInt.MAXINT;
    IntDomainVar v0 = null;
    int n = solver.getNbIntVars();
    for (int i = 0; i < n; i++) {
      IntDomainVar v = (IntDomainVar) solver.getIntVar(i);
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

  public IntDomainVar getMinVar(AbstractIntSConstraint c) throws ContradictionException {
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


  public List<IntDomainVar> getAllMinVars(Solver solver) throws ContradictionException {
    List<IntDomainVar> res = new ArrayList<IntDomainVar>();
    int minValue = IStateInt.MAXINT;
    int n = solver.getNbIntVars();
    for (int i = 0; i < n; i++) {
      IntDomainVar v = (IntDomainVar) solver.getIntVar(i);
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

  public List<IntDomainVar> getAllMinVars(IntDomainVar[] vars) throws ContradictionException {
    List<IntDomainVar> res = new ArrayList<IntDomainVar>();
    int minValue = IStateInt.MAXINT;
    int n = solver.getNbIntVars();
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

   public List<IntDomainVar> getAllMinVars(AbstractIntSConstraint c) throws ContradictionException {
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
