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

public abstract class CouplesTest extends ConsistencyRelation implements BinRelation {

  /**
   * the default constructor build a relation in feasability
   */
  protected CouplesTest() {
    feasible = true;
  }

  protected CouplesTest(boolean feasible) {
    this.feasible = feasible;
  }

  /**
   * check if the couple (x,y) is consistent according
   * to the feasability of the relation and the checkCouple method.
   * checkCouple have to be overriden by any concrete CouplesTest
   * relation.
   */
  public boolean isConsistent(int x, int y) {
    return checkCouple(x, y) == feasible;
  }

  /**
   * @return the opposite relation
   */
  public ConsistencyRelation getOpposite() {
    CouplesTest ct = null;
    try {
      ct = (CouplesTest) this.clone();
      ct.feasible = !feasible;
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return (ConsistencyRelation) ct;
  }
}
