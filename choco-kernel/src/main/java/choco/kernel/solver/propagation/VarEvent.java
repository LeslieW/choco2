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

import choco.kernel.common.util.IPrioritizable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.Var;

import java.util.logging.Logger;

/**
 * Implements an
 * {@link choco.kernel.solver.propagation.PropagationEvent} for the variable events.
 */
public abstract class VarEvent <E extends Var> implements PropagationEvent, IPrioritizable {

    /**
   * empty bitvector for the event type.
   */
  public final static int NOEVENT = -2;

  /**
   * Semantic of the cause of an event: -1 means that the event is active without
   * any precise cause. (Equivalent of 0 in Claire version)
   */
  public final static int NOCAUSE = -1;

  /**
   * Cause of this basic var.
   */
  protected int cause = NOEVENT;

  /**
   * empty bitvector for the event type.
   */
  public final static int EMPTYEVENT = 0;

  /**
   * The touched variable.
   */

  protected E modifiedVar;


  /**
   * Reference to the root Logger, an object for logging trace statements related to propagation events (using the java.util.logging package)
   */

  protected static Logger logger = Logger.getLogger("choco.kernel.solver.propagation");

  /**
   * active constraints to be propagated
   */
  // protected IStateBitSet activeConstraints;

  /**
   * stores the type of update performed on the variable
   */
  protected int eventType = EMPTYEVENT;

  /**
   * The events that should be fired for the constraints
   */
  protected int propagatedEvents = 0;

  /**
   * Constructs a variable event for the specified variable and with the given
   * basic events.
   */

  public VarEvent(E var) {
    this.modifiedVar = var;
    // activeCycle = new StoredPointerCycle(getProblem().getEnvironment());
    // activeConstraints = new IStateBitSet(getProblem().getEnvironment(),0);
  }

  /*
   public int getNbListeners() {
     return activeConstraints.cardinality();
     // return activeCycle.size();
   }
   */

  public void addPropagatedEvents(int bitsmask) {
    propagatedEvents |= bitsmask;
  }

  public int getPropagatedEvents() {
    return propagatedEvents;
  }

  /**
   * Returns the touched variable.
   */

  public E getModifiedVar() {
    return modifiedVar;
  }

  /**
   * Returns the touched variable.
   */

  public Object getModifiedObject() {
    return modifiedVar;
  }

  /**
   * freezes the state of the "delta domain": the set of values that are considered for removal
   * from the domain. Further removals will be treated as a further event.
   */
  protected void freeze() {
    cause = NOEVENT;
  }

  protected boolean release() {
    boolean anyUpdateSinceFreeze = (cause != NOEVENT);
    return anyUpdateSinceFreeze;
  }

  /**
   * Propagates the event through calls to the propagation engine.
   *
   * @return true if the event has been fully propagated (and can thus be discarded), false otherwise
   * @throws choco.kernel.solver.ContradictionException
   */
  public abstract boolean propagateEvent() throws ContradictionException;

  /**
   * Clears the var: delegates to the basic events.
   */
  public abstract void clear();

  /**
   * Checks if a given listener is active or not
   *
   * @param idx the index of the listener among all listeners connected to the variable
   */


  public boolean isActive(int idx) {
//    return activeCycle.isInCycle(idx);
//      return activeConstraints.get(idx);
    return true; // TODO FIXME
  }

  /**
   * Retrieving the model.
   */

  public Solver getSolver() {
    return modifiedVar.getSolver();
  }

  /**
   * Returns the cause of this basic var.
   */
  public int getCause() {
    return cause;
  }

  public int getPriority() {
    return 0;
  }

    public int getEventType() {
        return eventType;
    }

    /**
   * tests whether the event is currently active (present in some queue) or not
   *
   * @return true if and only if the event is present in some queue, waiting to be handled
   *         (returns false if the event is either absent from the queue or is the current event,
   *         just popped from the queue and being propagated)
   */
  public boolean isEnqueued() {
    return (eventType != EMPTYEVENT);
  }

  public void recordEventTypeAndCause(int basicEvt, int idx) {
    // if no such event was active on the same variable
    if ((cause == NOEVENT) || (eventType == EMPTYEVENT)) {  // note: these two tests should be equivalent
      // the varevent is reduced to basicEvt, and the cause is recorded
      eventType = (1 << basicEvt);
      cause = idx;
    } else {
      // otherwise, this basic event is added to all previous updates that are possibly mending on the same variable
      eventType = (eventType | (1 << basicEvt));
      // in case the cause of this update is different from the previous cause, all causes are forgotten
      // (so that the constraints that caused the event will be reawaken)
      if (cause != idx) {
        cause = NOCAUSE;
      }
    }
  }

}
