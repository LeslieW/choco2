// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.mem;

import java.util.logging.Logger;

/**
 * A class that implements a vector of integers with two states (one current and one single historical state)
 * TODO: implement all those darn methods
 */
public class TwoStateIntVector implements IStateIntVector {

  /**
   * Reference to an object for logging trace statements related memory & backtrack (using the java.util.logging package)
   */
  private static Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.mem");

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
}
