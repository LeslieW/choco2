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

import java.util.List;


/**
 * A class the selects the variables which minimizes a heuristic
 *  (such classes support ties)
 */
public abstract class HeuristicIntVarSelector extends AbstractIntVarSelector {
  public HeuristicIntVarSelector(Solver solver) {
    this.solver = solver;
  }
  /**
   * @param vars the set of vars among which the variable is returned
   * @return the first variable minimizing a given heuristic
   */
  public abstract IntDomainVar getMinVar(List<IntDomainVar> vars) throws ContradictionException;

  /**
   * @param vars the set of vars among which the variable is returned
   * @return the first variable minimizing a given heuristic
   */
  public abstract IntDomainVar getMinVar(IntDomainVar[] vars) throws ContradictionException;

  /**
    * @param s the model
    * @return the first variable minimizing a given heuristic among all variables of the model
    */
  public abstract IntDomainVar getMinVar(Solver s) throws ContradictionException;


  public IntDomainVar selectIntVar() throws ContradictionException {
    if (null != vars)
      return getMinVar(vars);
    else
      return getMinVar(solver);
  }

  public IntDomainVar getMinVar(IntSConstraint c) throws ContradictionException {
    IntDomainVar[] vars = new IntDomainVar[c.getNbVars()];
    for(int i = 0; i < c.getNbVars(); i++) {
      vars[i] = c.getIntVar(i);
    }
    return getMinVar(vars);
  }

  public abstract List<IntDomainVar> getAllMinVars(Solver s) throws ContradictionException;

  public abstract List<IntDomainVar> getAllMinVars(IntDomainVar[] vars) throws ContradictionException;

  public abstract List<IntDomainVar> getAllMinVars(IntSConstraint c) throws ContradictionException;



  public List<IntDomainVar> selectTiedIntVars() throws ContradictionException {
      if (null != vars)
          return getAllMinVars(vars);
      else
          return getAllMinVars(solver);
  }

}
