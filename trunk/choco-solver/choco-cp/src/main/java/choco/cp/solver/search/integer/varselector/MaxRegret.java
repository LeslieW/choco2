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

import choco.kernel.solver.Solver;
import choco.kernel.solver.search.integer.IntHeuristicIntVarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class MaxRegret extends IntHeuristicIntVarSelector {
  public MaxRegret(Solver solver) {
    super(solver);
  }

  public MaxRegret(Solver solver, IntDomainVar[] vs) {
    super(solver, vs);
  }

    /**
     * Compute the difference between the two smallest values in domain of {@code v}.
     * The goal is to choose the variable with the largest difference.
     * @param v variable
     * @return difference between the two smallest values of {@code v}.
     */
  public int getHeuristic(IntDomainVar v) {
      int val = v.getInf();
      val -= v.getDomain().getNextValue(val);
      return val;
  }
}