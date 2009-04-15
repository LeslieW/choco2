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

package choco.kernel.common.util;

import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateIntVector;
import choco.kernel.solver.propagation.VarEvent;

import java.util.logging.Logger;

/**
 * A data structure implementing a subset of a given set, by means of a cyclic
 * chain of pointers (grossly specking, each entry contains the index of the
 * next entry in the cycle). This data structure is convenient for iterating a
 * subset of objets (chain of pointers), starting from any object (cyclic
 * iteration). The data structure is based on backtrackable vectors
 * (@link{StoredIntVector}). It is also robust in the sense that objects can
 * be removed from the cycle while the cycle is being iterated.
 *
 * @deprecated Not used anymore !
 */
public class StoredPointerCycle {
  /**
   * Wildcard: all entries of the vector contain valid indices
   * (i.e. values from 0 to n-1, n being the vector size)
   * or the constant INVALID_INDEX.
   */
  public static final int INVALID_INDEX = -1;

  /**
   * The vectors of entries.
   */
  protected IStateIntVector next;


  /**
   * Builds the data structure with the specified environment.
   * @param env The environment responsible of managing worlds
   */
  public StoredPointerCycle(final IEnvironment env) {
    next = env.makeIntVector();
  }

  /**
   * Compute the size of the data structure.
   * @return the number of elements
   */
  public int size() {
    return next.size();
  }

  /**
   * Tests if a given index is in the cycle or not.
   * @param idx The index to be checked
   * @return true is the index is in the cycle
   */
  public boolean isInCycle(final int idx) {
    int n = next.size();
    int k = (idx == 0) ? n - 1 : idx - 1;
    return ((n != 0) && (next.get(k) == idx));
  }

  /**
   * Adds an entry to the static collection, at a given index.
   * @param idx where the entry should be added
   * @param inCycle Specifies if the new entry is in the cycle
   */
  public void add(final int idx, final boolean inCycle) {   // ex connectEvent
    if (idx >= 1) {
      int j = next.get(idx - 1);
      int k = idx - 1;
      if ((j == -1) && inCycle) {
        next.add(idx);
      } else {
        next.add(j);
      }
      if (inCycle) {
        while ((k >= 0) && (k >= j) && (next.get(k) == j)) {
          next.set(k, idx);
          k--;
        }
      }
    } else {
      if (inCycle) {
        next.add(0);
      } else {
        next.add(-1);
      }
    }
  }

  /**
   * Sets the index into the cycle.
   * @param i the index to add
   */
  public void setInCycle(final int i) { // ex disconnectEvent
    int n = next.size();
    int nextIdx = next.get(i);

    if (nextIdx == -1) {         // No listener
      for (int j = 0; j < n; j++) {
        next.set(j, i);
      }
    } else {
      int k = (i == 0) ? n - 1 : i - 1;
      boolean needToContinue = true;

      while (needToContinue) {
        if (next.get(k) == nextIdx) {
          next.set(k, i);
          needToContinue = (k != nextIdx);
        } else {
          needToContinue = false;
        }
        k = (k == 0) ? n - 1 : k - 1;
      }
    }
  }

  /**
   * Sets the index into the cycle.
   * @param i removes an index from the cycle
   */
  public void setOutOfCycle(final int i) { // ex reconnectEvent
    int n = next.size();

    if (next.get(i) == -1) {       // Already deactivated
    } else if (next.get(i) == i) { // This is the only one
      for (int j = 0; j < n; j++) {
        next.set(j, -1);
      }
    } else {
      int j = next.get(i);
      int k = (i == 0) ? n - 1 : i - 1;
      while (next.get(k) == i) {
        next.set(k, j);
        k = (k == 0) ? n - 1 : k - 1;
      }
    }
  }

