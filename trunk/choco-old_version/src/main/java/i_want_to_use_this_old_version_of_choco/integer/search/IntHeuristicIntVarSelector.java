// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;

import java.util.ArrayList;
import java.util.List;

/**
 * A class the selects the variables which minimizes a heuristic
 *  (such classes support ties)
 */
public abstract class IntHeuristicIntVarSelector extends HeuristicIntVarSelector {
  public IntHeuristicIntVarSelector(AbstractProblem problem) {
    super(problem);
  }

  /**
   * the heuristic that is minimized in order to find the best IntVar
   */
  public abstract int getHeuristic(IntDomainVar v) throws ContradictionException;

  public int getHeuristic(IntConstraint c, int i) throws ContradictionException {
    return getHeuristic(c.getIntVar(i));
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
   * @param p the problem
   * @return the first variable minimizing a given heuristic among all variables of the problem
   */
  public IntDomainVar getMinVar(AbstractProblem p) throws ContradictionException {
    int minValue = IStateInt.MAXINT;
    IntDomainVar v0 = null;
    int n = p.getNbIntVars();
    for (int i = 0; i < n; i++) {
      IntDomainVar v = (IntDomainVar) p.getIntVar(i);
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

  public IntDomainVar getMinVar(IntConstraint c) throws ContradictionException {
    double minValue = Double.POSITIVE_INFINITY;
    IntDomainVar v0 = null;
    for (int i=0; i<c.getNbVars(); i++) {
      IntDomainVar v = c.getIntVar(i);
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


  public List<IntDomainVar> getAllMinVars(AbstractProblem p) throws ContradictionException {
    List<IntDomainVar> res = new ArrayList<IntDomainVar>();
    int minValue = IStateInt.MAXINT;
    int n = p.getNbIntVars();
    for (int i = 0; i < n; i++) {
      IntDomainVar v = (IntDomainVar) p.getIntVar(i);
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

   public List<IntDomainVar> getAllMinVars(IntConstraint c) throws ContradictionException {
    List<IntDomainVar> res = new ArrayList<IntDomainVar>();
    int minValue = IStateInt.MAXINT;
    for (int i = 0; i < c.getNbVars(); i++) {
      IntDomainVar v = c.getIntVar(i);
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
