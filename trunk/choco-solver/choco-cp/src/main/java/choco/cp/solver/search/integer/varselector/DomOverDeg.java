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

import choco.cp.solver.search.BranchingFactory;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.integer.DoubleHeuristicIntVarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * A heuristic selecting the {@link choco.cp.solver.variables.integer.IntDomainVarImpl} with smallest ration (domainSize / degree)
 * (the degree of a variable is the number of constraints linked to it)
 * @deprecated @see {@link BranchingFactory}
 */
@Deprecated
public class DomOverDeg extends DoubleHeuristicIntVarSelector {
  public DomOverDeg(Solver solver) {
    super(solver);

  }

  public DomOverDeg(Solver solver, IntDomainVar[] vs) {
    super(solver, vs);
  }

  public double getHeuristic(IntDomainVar v) {
    int dsize = v.getDomainSize();
    int deg = v.getNbConstraints();
    if (deg == 0)
      return Double.POSITIVE_INFINITY;
    else
      return (double) dsize / (double) deg;
  }
}
