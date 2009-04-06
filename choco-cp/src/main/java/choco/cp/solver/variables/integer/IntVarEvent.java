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
package choco.cp.solver.variables.integer;

import choco.kernel.common.util.DisposableIntIterator;
import choco.kernel.common.util.IntIterator;
import choco.kernel.memory.PartiallyStoredIntVector;
import choco.kernel.memory.PartiallyStoredVector;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.IntSConstraint;
import choco.kernel.solver.propagation.VarEvent;

import java.util.logging.Level;

public class IntVarEvent extends VarEvent<IntDomainVarImpl> {

  /**
   * Constants for the <i>eventType</i> bitvector: index of bit for updates to lower bound of IntVars
   */
  public static final int INCINF = 0;

  /**
   * Constants for the <i>eventType</i> bitvector: index of bit for updates to upper bound of IntVars
   */
  public static final int DECSUP = 1;

  /**
   * Constants for the <i>eventType</i> bitvector: index of bit for holes in the domain of IntVars
   */
  public static final int REMVAL = 2;

  /**
   * Constants for the <i>eventType</i> bitvector: index of bit for instantiations of IntVars
   */
  public static final int INSTINT = 3;

  /**
   * Constants for the <i>eventType</i> bitvector: value of bitvector for updates to lower bound of IntVars
   */
  public static final int INCINFbitvector = 1;

  /**
   * Constants for the <i>eventType</i> bitvector: value of bitvector for updates to upper bound of IntVars
   */
  public static final int DECSUPbitvector = 2;

  /**
   * Constants for the <i>eventType</i> bitvector: value of bitvector for updates to both bound of IntVars
   */
  public static final int BOUNDSbitvector = 3;

  /**
   * Constants for the <i>eventType</i> bitvector: value of bitvector for holes in the domain of IntVars
   */
  public static final int REMVALbitvector = 4;

  /**
   * Constants for the <i>eventType</i> bitvector: value of bitvector for instantiations of IntVars
   */
  public static final int INSTINTbitvector = 8;

  public IntVarEvent(IntDomainVarImpl var) {
    super(var);
    eventType = EMPTYEVENT;
  }

  /**
   * useful for debugging
   */
  public String toString() {
    return ("VarEvt(" + modifiedVar + ")[" + eventType + ":"
        + ((eventType & INCINFbitvector) != 0 ? "I" : "")
        + ((eventType & DECSUPbitvector) != 0 ? "S" : "")
        + ((eventType & REMVALbitvector) != 0 ? "r" : "")
        + ((eventType & INSTINTbitvector) != 0 ? "X" : "")
        + "]");
  }

  /**
   * Retrieves the event type
   */
  public int getEventType() {
    return eventType;
    //return BitSet.getHeavierBit(eventType);
  }

    @Override
    public int getPriority() {
        return modifiedVar.getPriority();
    }

    /**
   * Clears the var: delegates to the basic events.
   */
  public void clear() {
    this.eventType = EMPTYEVENT;
    cause = NOEVENT;
    modifiedVar.getDomain().clearDeltaDomain();
  }

  /**
   * the event had been "frozen", (since the call to freeze), while it was handled by the propagation engine:
   * This meant that the meaning of the event could not be changed: it represented
   * a static set of value removals, during propagation.
   * Now, the event becomes "open" again: new value removals can be hosted, the delta domain can
   * accept that further values are removed.
   * In case value removals happened while the event was frozen, the release method returns false
   * (the event cannot be released, it must be handled once more). Otherwise (the standard behavior),
   * the method returns true
   */
  protected boolean release() {
    // we no longer use the shortcut (if eventType == EMPTYEVENT => nothing to do) because of event transformation
    // (in case a removal turned out to be an instantiation, we need to release the delta domain associated to the removal)
    // boolean anyUpdateSinceFreeze = ((eventType != EMPTYEVENT) || (cause != NOEVENT));  // note: these two tests should be equivalent
    // anyUpdateSinceFreeze = (anyUpdateSinceFreeze || !(getIntVar().getDomain().releaseDeltaDomain()));
    // return !anyUpdateSinceFreeze;
    return modifiedVar.getDomain().releaseDeltaDomain();
  }

  protected void freeze() {
	modifiedVar.getDomain().freezeDeltaDomain();
    cause = NOEVENT;
    eventType = EMPTYEVENT;
  }

  public boolean getReleased() {
    return modifiedVar.getDomain().getReleasedDeltaDomain();
  }

  /**
   * Returns an iterator over the set of removed values
   *
   * @return an iterator over the set of values that have been removed from the domain
   */
  public DisposableIntIterator getEventIterator() {
    return modifiedVar.getDomain().getDeltaIterator();
  }

