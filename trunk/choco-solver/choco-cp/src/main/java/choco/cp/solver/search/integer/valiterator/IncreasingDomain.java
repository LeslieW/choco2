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
package choco.cp.solver.search.integer.valiterator;

import choco.kernel.solver.search.ValIterator;
import choco.kernel.solver.variables.integer.IntDomainVar;

public final class IncreasingDomain implements ValIterator<IntDomainVar> {

  /**
   * testing whether more branches can be considered after branch i, on the alternative associated to variable x
   *
   * @param x the variable under scrutiny
   * @param i the index of the last branch explored
   * @return true if more branches can be expanded after branch i
   */
  public boolean hasNextVal(IntDomainVar x, int i) {
    return (i < x.getSup());
  }

  /**
   * Accessing the index of the first branch for variable x
   *
   * @param x the variable under scrutiny
   * @return the index of the first branch (such as the first value to be assigned to the variable)
   */
  public int getFirstVal(IntDomainVar x) {
    return x.getInf();
  }

  /**
   * generates the index of the next branch after branch i, on the alternative associated to variable x
   *
   * @param x the variable under scrutiny
   * @param i the index of the last branch explored
   * @return the index of the next branch to be expanded after branch i
   */
  public int getNextVal(IntDomainVar x, int i) {
    return x.getNextDomainValue(i);
  }
}