  /**
   * Builds an iterator to enumerate all indices except one.
   * @param avoidIndex the index to exclude
   * @return the built iterator
   */
  public IntIterator getCycleButIterator(final int avoidIndex) {
    int n = size();
    if (avoidIndex != VarEvent.NOCAUSE) { n -= 1; }
    if (n > 0) {
      return new CyclicIterator(this, avoidIndex);
    } else {
      return new EmptyIterator();
    }
  }

  /**
   * An empty iterator.
   */
  class EmptyIterator implements IntIterator {
    /**
     * Checks if there is another element.
     * @return here always false
     */
    public boolean hasNext() {
      return false;
    }

    /**
     * The next element.
     * @return here arbitrarily 0
     */
    public int next() {
      return 0;
    }

    /**
     * Removes an element. Not supported here !
     */
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * An iterator for a pointer cycle object.
   */
  class CyclicIterator implements IntIterator {
    /**
     * The variable over whose constraint cycle we are iterating.
     */
    private IStateIntVector itnext;

    /**
     * Current index of the iteration
     * (the one just returned at the last call to next()).
     */
    private int k;

    /**
     * The index where the iteration started when the iterator was created.
     */
    private int endMarker;

    /**
     * Marker: true when the iteration is from the first index to the vector
     * end (first half of the iteration) and false when the itereation is
     * from the vector start to the first index,
     * excluded (second half of the iteration).
     */
    private boolean didCircleArnoundTheEnd;

    /**
     * Builds the iterator the a specified cycle, except an index (typically
     * the constraint responsible of the event).
     * @param pCycle the cycle
     * @param avoidIndex the index to exclude
     */
    public CyclicIterator(final StoredPointerCycle pCycle,
                          final int avoidIndex) {
      itnext = pCycle.next;
      didCircleArnoundTheEnd = false;
      int n = pCycle.size();
      if (n > 0) {
        if (avoidIndex == -1) {
          k = n - 1;
          endMarker = n;
        } else {
          k = avoidIndex;
          endMarker = avoidIndex;
        }
      } else {
        k = -1;
        endMarker = -1;
      }
    }

    /**
     * Checks if there is more elements.
     * @return true if there is another element
     */
    public boolean hasNext() {
      int nextk = itnext.get(k);
      if (!didCircleArnoundTheEnd) {
        return ((nextk > k)
            || ((nextk > -1) && (nextk < endMarker)));
      } else {
        return ((nextk > k) && (nextk < endMarker));
      }
    }

    /**
     * Searchs the next element in the data structure.
     * @return the next element.
     */
    public int next() {
      int prevk = k;
      k = itnext.get(k);
      if (!didCircleArnoundTheEnd && (k <= prevk)) {
        prevk = -1;          // useless
        didCircleArnoundTheEnd = true;
      }
      return k;
    }

    /**
     * Removes an element. Not supported here !
     */
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

}

/*    public void propagateEvent(EventQueue q) throws ContradictionException {

    // first, mark event
    VarEventQueue queue = (VarEventQueue) q;
    queue.setPopping(true);

    AbstractVar v =    logger.finer("propagate " + this.toString());
 getModifiedVar();
    this.freeze();

    int n = v.getNbConstraints();
    if (n > 0) {
      StoredIntVector constList = getNextConst();
      int prevk = cause;
      int k = (cause == NOCAUSE) ? constList.get(n - 1) : constList.get(cause);

      if ((k >= 0) && (k != cause)) {
        Constraint[] constraints = getModifiedVar().getConstraints();
        Integer[] indices = getModifiedVar().getVarIndices();

        while (k > prevk) {
          constraints[k].propagateEvent(indices[k].intValue(), this);
          prevk = k;
          k = constList.get(k);
        }

        prevk = -1; // 0 est authorise !!! => il faut mettre -1

        while ((k > prevk) && (k < cause)) {
          constraints[k].propagateEvent(indices[k].intValue(), this);
          prevk = k;
          k = constList.get(k);
        }
      }
    }
    // last, release event
    this.release();
    queue.setPopping(false);
  }
  */

