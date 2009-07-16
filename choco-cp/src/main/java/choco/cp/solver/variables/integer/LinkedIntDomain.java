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
package choco.cp.solver.variables.integer;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.IStateIntVector;

import java.util.Random;

/**
 * Integer domain implementation using linked list of indices. This implementation is more
 * costful in terms of memory than bit set implementation but should be more efficient in terms
 * of CPU (mainly for interating the domain).
 * <p/>
 * This implementation should be extendable to deal with large sparse domains, since we mainly
 * deal with indices of values (which is managed implicitely here: offset + index).
 * <p/>
 * Author: Guillaume Rochart
 * Creation date: January, 20th 2007
 */
public class LinkedIntDomain extends AbstractIntDomain {
  /**
   * A random generator for random value from the domain
   */

  protected static Random random = new Random(System.currentTimeMillis());

  /**
   * A vector containing the index of the next value in the domain. The value is -1 if the
   * value in not in the domain anymore.
   */
  protected final IStateIntVector nextIndex;

  /**
   * A vector containing the index of the previous value in the domain. The value is -1 if the
   * value in not in the domain anymore.
   */
  protected final IStateIntVector prevIndex;

  /**
   * The (dynamic) lower bound of the domain.
   */
  protected final IStateInt lowerBound;

  /**
   * The (dynamic) upper bound of the domain.
   */
  protected final IStateInt upperBound;

  /**
   * The (dynamic) size of the domain.
   */
  protected final IStateInt size;

  /**
   * The value of the original lower bound of the domain.
   */
  protected final int offset;

  /**
   * A chained list implementing two subsets of values:
   * - the removed values waiting to be propagated
   * - the removed values being propagated
   * (each element points to the index of the enxt element)
   * -1 for the last element
   */
  protected int[] chain;

  /**
   * start of the chain for the values waiting to be propagated
   * -1 for empty chains
   */
  protected int firstIndexToBePropagated;

  /**
   * start of the chain for the values being propagated
   * -1 for empty chains
   */
  protected int firstIndexBeingPropagated;

  /**
   * Constructs a new domain for the specified variable and bounds.
   *
   * @param v The involved variable.
   * @param a Minimal value.
   * @param b Maximal value.
   */

  public LinkedIntDomain(IntDomainVarImpl v, int a, int b) {
    variable = v;
    solver = v.getSolver();
    IEnvironment env = solver.getEnvironment();
    this.offset = a;
    lowerBound = env.makeInt(a);
    upperBound = env.makeInt(b);
    int size = b - a + 1;
    this.size = env.makeInt(size);
    int[] prevIndices = new int[size];
    int[] nextIndices = new int[size];
    for (int i = 0; i < nextIndices.length; i++) {
      nextIndices[i] = (i + 1) % size;
      prevIndices[i] = (i - 1 + size) % size;
    }
    nextIndex = env.makeIntVector(nextIndices);
    prevIndex = env.makeIntVector(prevIndices);

    chain = new int[size];
    firstIndexToBePropagated = -1;
    firstIndexBeingPropagated = -1;
  }

  public LinkedIntDomain(IntDomainVarImpl v, int[] sortedValues) {
    variable = v;
    solver = v.getSolver();
    IEnvironment env = solver.getEnvironment();
    this.offset = sortedValues[0];
    lowerBound = env.makeInt(sortedValues[0]);
    upperBound = env.makeInt(sortedValues[sortedValues.length - 1]);
    int size = sortedValues.length;
    this.size = env.makeInt(size);
    int[] prevIndices = new int[size];
    int[] nextIndices = new int[size];
    for (int i = 0; i < sortedValues.length; i++) {
      nextIndices[i] = (i+1)%sortedValues.length;
      prevIndices[i] = (i-1+sortedValues.length)%sortedValues.length;
    }
    nextIndex = env.makeIntVector(nextIndices);
    prevIndex = env.makeIntVector(prevIndices);

    chain = new int[size];
    firstIndexToBePropagated = -1;
    firstIndexBeingPropagated = -1;
  }

