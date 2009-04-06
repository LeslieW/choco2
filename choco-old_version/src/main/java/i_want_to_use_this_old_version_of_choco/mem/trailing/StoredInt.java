// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.mem.trailing;

import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;

import java.util.logging.Logger;

/**
 * A class implementing backtrackable integers.
 */
public final class StoredInt implements IStateInt {
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

  private int currentValue;


  /**
   * The last world the search was moidified in.
   */

  int worldStamp;


  /**
   * The current {@link StoredIntTrail}.
   */

  private final StoredIntTrail trail;


  /**
   * Constructs a stored search with an unknown initial value.
   * Note: this constructor should not be used directly: one should instead
   * use the IEnvironment factory
   */

  public StoredInt(EnvironmentTrailing env) {
    this(env, UNKNOWN_INT);
  }


  /**
   * Constructs a stored search with an initial value.
   * Note: this constructor should not be used directly: one should instead
   * use the IEnvironment factory
   */

  public StoredInt(EnvironmentTrailing env, int i) {
    environment = env;
    currentValue = i;
    worldStamp = env.getWorldIndex();
    trail = (StoredIntTrail) this.environment.getTrail(IEnvironment.INT_TRAIL);
  }


  /**
   * Returns the current value.
   */

  public int get() {
    return currentValue;
  }


  /**
   * Checks if a value is currently stored.
   */

  public boolean isKnown() {
    return (currentValue != UNKNOWN_INT);
  }


  /**
   * Modifies the value and stores if needed the former value on the
   * trailing stack.
   */

  public void set(int y) {
    if (y != currentValue) {
      if (this.worldStamp < environment.getWorldIndex()) {
        trail.savePreviousState(this, currentValue, worldStamp);
        worldStamp = environment.getWorldIndex();
      }
      currentValue = y;
    }
  }

  /**
   * modifying a StoredInt by an increment
   *
   * @param delta
   */
  public void add(int delta) {
    set(get() + delta);
  }

  /**
   * Modifies the value without storing the former value on the trailing stack.
   *
   * @param y      the new value
   * @param wstamp the stamp of the world in which the update is performed
   */

  void _set(int y, int wstamp) {
    currentValue = y;
    worldStamp = wstamp;
  }

  /**
   * Retrieving the environment
   */
  public IEnvironment getEnvironment() {
    return environment;
  }

  /**
   *  pretty printing
   */
  public String toString() {
    if (isKnown())
      return String.valueOf(currentValue);
    else
      return "?";
  }
}

