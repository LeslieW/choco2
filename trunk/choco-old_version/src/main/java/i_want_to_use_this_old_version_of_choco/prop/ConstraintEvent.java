// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.prop;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Propagator;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class for constraint revisions in the propagation process.
 */
public class ConstraintEvent implements PropagationEvent {

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

  private static Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop");

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
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
   */

  public boolean propagateEvent() throws ContradictionException {
    if (this.initialized) {
      this.touchedConstraint.propagate();
    } else {
      this.touchedConstraint.awake();
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