  /**
   * Function to find the value from the index.
   *
   * @param index the index of the value.
   * @return the designed value in the domain.
   */
  protected int indexToValue(int index) {
    return index + offset;
  }

  /**
   * Function to find the index of a given value in the domain. Warning! There is no check!
   *
   * @param value the value of the domain (It Should be checked before calling!!)
   * @return the index of this value.
   */
  protected int valueToIndex(int value) {
    return value - offset;
  }

  /**
   * Inner function in order to maintain data structure when a value is removed.
   *
   * @param indexToRemove should be a valid index of value in the domain.
   */
  protected void removeIndex(int indexToRemove) {
    nextIndex.set(indexToRemove, -1);
    prevIndex.set(indexToRemove, -1);
    chain[indexToRemove] = firstIndexToBePropagated;
    firstIndexToBePropagated = indexToRemove;
    size.add(-1);
  }

  /**
   * Returns the lower bound of the domain in O(1).
   *
   * @return the lower bound of the domain.
   */
  public int getInf() {
    return lowerBound.get();
  }

  /**
   * Returns the upper bound of the domain in O(1).
   *
   * @return the upper bound of the domain.
   */
  public int getSup() {
    return upperBound.get();
  }

  /**
   * Checks if the value x is in the current domain. It is done in O(1).
   *
   * @param x the value to check wetehr it is in the domain.
   *          It can be completely outside of the original domain in this implementation.
   * @return true if the value x is in the domain.
   */
  public boolean contains(int x) {
    int xIndex = valueToIndex(x);
    if (xIndex < 0 || xIndex >= nextIndex.size()) return false;
    return nextIndex.get(xIndex) != -1;
  }

  /**
   * Updates the lower bound of the domain to the next value contained in the domain
   * which is more or equal to x.
   * <p/>
   * This is done in O(n) with n the size of the domain.
   *
   * @param x a value the lower bound should be more than. x should be less than the upper bound!
   * @return the new lower bound of the domain.
   */
  public int updateInf(int x) {
    int xIndex = valueToIndex(x);
    int currentInf = valueToIndex(lowerBound.get());
    int sup = valueToIndex(upperBound.get());
    while (currentInf < xIndex && currentInf <= sup) {
      int next = nextIndex.get(currentInf);
      removeIndex(currentInf);
      currentInf = next;
    }
    prevIndex.set(currentInf, sup);
    nextIndex.set(sup, currentInf);
    lowerBound.set(indexToValue(currentInf));
    return indexToValue(currentInf);
  }

  /**
   * Updates the upper bound of the domain to the next value contained in the domain
   * which is less or equal to x.
   * <p/>
   * This is done in O(n) with n the size of the domain.
   *
   * @param x a value the upper bound should be less than. x should be more than lower bound!
   * @return the new upper bound of the domain.
   */
  public int updateSup(int x) {
    int xIndex = valueToIndex(x);
    int currentSup = valueToIndex(upperBound.get());
    int inf = valueToIndex(lowerBound.get());
    while (currentSup > xIndex) {
      int prev = prevIndex.get(currentSup);
      removeIndex(currentSup);
      currentSup = prev;
    }
    prevIndex.set(inf, currentSup);
    nextIndex.set(currentSup, inf);
    upperBound.set(indexToValue(currentSup));
    return indexToValue(currentSup);
  }

  /**
   * Restricts the domain to the given value.
   * <p/>
   * This is done in O(n) with n the size of the domain.
   *
   * @param x the value to which this domain should be restricted to.
   */
  public void restrict(int x) {
    int xIndex = valueToIndex(x);
    int currentInf = valueToIndex(lowerBound.get());
    int currentSup = valueToIndex(upperBound.get());
    while (currentInf < xIndex) {
      int next = nextIndex.get(currentInf);
      removeIndex(currentInf);
      currentInf = next;
    }
    while (currentSup > xIndex) {
      int prev = prevIndex.get(currentSup);
      removeIndex(currentSup);
      currentSup = prev;
    }
    prevIndex.set(xIndex, xIndex);
    nextIndex.set(xIndex, xIndex);
    lowerBound.set(x);
    upperBound.set(x);
  }

