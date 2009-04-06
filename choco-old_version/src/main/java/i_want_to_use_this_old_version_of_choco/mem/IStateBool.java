// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.mem;

/**
 * Describes a boolean with states (describing some history of the data structure).
 */
public interface IStateBool {
  /**
   * Returns the current value.
   */

  public boolean get();

  /**
   * Modifies the value and stores if needed the former value on the
   * trailing stack.
   */

  public void set(boolean b);

  /**
   * Retrieving the environment
   */

  public IEnvironment getEnvironment();

}

