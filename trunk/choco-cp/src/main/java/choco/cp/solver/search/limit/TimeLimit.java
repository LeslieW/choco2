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
package choco.cp.solver.search.limit;

import choco.kernel.solver.search.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.Limit;

public class TimeLimit extends AbstractGlobalSearchLimit {
  protected long starth = Long.MIN_VALUE;

  public TimeLimit(AbstractGlobalSearchStrategy theStrategy, int theLimit) {
    super(theStrategy, theLimit, Limit.TIME);
  }

  @Override
public void reset(boolean first) {
    long newh = System.currentTimeMillis();
    nb = (int) (newh - starth);
    super.reset(first);
    starth = newh;
  }

  public boolean newNode(AbstractGlobalSearchStrategy strategy) {
    nb = (int) (System.currentTimeMillis() - starth);
    return ((nb + nbTot) < nbMax);
  }

  public boolean endNode(AbstractGlobalSearchStrategy strategy) {
      nb = (int) (System.currentTimeMillis() - starth);
      return ((nb + nbTot) < nbMax);
  }
}
