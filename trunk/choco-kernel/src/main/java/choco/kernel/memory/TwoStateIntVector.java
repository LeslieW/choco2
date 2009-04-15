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

import choco.kernel.common.util.DisposableIntIterator;

/**
 * A class that implements a vector of integers with two states (one current and one single historical state)
 * TODO: implement all those darn methods
 */
public class TwoStateIntVector implements IStateIntVector {


  /**
   * Minimal capacity of a vector
   */
  public static final int MIN_CAPACITY = 8;

  /**
   * Contains the elements of the vector.
   */
  private int[] elementData;

  /**
   * Contains entries from the (one and only) saved state
   */

  private int[] savedElementData;

  /**
   * indicates whether the current state is different from the saved state
   */
  private boolean isModified;

  /**
   * A backtrackable search with the size of the vector.
   */
  private IStateInt size;

  /**
   * The current environment.
   */
  private IEnvironment environment;


  /**
   * @param env          the memory environment
   * @param initialSize  the initial size of the vector
   * @param initialValue the initial value for all entries of the vector
   */
  public TwoStateIntVector(IEnvironment env, int initialSize, int initialValue) {
  }

  /**
   * Returns the current size of the stored search vector.
   */

  public int size() {
    return 0;
  }

  /**
   * Checks if the vector is empty.
   */

  public boolean isEmpty() {
    return false;
  }

  /**
   * Adds a new search at the end of the vector.
   *
   * @param i The search to add.
   */

  public void add(int i) {
  }

    /**
     * Removes an int.
     *
     * @param i The search to remove.
     */
    @Override
    public void remove(int i) {
    }

    /**
   * removes the search at the end of the vector.
   * does nothing when called on an empty vector
   */

  public void removeLast() {
  }

  /**
   * Returns the <code>index</code>th element of the vector.
   */

  public int get(int index) {
    return 0;
  }

  /**
   * Assigns a new value <code>val</code> to the element <code>index</code>.
   */

  public int set(int index, int val) {
    return 0;
  }

  public void saveState() {
  }

  public void restoreState() {
  }

@Override
public DisposableIntIterator getIterator() {
	return null;
}
  
  
}