  /**
   * Removes a precise value from the domain.
   * <p/>
   * This is done in O(1).
   *
   * @param x the value in the domain.
   * @return true if a value was actually removed.
   */
  public boolean remove(int x) {
    if (!contains(x)) return false;
    int xIndex = valueToIndex(x);
    int next = nextIndex.get(xIndex);
    int prev = prevIndex.get(xIndex);
    if (x == lowerBound.get()) lowerBound.set(indexToValue(next));
    if (x == upperBound.get()) upperBound.set(indexToValue(prev));
    removeIndex(xIndex);
    prevIndex.set(next, prev);
    nextIndex.set(prev, next);
    return true;
  }

  /**
   * Retuens the dynamic size of the domain, that is the number of possible values in the domain when
   * the method is called.
   *
   * @return the size of the domain.
   */
  public int getSize() {
    return size.get();
  }

  /**
   * Looks for the value after x in the domain. It is safe with value out of the domain.
   *
   * It is done in O(1) if x is a value of the domain, O(n) else.
   * @param x A value (in or out of the domain).
   * @return The value in the domain after x, Integer.MAX_VALUE if none.
   */
  public int getNextValue(int x) {
    int inf = lowerBound.get();
    if (x < inf) return inf;
    if (!hasNextValue(x)) return Integer.MAX_VALUE;
    int xIndex = valueToIndex(x);
    if (nextIndex.get(xIndex) != -1) {
      return indexToValue(nextIndex.get(xIndex));
    }
    xIndex++;
    while (nextIndex.get(xIndex) == -1) xIndex++;
    return indexToValue(xIndex);
  }

  /**
   * Looks for the value before x in the domain. It is safe with value out of the domain.
   *
   * It is done in O(1) if x is a value of the domain, O(n) else.
   * @param x A value (in or out of the domain).
   * @return The value in the domain before x, Integer.MIN_VALUE if none.
   */
  public int getPrevValue(int x) {
    int sup = upperBound.get();
    if (x > sup) return sup;
    if (!hasPrevValue(x)) return Integer.MIN_VALUE;
    int xIndex = valueToIndex(x);
    if (prevIndex.get(xIndex) != -1) {
      return indexToValue(prevIndex.get(xIndex));
    }
    xIndex--;
    while (prevIndex.get(xIndex) == -1) xIndex--;
    return indexToValue(xIndex);
  }

  /**
   * Checks if there is a value after x in the domain. Basically checks that x if less than
   * the upper bound (in O(1)).
   * @param x value in or out of the domain.
   * @return true if there is value in the domain greater than x
   */
  public boolean hasNextValue(int x) {
    return x < upperBound.get();
  }

  /**
   * Checks if there is a value before x in the domain. Basically checks that x if more than
   * the lower bound (in O(1)).
   * @param x value in or out of the domain.
   * @return true if there is value in the domain smaller than x
   */
  public boolean hasPrevValue(int x) {
    return x > lowerBound.get();
  }

  /**
   * Returns a value randomly choosed in the domain.
   *
   * It is done in O(n).
   * @return a random value from the domain.
   */
  public int getRandomValue() {
    int size = getSize();
    if (size == 1) return this.getInf();
    else {
      int rand = random.nextInt(size);
      int val = this.getInf();
      for (int o = 0; o < rand; o++) {
        val = getNextValue(val);
      }
      return val;
    }
  }

  /**
   * Interface method to know if this domain is enumerated. Always true here.
   * @return true
   */
  public boolean isEnumerated() {
    return true;
  }

  /**
   * Checks if this is a boolean domain, that is the values are 0 or 1.
   * @return tue if this is a boolean domain.
   */
  public boolean isBoolean() {
    return offset == 0 && nextIndex.size() == 2;
  }


   protected DeltaIntDomainIterator _cachedDeltaIntDomainIterator = null;

