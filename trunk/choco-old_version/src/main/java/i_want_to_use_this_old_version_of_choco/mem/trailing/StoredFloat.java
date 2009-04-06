// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.mem.trailing;

import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.IStateFloat;

import java.util.logging.Logger;

/**
 * A class implementing a backtrackable float variable.
 */
public class StoredFloat implements IStateFloat {
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

  private double currentValue;


  /**
   * The last world the search was moidified in.
   */

  int worldStamp;


  /**
   * The current {@link StoredIntTrail}.
   */

  private final StoredFloatTrail trail;


  /**
   * Constructs a stored search with an unknown initial value.
   * Note: this constructor should not be used directly: one should instead
   * use the IEnvironment factory
   */

  public StoredFloat(EnvironmentTrailing env) {
    this(env, Double.NaN);
  }


  /**
   * Constructs a stored search with an initial value.
   * Note: this constructor should not be used directly: one should instead
   * use the IEnvironment factory
   */

  public StoredFloat(EnvironmentTrailing env, double d) {
    environment = env;
    currentValue = d;
    worldStamp = env.getWorldIndex();
    trail = (StoredFloatTrail) this.environment.getTrail(IEnvironment.FLOAT_TRAIL);
  }


  public double get() {
    return currentValue;
  }


  public boolean isKnown() {
    return (currentValue != Double.NaN);
  }


  public void set(double y) {
    if (y != currentValue) {
      if (this.worldStamp < environment.getWorldIndex()) {
        trail.savePreviousState(this, currentValue, worldStamp);
        worldStamp = environment.getWorldIndex();
      }
      currentValue = y;
    }
  }

  public void add(double delta) {
    set(get() + delta);
  }

  /**
   * Modifies the value without storing the former value on the trailing stack.
   *
   * @param y      the new value
   * @param wstamp the stamp of the world in which the update is performed
   */

  void _set(double y, int wstamp) {
    currentValue = y;
    worldStamp = wstamp;
  }

  public IEnvironment getEnvironment() {
    return environment;
  }

}
