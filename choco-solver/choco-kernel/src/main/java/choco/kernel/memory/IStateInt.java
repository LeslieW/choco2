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

