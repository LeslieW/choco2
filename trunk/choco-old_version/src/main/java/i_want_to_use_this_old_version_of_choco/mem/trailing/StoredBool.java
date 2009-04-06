// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.mem.trailing;

import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.IStateBool;

import java.util.logging.Logger;

/**
 * A class implementing backtrackable booleans.
 */
public final class StoredBool implements IStateBool {
  /**
   * Reference to an object for logging trace statements related memory & backtrack (using the java.util.logging package)
   */

  private static Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.mem");

  /**
   * The current {@link EnvironmentTrailing}.
   */

  private EnvironmentTrailing environment;


  /**
   * Current value of the search.
   */

  private boolean currentValue;


  /**
   * The last world the search was moidified in.
   */

  int worldStamp;


  /**
   * The current {@link StoredIntTrail}.
   */

  private final StoredBoolTrail trail;


  /**
   * Constructs a stored search with an initial value.
   * Note: this constructor should not be used directly: one should instead
   * use the IEnvironment factory
   */

  public StoredBool(EnvironmentTrailing env, boolean b) {
    environment = env;
    currentValue = b;
    worldStamp = env.getWorldIndex();
    trail = (StoredBoolTrail) this.environment.getTrail(IEnvironment.BOOL_TRAIL);
  }


  /**
   * Returns the current value.
   */

  public boolean get() {
    return currentValue;
  }

  /**
   * Modifies the value and stores if needed the former value on the
   * trailing stack.
   */

  public void set(boolean b) {
    if (b != currentValue) {
      if (this.worldStamp < environment.getWorldIndex()) {
        trail.savePreviousState(this, currentValue, worldStamp);
        worldStamp = environment.getWorldIndex();
      }
      currentValue = b;
    }
  }

  /**
   * Modifies the value without storing the former value on the trailing stack.
   *
   * @param b      the new value
   * @param wstamp the stamp of the world in which the update is performed
   */

  void _set(boolean b, int wstamp) {
    currentValue = b;
    worldStamp = wstamp;
  }

  /**
   * Retrieving the environment
   */
  public IEnvironment getEnvironment() {
    return environment;
  }

  public String toString() {
    return String.valueOf(currentValue);
  }

}

