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

import java.util.BitSet;

public class CouplesTable extends ConsistencyRelation implements BinRelation, ExtensionalBinRelation {

  /**
   * matrix of consistency/inconsistency
   */
  protected BitSet table;

  /**
   * first value of x
   */
  protected int offset1;

  /**
   * first value of y
   */
  protected int offset2;

  /**
   * size of the initial domain of x
   */
  protected int n2;


  public CouplesTable() {
  }

  public CouplesTable(boolean feas, int offset1, int offset2, int n1, int n2) {
    this.offset1 = offset1;
    this.offset2 = offset2;
    this.n2 = n2;
    this.table = new BitSet(n1 * n2);
    this.feasible = feas;
  }

  /**
   * compute the opposite relation by "reusing" the table of consistency
   *
   * @return the opposite relation
   */
  public ConsistencyRelation getOpposite() {
    CouplesTable t = new CouplesTable();
    t.feasible = !feasible;
    t.table = table;
    t.offset1 = offset1;
    t.offset2 = offset2;
    t.n2 = n2;
    return t;
  }

  public void setCouple(int x, int y) {
    table.set((x - offset1) * n2 + y - offset2);
  }

  public void setCoupleWithoutOffset(int x, int y) {
    table.set(x * n2 + y);
  }

  public boolean isConsistent(int x, int y) {
    return table.get((x - offset1) * n2 + y - offset2) == feasible;
  }

  public boolean checkCouple(int x, int y) {
    return table.get((x - offset1) * n2 + y - offset2);
  }

}
