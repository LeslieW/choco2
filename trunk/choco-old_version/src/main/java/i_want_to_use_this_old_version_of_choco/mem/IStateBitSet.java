package i_want_to_use_this_old_version_of_choco.mem;

import i_want_to_use_this_old_version_of_choco.util.IntIterator;

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
   * Puts the specified bit on.
     * @param bitIndex the bit to put on
     */
    void set(int bitIndex);

    /**
   * Puts the specified bit off.
     * @param bitIndex the bit to put off
     */
    void clear(int bitIndex);

    void set(int index, boolean value);

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
    public CyclicIterator(final IStateBitSet bs, final int avoidIndex) {
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
