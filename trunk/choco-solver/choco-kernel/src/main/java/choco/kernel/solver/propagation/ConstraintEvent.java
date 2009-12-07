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

/**
 * A class for constraint revisions in the propagation process.
 */
public class ConstraintEvent implements PropagationEvent {

    public final static int HIGH = 0, MEDIUM = 1, LOW = 2;
    public final static int nbpriority = 3;


  /**
   * The touched constraint.
   */

  private Propagator touchedConstraint;


  /**
   * Specifies if the constraint should be initialized.
   */

  private boolean initialized = false;


  /**
   * Returns the priority of the var.
   */

  private int priority = (-1);

  /**
   * Constructs a new var with the specified values for the fileds.
   */

  public ConstraintEvent(Propagator constraint, boolean init, int prio) {
    this.touchedConstraint = constraint;
    this.initialized = init;
    this.priority = prio;
  }

  public Object getModifiedObject() {
    return touchedConstraint;
  }

  /**
   * Returns the priority of the var.
   */

  public int getPriority() {
    return priority;
  }


  /**
   * Propagates the var: awake or propagate depending on the init status.
   *
   * @throws choco.kernel.solver.ContradictionException
   *
   */

  public boolean propagateEvent() throws ContradictionException {
      if (this.initialized) {
          assert (this.touchedConstraint.isActive());
          this.touchedConstraint.propagate();
      } else {
          this.touchedConstraint.awake();
          this.touchedConstraint.setActiveSilently();
      }
      return true;
  }


  /**
   * Returns if the constraint is initialized.
   */

  public boolean isInitialized() {
    return this.initialized;
  }


  /**
   * Sets if the constraint is initialized.
   */

  public void setInitialized(boolean init) {
    this.initialized = init;
  }


  /**
   * Testing whether an event is active in the propagation network
   */

  public boolean isActive(int idx) {
    return true;
  }


  /**
   * Clears the var. This should not be called with this kind of var.
   */

  public void clear() {
	  LOGGER.warning("Const Awake Event does not need to be cleared !");
  }
}

