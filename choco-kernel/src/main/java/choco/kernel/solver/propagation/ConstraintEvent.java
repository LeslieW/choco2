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

import choco.Choco;
import choco.kernel.solver.ContradictionException;

import java.util.logging.Level;
import java.util.logging.Logger;

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
   * Reference to an object for logging trace statements related to propagation events (using the java.util.logging package)
   */

  private static Logger logger = Logger.getLogger("choco.kernel.solver.propagation");

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
      if (Choco.DEBUG) {
        if (!this.touchedConstraint.isActive()) {
          System.err.println("There should not be some not active constraint events in the queue !");
        }
      }
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
    if (logger.isLoggable(Level.WARNING))
      logger.warning("Const Awake Event does not need to be cleared !");
  }
}

