// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.mem;

/**
 * Describes an integer with states (describing some history of the data
 * structure).
 */
public interface IStateInt {
  /**
   * Value for an unknown integer.
   */

  int UNKNOWN_INT = Integer.MAX_VALUE;


  /**
   * Minimum value an integer can be equal to.
   */

  int MININT = Integer.MIN_VALUE;


  /**
   * Maximum value an integer can be equal to.
   */

  int MAXINT = Integer.MAX_VALUE - 1;


  /**
   * Returns the current value according to the current world.
   * @return The current value of the storable variable.
   */

  int get();

  /**
   * Checks if a value is currently stored.
   * @return true if the value is known (different from UNKNOWN_INT).
   */

  boolean isKnown();

  /**
   * Modifies the value and stores if needed the former value on the
   * trailing stack.
   * @param y the new value of the variable.
   */
  void set(int y);

  /**
   * Modifying a StoredInt by an increment.
   * @param delta the value to add to the current value.
   */
  void add(int delta);

  /**
   * Retrieving the environment.
   * @return the environment associated to this variable (the object
   * responsible to manage worlds and storable variables).
   */

  IEnvironment getEnvironment();

}