  // TODO: these methods should be in a AbstractEnumeratedIntDomain !!
  public DisposableIntIterator getDeltaIterator() {
    DeltaIntDomainIterator iter = _cachedDeltaIntDomainIterator;
        if (iter != null && iter.reusable) {
            iter.init();
            return iter;
        }
        _cachedDeltaIntDomainIterator = new DeltaIntDomainIterator(this);
        return _cachedDeltaIntDomainIterator;
  }

  protected static class DeltaIntDomainIterator extends DisposableIntIterator {
    protected LinkedIntDomain domain;
    protected int currentIndex = -1;

    private DeltaIntDomainIterator(LinkedIntDomain dom) {
      domain = dom;
      currentIndex = -1;
    }

    public void dispose() {
      
    }

    public boolean hasNext() {
      if (currentIndex == -1) {
        return (domain.firstIndexBeingPropagated != -1);
      } else {
        return (domain.chain[currentIndex] != -1);
      }
    }

    public int next() {
      if (currentIndex == -1) {
        currentIndex = domain.firstIndexBeingPropagated;
      } else {
        currentIndex = domain.chain[currentIndex];
      }
      return currentIndex + domain.offset;
    }

    public void remove() {
      if (currentIndex == -1) {
        throw new IllegalStateException();
      } else {
        throw new UnsupportedOperationException();
      }
    }
  }

  /**
   * The delta domain container is "frozen" (it can no longer accept new value removals)
   * so that this set of values can be iterated as such
   */
  public void freezeDeltaDomain() {
    // freeze all data associated to bounds for the the event
    super.freezeDeltaDomain();
    // if the delta domain is already being iterated, it cannot be frozen
    if (firstIndexBeingPropagated != -1) {
    }//throw new IllegalStateException();
    else {
      // the set of values waiting to be propagated is now "frozen" as such,
      // so that those value removals can be iterated and propagated
      firstIndexBeingPropagated = firstIndexToBePropagated;
      // the container (link list) for values waiting to be propagated is reinitialized to an empty set
      firstIndexToBePropagated = -1;
    }
  }

  /**
   * after an iteration over the delta domain, the delta domain is reopened again.
   *
   * @return true iff the delta domain is reopened empty (no updates have been made to the domain
   *         while it was frozen, false iff the delta domain is reopened with pending value removals (updates
   *         were made to the domain, while the delta domain was frozen).
   */
  public boolean releaseDeltaDomain() {
    // release all data associated to bounds for the the event
    super.releaseDeltaDomain();
    // special case: the set of removals was not being iterated (because the variable was instantiated, or a bound was updated)
    if (firstIndexBeingPropagated == -1) {
      // remove all values that are waiting to be iterated
      firstIndexToBePropagated = -1;
      // return true because the event has been "flushed" (nothing more is awaiting)
      return true;
    } else { // standard case: the set of removals was being iterated
      // empty the set of values that were being propagated
      firstIndexBeingPropagated = -1;
      // if more values are waiting to be propagated, return true
      return (firstIndexToBePropagated == -1);
    }
  }

  public boolean getReleasedDeltaDomain() {
    return ((firstIndexBeingPropagated == -1) && (firstIndexToBePropagated == -1));
  }

  /**
   * cleans the data structure implementing the delta domain
   */
  public void clearDeltaDomain() {
    firstIndexBeingPropagated = -1;
    firstIndexToBePropagated = -1;
  }


  public String toString() {
    return "{" + getInf() + "..." + getSup() + "}";
  }

  public String pretty() {
    StringBuffer buf = new StringBuffer("{");
    int maxDisplay = 15;
    int count = 0;
      DisposableIntIterator it = this.getIterator();
    for (; (it.hasNext() && count < maxDisplay);) {
      int val = it.next();
      count++;
      if (count > 1) buf.append(", ");
      buf.append(val);
    }
      it.dispose();
    if (this.getSize() > maxDisplay) {
      buf.append("..., ");
      buf.append(this.getSup());
    }
    buf.append("}");
    return buf.toString();
  }

}
