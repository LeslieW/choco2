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
package choco.kernel.solver.search;

import choco.IPretty;

/**
 * The interface of objects limiting the global search exploration
 */
public interface GlobalSearchLimit extends IPretty {
  /**
   * resets the limit (the counter run from now on)
   *
   * @param first true for the very first initialization, false for subsequent ones
   */
  public void reset(boolean first);

  /**
   * notify the limit object whenever a new node is created in the search tree
   *
   * @param strategy the controller of the search exploration, managing the limit
   * @return true if the limit accepts the creation of the new node, false otherwise
   */
  public boolean newNode(AbstractGlobalSearchStrategy strategy);

  /**
   * notify the limit object whenever the search closes a node in the search tree
   *
   * @param strategy the controller of the search exploration, managing the limit
   * @return true if the limit accepts the death of the new node, false otherwise
   */
  public boolean endNode(AbstractGlobalSearchStrategy strategy);

}
