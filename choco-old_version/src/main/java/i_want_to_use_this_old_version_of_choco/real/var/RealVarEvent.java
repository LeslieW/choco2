package i_want_to_use_this_old_version_of_choco.real.var;

import i_want_to_use_this_old_version_of_choco.AbstractVar;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredIntVector;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredVector;
import i_want_to_use_this_old_version_of_choco.prop.VarEvent;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.real.constraint.RealListener;
import i_want_to_use_this_old_version_of_choco.util.BitSet;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.logging.Level;

/**
 * An event for real interval variable modifications.
 */
public class RealVarEvent extends VarEvent {
  public static final int INCINF = 0;
  public static final int DECSUP = 1;

  public final static int EMPTYEVENT = 0;
  public final static int BOUNDSEVENT = 3;
  public final static int INFEVENT = 1;
  public final static int SUPEVENT = 2;

  public RealVarEvent(AbstractVar var) {
    super(var);
  }

  public String toString() {
    return ("VarEvt(" + modifiedVar.toString() + ")[" + eventType + ":"
        + (BitSet.getBit(eventType, INCINF) ? "I" : "")
        + (BitSet.getBit(eventType, DECSUP) ? "S" : "")
        + "]");
  }

  public void clear() {
    this.eventType = EMPTYEVENT;
    ((RealVar) modifiedVar).getDomain().clearDeltaDomain();
  }

  protected boolean release() {
    return ((RealVar) modifiedVar).getDomain().releaseDeltaDomain();
  }

  protected void freeze() {
    ((RealVar) modifiedVar).getDomain().freezeDeltaDomain();
    cause = NOEVENT;
    eventType = 0;
  }

  public boolean getReleased() {
    return ((RealVar) modifiedVar).getDomain().getReleasedDeltaDomain();
  }

  public boolean propagateEvent() throws ContradictionException {
    if (logger.isLoggable(Level.FINER))
      logger.finer("propagate " + this.toString());
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
    AbstractVar v = getModifiedVar();
    PartiallyStoredVector constraints = v.getConstraintVector();
    PartiallyStoredIntVector indices = v.getIndexVector();

    for (IntIterator cit = constraints.getIndexIterator(); cit.hasNext();) {
      int idx = cit.next();
      if (idx != evtCause) {
        RealListener c = (RealListener) constraints.get(idx);
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
        RealListener c = (RealListener) constraints.get(idx);
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
        RealListener c = (RealListener) constraints.get(idx);
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
