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
package choco.cp.solver.propagation;

import choco.cp.solver.variables.real.RealVarImpl;
import choco.kernel.common.util.IntIterator;
import choco.kernel.memory.PartiallyStoredIntVector;
import choco.kernel.memory.PartiallyStoredVector;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.propagation.RealVarEventListener;
import choco.kernel.solver.propagation.VarEvent;
import choco.kernel.solver.variables.real.RealVar;

import java.util.logging.Level;

/**
 * An event for real interval variable modifications.
 */
public class RealVarEvent extends VarEvent<RealVarImpl> {
  public static final int INCINF = 0;
  public static final int DECSUP = 1;

  public final static int EMPTYEVENT = 0;
  public final static int BOUNDSEVENT = 3;
  public final static int INFEVENT = 1;
  public final static int SUPEVENT = 2;

  public RealVarEvent(RealVarImpl var) {
    super(var);
  }

  public String toString() {
    return ("VarEvt(" + modifiedVar.toString() + ")[" + eventType + ":"
        + ((eventType & INFEVENT) != 0 ? "I" : "")
        + ((eventType & SUPEVENT) != 0 ? "S" : "")
        + "]");
  }

  public void clear() {
    this.eventType = EMPTYEVENT;
    modifiedVar.getDomain().clearDeltaDomain();
  }

  protected boolean release() {
    return modifiedVar.getDomain().releaseDeltaDomain();
  }

  protected void freeze() {
    modifiedVar.getDomain().freezeDeltaDomain();
    cause = NOEVENT;
    eventType = 0;
  }

  public boolean getReleased() {
    return modifiedVar.getDomain().getReleasedDeltaDomain();
  }

  public boolean propagateEvent() throws ContradictionException {
	  if(LOGGER.isLoggable(Level.FINER)) LOGGER.log(Level.FINER,"propagate {0}", this);
    // first, mark event
    int evtType = eventType;
    int evtCause = cause;
    freeze();

    if (evtType <= BOUNDSEVENT) {     // only two first bits (bounds) are on
      if (evtType == INFEVENT)
        propagateInfEvent(evtCause);
      else if (evtType == SUPEVENT)
        propagateSupEvent(evtCause);
      else if (evtType == BOUNDSEVENT) {
        propagateBoundsEvent(evtCause);
      }
    }
    // last, release event
    return release();
  }

  /**
   * Propagates the update to the upper bound
   */
  public void propagateSupEvent(int evtCause) throws ContradictionException {
    RealVar v = getModifiedVar();
    PartiallyStoredVector constraints = v.getConstraintVector();
    PartiallyStoredIntVector indices = v.getIndexVector();

    for (IntIterator cit = constraints.getIndexIterator(); cit.hasNext();) {
      int idx = cit.next();
      if (idx != evtCause) {
        RealVarEventListener c = (RealVarEventListener) constraints.get(idx);
        if (c.isActive()) {
          int i = indices.get(idx);
          c.awakeOnSup(i);
        }
      }
    }
  }

  /**
   * Propagates the update to the lower bound
   */
  public void propagateInfEvent(int evtCause) throws ContradictionException {
	RealVar v = getModifiedVar();
    PartiallyStoredVector constraints = v.getConstraintVector();
    PartiallyStoredIntVector indices = v.getIndexVector();

    for (IntIterator cit = constraints.getIndexIterator(); cit.hasNext();) {
      int idx = cit.next();
      if (idx != evtCause) {
        RealVarEventListener c = (RealVarEventListener) constraints.get(idx);
        if (c.isActive()) {
          int i = indices.get(idx);
          c.awakeOnInf(i);
        }
      }
    }
  }

  /**
   * Propagates the update to the domain lower and upper bounds
   */
  public void propagateBoundsEvent(int evtCause) throws ContradictionException {
	RealVar v = getModifiedVar();
    PartiallyStoredVector constraints = v.getConstraintVector();
    PartiallyStoredIntVector indices = v.getIndexVector();

    for (IntIterator cit = constraints.getIndexIterator(); cit.hasNext();) {
      int idx = cit.next();
      if (idx != evtCause) {
        RealVarEventListener c = (RealVarEventListener) constraints.get(idx);
        if (c.isActive()) {
          int i = indices.get(idx);
          c.awakeOnInf(i);
          c.awakeOnSup(i);
        }
      }
    }

  }

  /**
   * Retrieves the event type
   */
  public int getEventType() { /// TODO : dans VarEvent !
    return eventType;
  }
}
