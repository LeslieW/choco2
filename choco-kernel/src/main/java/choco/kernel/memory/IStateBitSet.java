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
package choco.kernel.memory;

import choco.kernel.common.util.IntIterator;

import java.util.BitSet;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: 12 juil. 2007
 * Time: 10:16:08
 */
public interface IStateBitSet {




    /**
   * Number of bits on. Sums the number of on bits in each integer.
     * @return the total number of bits on
     */
    int cardinality();

    /**
     * Size of the bitset
     * @return
     */
    int size();

    /**
   * Puts the specified bit on.
     * @param bitIndex the bit to put on
     */
    void set(int bitIndex);

    /**
   * Puts the specified bit off.
     * @param bitIndex the bit to put off
     */
    void clear(int bitIndex);

  /**
   * Remove all bits;
   */
  void clear();

    void set(int index, boolean value);
    void set(int fromIdex, int toIndex);

    boolean get(int bitIndex);

    /**
   * Returns the index of the first bit that is set to <code>true</code>
     * that occurs on or after the specified starting index. If no such
     * bit exists then -1 is returned.
     * <p/>
     * To iterate over the <code>true</code> bits in a <code>BitSet</code>,
     * use the following loop:
     * <p/>
     * for(int i=bs.nextSetBit(0); i>=0; i=bs.nextSetBit(i+1)) {
     * // operate on index i here
     * }
     *
     * @param fromIndex the index to start checking from (inclusive).
     * @return the index of the next set bit.
     * @throws IndexOutOfBoundsException if the specified index is negative.
     * @since 1.4
     */
    // TODO: write the currentElement file + prevSetBit + nextClearBit
    int nextSetBit(int fromIndex);

    /**
   * Returns the index of the first bit that is set to <code>true</code>
     * that occurs on or before the specified starting index. If no such
     * bit exists then -1 is returned.
     * @param fromIndex the index to start checking from (inclusive).
     * @return the index of the previous set bit.
     * @throws IndexOutOfBoundsException if the specified index is
     * negative or too large
     */
    int prevSetBit(int fromIndex);

    int capacity();

    IStateBitSet copy();

    BitSet copyToBitSet();


    void or (IStateBitSet other);
    void and (IStateBitSet other);
    void xor (IStateBitSet other);
    void andNot(IStateBitSet other);
    boolean intersects(IStateBitSet setI);
    void flip(int bitIndex);
    void flip(int fromIndex, int toIndex);

    boolean isEmpty();

    void ensureCapacity(int bitIndex);

    IntIterator getCycleButIterator(int avoidIndex);

    class EmptyIterator implements IntIterator {
    public boolean hasNext() {
      return false;
    }

    public int next() {
      return 0;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  class CyclicIterator implements IntIterator {
    /**
     * current index of the iteration
     * (the one just returned at the last call to next())
     */
    private int k, nextk;

    /**
     * the index where the iteration started when the iterator was created
     */
    private int endMarker;

    private IStateBitSet bset;

    /**
     * constructor
     */
    public CyclicIterator(final choco.kernel.memory.IStateBitSet bs, final int avoidIndex) {
      bset = bs;
      k = -1;
      nextk = -1;
      endMarker = avoidIndex;
    }

    public boolean hasNext() {
      nextk = bset.nextSetBit(k + 1);
      if (nextk < 0) {
        return false;
      } else if (nextk == endMarker) {
        nextk = bset.nextSetBit(nextk + 1);
        if (nextk < 0) {
          return false;
        } else {
          return true;
        }
      } else {
        return true;
      }
    }

    public int next() {
      k = nextk;
      return k;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
