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

import choco.kernel.solver.search.integer.AbstractIntVarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.memory.IStateInt;

/**
 * A variable selector selecting the first non instantiated variable according to a given static order
 */
public class StaticVarOrder extends AbstractIntVarSelector {

  IStateInt last;

  public StaticVarOrder(IntDomainVar[] vars) {
    this.last = vars[0].getSolver().getEnvironment().makeInt(0);
    this.vars = vars;
  }

  public IntDomainVar selectIntVar() {
    //<hca> it starts at last.get() and not last.get() +1 to be
    //robust to restart search loop
    for (int i = last.get(); i < vars.length; i++) {
      if (!vars[i].isInstantiated()) {
          last.set(i);
          return vars[i];

      }
    }
    return null;
  }
}
