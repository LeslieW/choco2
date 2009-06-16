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

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.propagation.PropagationEngineListener;
import choco.kernel.solver.search.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.Limit;

/**
 * Limit counting the fail number
 */
public class FailLimit extends AbstractGlobalSearchLimit implements PropagationEngineListener {
  public FailLimit(AbstractGlobalSearchStrategy theStrategy, int theLimit) {
    super(theStrategy, theLimit, Limit.FAIL);
    theStrategy.getSolver().getPropagationEngine().addPropagationEngineListener(this);
  }

    /**
     * Define action to do just before a deletion.
     */
    @Override
    public void safeDelete() {
        strategy.getSolver().getPropagationEngine().removePropagationEngineListener(this);
    }

  public boolean newNode(AbstractGlobalSearchStrategy strategy) {
    return ((nb + nbTot) < nbMax);
  }

  public boolean endNode(AbstractGlobalSearchStrategy strategy) {
    return ((nb + nbTot) < nbMax);
  }

  public void contradictionOccured(ContradictionException e) {
    nb++;
  }
}