  /**
   * Propagates the event through calls to the propagation engine.
   *
   * @return true if the event has been fully propagated (and can thus be discarded), false otherwise
   * @throws ContradictionException
   */
  public boolean propagateEvent() throws ContradictionException {
    if (logger.isLoggable(Level.FINER))
      logger.finer("propagate " + this.toString());
    // first, mark event
    int evtType = eventType;
    int evtCause = cause;
    freeze();

    if ((propagatedEvents & INSTINTbitvector) != 0 && (evtType & INSTINTbitvector) != 0)
        propagateInstEvent(evtCause);
    if ((propagatedEvents & INCINFbitvector) != 0 && (evtType & INCINFbitvector) != 0)
        propagateInfEvent(evtCause);
    if ((propagatedEvents & DECSUPbitvector) != 0 && (evtType & DECSUPbitvector) != 0)
        propagateSupEvent(evtCause);
    if ((propagatedEvents & REMVALbitvector) != 0 && (evtType & REMVALbitvector) != 0)
        propagateRemovalsEvent(evtCause);

    // last, release event
    return release();
  }

    /**
   * Propagates the instantiation event
   */
  public void propagateInstEvent(int evtCause) throws ContradictionException {
        IntDomainVarImpl v = modifiedVar;
        PartiallyStoredVector<SConstraint> constraints = v.getConstraintVector();
        PartiallyStoredIntVector indices = v.getIndexVector();
    PartiallyStoredIntVector cindices = v.getEventsVector()[0]; 

        IntIterator cit = cindices.getIndexIterator();
        while (cit.hasNext()) {
            int idx = cindices.get(cit.next());
            if (idx != evtCause) {
                IntSConstraint c = (IntSConstraint) constraints.get(idx);
                int i = indices.get(idx);
                if (c.isActive()) {
                    c.awakeOnInst(i);
                }
            }
        }
    }


    /**
     * Propagates the update to the lower bound
     */
    public void propagateInfEvent(int evtCause) throws ContradictionException {
        IntDomainVarImpl v = modifiedVar;
        PartiallyStoredVector<SConstraint> constraints = v.getConstraintVector();
        PartiallyStoredIntVector indices = v.getIndexVector();
        PartiallyStoredIntVector cindices = v.getEventsVector()[1];

        IntIterator cit = cindices.getIndexIterator();
        while (cit.hasNext()) {
            int idx = cindices.get(cit.next());
            if (idx != evtCause) {
                IntSConstraint c = (IntSConstraint) constraints.get(idx);
                int i = indices.get(idx);
                if (c.isActive()) {
                    c.awakeOnInf(i);
                }
            }
        }
    }

    /**
   * Propagates the update to the upper bound
   */
  public void propagateSupEvent(int evtCause) throws ContradictionException {
        IntDomainVarImpl v = modifiedVar;
        PartiallyStoredVector<SConstraint> constraints = v.getConstraintVector();
        PartiallyStoredIntVector indices = v.getIndexVector();
    PartiallyStoredIntVector cindices = v.getEventsVector()[2];

        IntIterator cit = cindices.getIndexIterator();
        while (cit.hasNext()) {
            int idx = cindices.get(cit.next());
            if (idx != evtCause) {
                IntSConstraint c = (IntSConstraint) constraints.get(idx);
                int i = indices.get(idx);
                if (c.isActive()) {
                    c.awakeOnSup(i);
                }
            }
        }
    }

    /**
   * Propagates a set of value removals
   */
  public void propagateRemovalsEvent(int evtCause) throws ContradictionException {
        IntDomainVarImpl v = modifiedVar;
        PartiallyStoredVector<SConstraint> constraints = v.getConstraintVector();
        PartiallyStoredIntVector indices = v.getIndexVector();
        PartiallyStoredIntVector cindices = v.getEventsVector()[3];

        IntIterator cit = cindices.getIndexIterator();
        while (cit.hasNext()) {
            int idx = cindices.get(cit.next());
            if (idx != evtCause) {
                IntSConstraint c = (IntSConstraint) constraints.get(idx);
                int i = indices.get(idx);
                if (c.isActive()) {
                    DisposableIntIterator iter = this.getEventIterator();
                    try {
                        c.awakeOnRemovals(i, iter);
                    } finally {
                        iter.dispose();
                    }
                }
            }
        }
    }

  private int promoteEvent(int basicEvt) {
    switch(basicEvt) {
    case INSTINT:
      return INSTINTbitvector + INCINFbitvector + DECSUPbitvector + REMVALbitvector;
    
    case INCINF:
      return INCINFbitvector + REMVALbitvector;
      
    case DECSUP:
      return DECSUPbitvector + REMVALbitvector;
      
    case REMVAL:
      return REMVALbitvector;
      
    default:
      return 1 << basicEvt;
    }
  }
  
  public void recordEventTypeAndCause(int basicEvt, int idx) {
    // if no such event was active on the same variable
    if ((cause == NOEVENT) || (eventType == EMPTYEVENT)) {  // note: these two tests should be equivalent
      // the varevent is reduced to basicEvt, and the cause is recorded
      eventType = promoteEvent(basicEvt);
      cause = idx;
    } else {
      // otherwise, this basic event is added to all previous updates that are possibly mending on the same variable
      eventType = (eventType | promoteEvent(basicEvt));
      // in case the cause of this update is different from the previous cause, all causes are forgotten
      // (so that the constraints that caused the event will be reawaken)
      if (cause != idx) {
        cause = NOCAUSE;
      }
    }
  }
}