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
package choco.kernel.solver.propagation.listener;

import java.util.EventListener;

/**
 * An interface for all the search variable listeners.
 */
public interface VarEventListener extends EventListener {

  /**
   * This function connects a constraint with its variables in several ways.
   * Note that it may only be called once the constraint
   * has been fully created and is being posted to a model.
   * Note that it should be called only once per constraint.
   * This can be a dynamic addition (undone upon backtracking) or not
   *
   * @param dynamicAddition
   */
  public void addListener(boolean dynamicAddition);

  /**
   * <i>Propagation:</i>
   * A constraint is active if it is connected to the network and if it
   * does propagate.
   */

  public boolean isActive();


  /**
   * <i>Propagation:</i>
   * un-freezes a constraint
   * [a constraint is active if it is connected to the network and if it
   * does propagate]
   */

  public void setActive();

  /**
   * Same as setActive but does not add the constraint event in the queue !
   */
  void setActiveSilently();


  /**
   * <i>Propagation:</i>
   * freezes a constraint
   * [a constraint is active if it is connected to the network and if it
   * does propagate]
   */

  public void setPassive();


      /**
   * <i>Network management:</i>
   * Storing that among all listeners linked to the i-th variable of c,
   * this (the current constraint) is found at index idx.
   *
   * @param i   index of the variable in the constraint
   * @param idx index of the constraint in the among all listeners linked to that variable
   */

  void setConstraintIndex(int i, int idx);

  /**
   * <i>Network management:</i>
   * Among all listeners linked to the idx-th variable of c,
   * find the index of constraint c.
   *
   * @param idx index of the variable in the constraint
   */

  int getConstraintIdx(int idx);

}
