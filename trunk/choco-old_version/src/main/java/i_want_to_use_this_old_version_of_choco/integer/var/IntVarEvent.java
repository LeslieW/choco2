// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.var;

import i_want_to_use_this_old_version_of_choco.AbstractVar;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredIntVector;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredVector;
import i_want_to_use_this_old_version_of_choco.prop.VarEvent;
import i_want_to_use_this_old_version_of_choco.util.BitSet;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.logging.Level;

public class IntVarEvent extends VarEvent {

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

  public IntVarEvent(AbstractVar var) {
    super(var);
    eventType = EMPTYEVENT;
  }

  public IntDomainVarImpl getIntVar() {
    return (IntDomainVarImpl) modifiedVar;
  }

  /**
   * useful for debugging
   */
  public String toString() {
    return ("VarEvt(" + modifiedVar.toString() + ")[" + eventType + ":"
        + (BitSet.getBit(eventType, INCINF) ? "I" : "")
        + (BitSet.getBit(eventType, DECSUP) ? "S" : "")
        + (BitSet.getBit(eventType, REMVAL) ? "r" : "")
        + (BitSet.getBit(eventType, INSTINT) ? "X" : "")
        + "]");
  }

  /**
   * Retrieves the event type
   */
  public int getEventType() {
    return eventType;
    //return BitSet.getHeavierBit(eventType);
  }

  /**
   * records the event type
   */
  public void setEventType(int evtType) {
    eventType = BitSet.setBit(eventType, evtType);
    //  with bit codes
    // INCINF = 0;
    // DECSUP = 1;
    // REMVAL = 2;
    // REMMANYVALS = 3;
    // INSTINT = 4;
  }

  /**
   * Clears the var: delegates to the basic events.
   */
  public void clear() {
    this.eventType = EMPTYEVENT;
    cause = NOEVENT;
    getIntVar().getDomain().clearDeltaDomain();
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
    return getIntVar().getDomain().releaseDeltaDomain();
  }

  protected void freeze() {
    getIntVar().getDomain().freezeDeltaDomain();
    cause = NOEVENT;
    eventType = EMPTYEVENT;
  }

  public boolean getReleased() {
    return getIntVar().getDomain().getReleasedDeltaDomain();
  }

  /**
   * Returns an iterator over the set of removed values
   *
   * @return an iterator over the set of values that have been removed from the domain
   */
  public IntIterator getEventIterator() {
    return getIntVar().getDomain().getDeltaIterator();
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

    if (evtType >= INSTINTbitvector)           // heavy bit (INSTINT) is on and dominates all other bits
      propagateInstEvent(evtCause);
    else if (evtType <= BOUNDSbitvector) {     // only two first bits (bounds) are on
      if (evtType == INCINFbitvector)
        propagateInfEvent(evtCause);
      else if (evtType == DECSUPbitvector)
        propagateSupEvent(evtCause);
      else if (evtType == BOUNDSbitvector) {
        propagateBoundsEvent(evtCause);
      }
    } else if (evtType >= REMVALbitvector) {   // the REMVAL bit is on + maybe INCINF / DECSUP bits
      propagateRemovalsEvent(evtCause);
      // Note: once all removals have been propagated, the bound events are themselves propagated
      // This supposes that a constraint reacts only once to updates
      // (the reaction corresponding to a removal that happens to be a bound should be performed once,
      //  either in awakeOnInf/Sup/Bound or in awakeOnRem)
      int deltaEvtType = evtType - REMVALbitvector;
      if (deltaEvtType > 0) {
        if (deltaEvtType == INCINFbitvector)
          propagateInfEvent(evtCause);
        else if (deltaEvtType == DECSUPbitvector)
          propagateSupEvent(evtCause);
        else if (deltaEvtType == BOUNDSbitvector)
          propagateBoundsEvent(evtCause);
      }
    }
    // last, release event
    return release();
  }

  /**
   * Propagates the instantiation event
   */
  public void propagateInstEvent(int evtCause) throws ContradictionException {
    AbstractVar v = getModifiedVar();
    PartiallyStoredVector constraints = v.getConstraintVector();
    PartiallyStoredIntVector indices = v.getIndexVector();

    for (IntIterator cit = constraints.getIndexIterator(); cit.hasNext();) {
      int idx = cit.next();
      if (idx != evtCause) {
        IntConstraint c = (IntConstraint) constraints.get(idx);
        if (c.isActive()) {
          int i = indices.get(idx);
          c.awakeOnInst(i);
        }
      }
    }

  }


  /**
   * Propagates the update to the upper bound
   */
  public void propagateSupEvent(int evtCause) throws ContradictionException {
    AbstractVar v = getModifiedVar();
    PartiallyStoredVector constraints = v.getConstraintVector();
    PartiallyStoredIntVector indices = v.getIndexVector();

    for (IntIterator cit = constraints.getIndexIterator(); cit.hasNext();) {
      int idx = cit.next();
      if (idx != evtCause) {
        IntConstraint c = (IntConstraint) constraints.get(idx);
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
    AbstractVar v = getModifiedVar();
    PartiallyStoredVector constraints = v.getConstraintVector();
    PartiallyStoredIntVector indices = v.getIndexVector();

    for (IntIterator cit = constraints.getIndexIterator(); cit.hasNext();) {
      int idx = cit.next();
      if (idx != evtCause) {
        IntConstraint c = (IntConstraint) constraints.get(idx);
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
    AbstractVar v = getModifiedVar();
    PartiallyStoredVector constraints = v.getConstraintVector();
    PartiallyStoredIntVector indices = v.getIndexVector();

    for (IntIterator cit = constraints.getIndexIterator(); cit.hasNext();) {
      int idx = cit.next();
      if (idx != evtCause) {
        IntConstraint c = (IntConstraint) constraints.get(idx);
        if (c.isActive()) {
          int i = indices.get(idx);
          c.awakeOnBounds(i);
        }
      }
    }
  }

  /**
   * Propagates a set of value removals
   */
  public void propagateRemovalsEvent(int evtCause) throws ContradictionException {
    AbstractVar v = getModifiedVar();
    PartiallyStoredVector constraints = v.getConstraintVector();
    PartiallyStoredIntVector indices = v.getIndexVector();

    for (IntIterator cit = constraints.getIndexIterator(); cit.hasNext();) {
      int idx = cit.next();
      if (idx != evtCause) {
        IntConstraint c = (IntConstraint) constraints.get(idx);
        if (c.isActive()) {
          int i = indices.get(idx);
          c.awakeOnRemovals(i, this.getEventIterator());
        }
      }
    }
  }


}