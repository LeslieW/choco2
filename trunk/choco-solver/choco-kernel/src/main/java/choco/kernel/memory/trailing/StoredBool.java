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
package choco.kernel.memory.trailing;

import choco.kernel.memory.IStateBool;
import choco.kernel.memory.trailing.trail.StoredBoolTrail;


/**
 * A class implementing backtrackable booleans.
 */
public final class StoredBool extends AbstractStoredObject implements IStateBool {
  
  
  /**
   * Current value of the search.
   */
  private boolean currentValue;

  /**
   * Constructs a stored search with an initial value.
   * Note: this constructor should not be used directly: one should instead
   * use the IEnvironment factory
   */

  public StoredBool(EnvironmentTrailing env, boolean b) {
	  super(env);
	  currentValue = b;
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

  public void set(final boolean b) {
    if (b != currentValue) {
      if (this.worldStamp < environment.getWorldIndex()) {
        environment.savePreviousState(this, currentValue, worldStamp);
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

  public void _set(final boolean b, final int wstamp) {
    currentValue = b;
    worldStamp = wstamp;
  }


  @Override
public String toString() {
    return String.valueOf(currentValue);
  }

}

