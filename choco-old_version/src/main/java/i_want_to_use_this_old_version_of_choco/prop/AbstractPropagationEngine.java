// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.prop;

import i_want_to_use_this_old_version_of_choco.AbstractEntity;
import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Entity;

import java.util.logging.Logger;

/**
 * An abstract class for all implementations of propagation engines.
 */
public abstract class AbstractPropagationEngine extends AbstractEntity implements PropagationEngine {

  /**
   * Storing the cause of the last contradiction.
   */

  protected Entity contradictionCause;

  /**
   * Reference to an object for logging trace statements related to propagation events (using the java.util.logging package)
   */

  protected static Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop");


  public AbstractPropagationEngine(AbstractProblem problem) {
    super(problem);
  }

  /**
   * Erase the cause of the last contradiction.
   */

  public void setNoContradictionCause() {
    contradictionCause = null;
  }


  /**
   * Store the cause of the last contradiction.
   */

  public void setContradictionCause(Entity cause) {
    contradictionCause = cause;
  }


  /**
   * Retrieving the cause of the last contradiction.
   */

  public Entity getContradictionCause() {
    return contradictionCause;
  }

  /**
   * Gets the next queue from which a var will be propagated.
   */

  public EventQueue getNextActiveEventQueue() {
    return null;
  }
}
