// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.mem;

/**
 * Describes an search vector with states (describing some history of the data structure).
 */
public interface IStateIntVector {

  /**
   * Minimal capacity of a vector
   */
  public static final int MIN_CAPACITY = 8;

  /**
   * Returns the current size of the stored search vector.
   */

  public int size();

  /**
   * Checks if the vector is empty.
   */

  public boolean isEmpty();

  /**
   * Adds a new search at the end of the vector.
   *
   * @param i The search to add.
   */

  public void add(int i);

  /**
   * removes the search at the end of the vector.
   * does nothing when called on an empty vector
   */

  public void removeLast();

  /**
   * Returns the <code>index</code>th element of the vector.
   */

  public int get(int index);

  /**
   * Assigns a new value <code>val</code> to the element <code>index</code>.
   */

  public int set(int index, int val);
}
