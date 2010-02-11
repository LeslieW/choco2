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
package choco.kernel.solver.propagation;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.event.PropagationEvent;
import choco.kernel.solver.propagation.listener.VarEventListener;

/**
 * An interface for all implementations of listeners.
 */
public interface Propagator extends VarEventListener, SConstraint {


  /**
   * <i>Utility:</i>
   * Testing if all the variables involved in the constraint are instantiated.
   * @return whether all the variables have been completely instantiated
   */

  public boolean isCompletelyInstantiated();


  /**
   * Forces a propagation of the constraint.
   * @param isInitialPropagation indicates if it is the initial propagation or not
   */

  public void constAwake(boolean isInitialPropagation);


  /**
   * <i>Propagation:</i>
   * Propagating the constraint for the very first time until local
   * consistency is reached.
   * @exception ContradictionException contradiction exception
   */

  public void awake() throws ContradictionException;


  /**
   * <i>Propagation:</i>
   * Propagating the constraint until local consistency is reached.
   * @exception choco.kernel.solver.ContradictionException contradiction exception
   */

  public void propagate() throws ContradictionException;


  /**
   * <i>Propagation:</i>
   * Accessing the priority level of the queue handling the propagation
   * of the constraint. Results range from 1 (most reactive, for listeners
   * with fast propagation algorithms) to 4 (most delayed, for listeners
   * with lengthy propagation algorithms).
   * @return the priority level of the queue handling the propagation of the constraint
   */

  public int getPriority();


  /**
   * Returns the constraint awake var associated with this constraint.
   * @return the constraint awake var associated with this constraint.
   */

  public PropagationEvent getEvent();

  /**
   * Checks whether the constraint is definitely satisfied, no matter what further restrictions
   * occur to the domain of its variables.
   * @return wether the constraint is entailed
   */
  public Boolean isEntailed();

  /**
   * tests if the constraint is consistent with respect to the current state of domains
   * @return wether the constraint is consistent
   */
  public boolean isConsistent();

  public int getFilteredEventMask(int idx);

}