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
package choco.kernel.memory.structure;

import choco.kernel.common.util.IntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateBitSet;

import java.util.BitSet;


/**
 * A class implementing a bitset with two kind of storage:
 * standard static storage in an array, and backtrackable storage.
 * By convention, values with small indices (0 .. 999999) are statically managed
 * as if they were in a standard bitset.
 * And values with large indices (1000000 ... ) are "stored" in a backtrackable
 * manner, as if they were in a StoredIntBitSet
 */
public class PartiallyStoredBitSet {

  public static final int STORED_OFFSET = 1000000;

  protected BitSet staticInts;
  protected IStateBitSet storedInts;

  public PartiallyStoredBitSet(IEnvironment env) {
    staticInts = new BitSet();
    storedInts = env.makeBitSet(16);
  }

    public void staticSet(int o){
        staticInts.set(o);
    }

    public void set(int o){
        storedInts.set(o);
    }


    public void staticClear(int o){
        staticInts.clear(o);
    }

    public void clear(int o){
        storedInts.clear(o);
    }


    public IntIterator getIndexIterator() {
    return new IntIterator() {
      int idxSta = staticInts.nextSetBit(0);
      int idxSto = storedInts.nextSetBit(0);

      public boolean hasNext() {
          return (idxSta!=-1 || idxSto!=-1);
      }

      public int next() {
          if(idxSta!=-1){
              int i = idxSta;
              idxSta = staticInts.nextSetBit(i+1);
              return i;
          }else{
              int i = idxSto;
              idxSto = storedInts.nextSetBit(i+1);
              return i;
          }
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
}