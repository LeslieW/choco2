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

public class TuplesTable extends ConsistencyRelation implements LargeRelation {

  /**
   * the number of dimensions of the considered tuples
   */
  protected int n;
  /**
   * The consistency matrix
   */
  protected BitSet table;

  /**
   * offset (lower bound) of each variable
   */
  protected int[] offsets;

  /**
   * domain size of each variable
   */
  protected int[] sizes;

  /**
   * in order to speed up the computation of the index of a tuple
   * in the table, blocks[i] stores the product of the size of variables j with j < i.
   */
  protected int[] blocks;

  public TuplesTable(int n) {
    this.n = n;
  }

  public TuplesTable(boolean feas, int[] offsetTable, int[] sizesTable) {
    offsets = offsetTable;
    sizes = sizesTable;
    n = offsetTable.length;
    feasible = feas;
    int totalSize = 1;
    blocks = new int[n];
    for (int i = 0; i < n; i++) {
      blocks[i] = totalSize;
      totalSize *= sizes[i];
    }

    if(totalSize/8 > 50*1024*1024)
        LOGGER.warning("Tuples requiered over 50Mo of memory...");


    if (totalSize < 0)
      table = new BitSet();
    else
      table = new BitSet(totalSize);
  }

  public boolean checkTuple(int[] tuple) {
    int address = 0;
    for (int i = (n - 1); i >= 0; i--) {
       if((tuple[i]<offsets[i])|| (tuple[i]>(offsets[i]+sizes[i]-1))){
           return false;
       }
      address += (tuple[i] - offsets[i]) * blocks[i];
    }
    return table.get(address);
  }

  public boolean isConsistent(int[] tuple) {
    return checkTuple(tuple) == feasible;
  }

  public void setTuple(int[] tuple) {
    int address = 0;
    for (int i = (n - 1); i >= 0; i--) {
      address += (tuple[i] - offsets[i]) * blocks[i];
    }
    table.set(address);
  }

  /**
   * @return the opposite relation
   */
  public ConsistencyRelation getOpposite() {
    TuplesTable t = new TuplesTable(this.n);
    t.feasible = !feasible;
    t.offsets = offsets;
    t.sizes = sizes;
    t.blocks = blocks;
    t.table = table;
    return t;
  }

}
