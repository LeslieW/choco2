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
package choco.kernel.solver.constraints.integer.extension;

import choco.kernel.common.logging.ChocoLogging;

import java.util.logging.Logger;

public abstract class ConsistencyRelation implements Cloneable {

    protected final static Logger LOGGER = ChocoLogging.getEngineLogger();

  protected boolean feasible;

  /**
   * currentElement if the relation is defined with feasible tuples or
   * infeasible one.
   */
  public boolean isDefinedByFeasability() {
    return feasible;
  }

  /**
   * inverse the feasability of the relation
   */
  public void switchToOppositeRelation() {
    feasible = !feasible;
  }

  /**
   * return the opposite relation of itself
   */
  public abstract ConsistencyRelation getOpposite();

}
