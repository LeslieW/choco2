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

public abstract class TuplesTest extends ConsistencyRelation implements LargeRelation {

  /**
   * the default constructor build a relation in feasability
   */
  public TuplesTest() {
    this.feasible = true;
  }

  public TuplesTest(boolean feasible) {
    this.feasible = feasible;
  }

  public boolean isConsistent(int[] tuple) {
    return checkTuple(tuple) == feasible;
  }

  public ConsistencyRelation getOpposite() {
    TuplesTest ct = null;
    try {
      ct = (TuplesTest) this.clone();
      ct.feasible = !feasible;
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return (ConsistencyRelation) ct;
  }

}
