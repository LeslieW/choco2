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

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.constraints.integer.AbstractBinIntSConstraint;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

public abstract class CspBinSConstraint extends AbstractBinIntSConstraint {


  protected BinRelation relation;


  protected CspBinSConstraint(IntDomainVar x, IntDomainVar y, BinRelation relation) {
    super(x, y);
    this.relation = relation;
  }

  /**
   * Checks if the constraint is satisfied when the variables are instantiated.
   *
   * @return true if the constraint is satisfied
   */

  public boolean isSatisfied(int[] tuple) {
    return relation.isConsistent(tuple[0], tuple[1]); //table.get((v1.getVal() - offset) * n + (v0.getVal() - offset));
  }

  public BinRelation getRelation() {
    return relation;
  }


  public Boolean isEntailed() {
    boolean always = true;
    DisposableIntIterator itv1 = v0.getDomain().getIterator();
    while (itv1.hasNext()) {
      int nbs = 0;
      int val = itv1.next();
      DisposableIntIterator itv2 = v1.getDomain().getIterator();
      while (itv2.hasNext()) {
        if (relation.isConsistent(val, itv2.next())) nbs += 1;
      }
      if (nbs == 0) {
        always = false;
      } else if (nbs != v1.getDomainSize()) {
        return null;
      }
      itv2.dispose();
    }
    itv1.dispose();
    if (always)
      return Boolean.TRUE;
    else
      return Boolean.FALSE;
  }
}
