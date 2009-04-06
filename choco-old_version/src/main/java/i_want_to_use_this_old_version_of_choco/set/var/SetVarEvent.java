package i_want_to_use_this_old_version_of_choco.set.var;

import i_want_to_use_this_old_version_of_choco.AbstractVar;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredIntVector;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredVector;
import i_want_to_use_this_old_version_of_choco.prop.VarEvent;
import i_want_to_use_this_old_version_of_choco.set.SetConstraint;
import i_want_to_use_this_old_version_of_choco.set.SetVar;
import i_want_to_use_this_old_version_of_choco.util.BitSet;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 6 juin 2004
 * Time: 14:21:13
 * To change this template use File | Settings | File Templates.
 */
public class SetVarEvent extends VarEvent {

  /**
   * Constants for the <i>eventType</i> bitvector: index of bit for events on SetVars
   */
  public static final int REMENV = 0;
  public static final int ADDKER = 1;
  public static final int INSTSET = 2;

  public static final int ENVEVENT = 1;
  public static final int KEREVENT = 2;
  public static final int BOUNDSEVENT = 3;
  public static final int INSTSETEVENT = 4;

  public SetVarEvent(AbstractVar var) {
    super(var);
    eventType = EMPTYEVENT;
  }

  public SetVar getIntVar() {
    return (SetVar) modifiedVar;
  }

  /**
   * useful for debugging
   */
  public String toString() {
    return ("VarEvt(" + modifiedVar.toString() + ")[" + eventType + ":"
        + (BitSet.getBit(eventType, REMENV) ? "E" : "")
        + (BitSet.getBit(eventType, ADDKER) ? "K" : "")
        + (BitSet.getBit(eventType, INSTSET) ? "X" : "")
        + "]");
  }

  /**
   * Clears the var: delegates to the basic events.
   */
  public void clear() {
    this.eventType = EMPTYEVENT;
    (((SetVar) modifiedVar).getDomain()).getEnveloppeDomain().clearDeltaDomain();
    (((SetVar) modifiedVar).getDomain()).getKernelDomain().clearDeltaDomain();
  }


  protected void freeze() {
    (((SetVar) modifiedVar).getDomain()).getEnveloppeDomain().freezeDeltaDomain();
    (((SetVar) modifiedVar).getDomain()).getKernelDomain().freezeDeltaDomain();
    cause = NOEVENT;
    eventType = 0;
  }

  protected boolean release() {
    return ((SetVar) modifiedVar).getDomain().getEnveloppeDomain().releaseDeltaDomain() &&
        ((SetVar) modifiedVar).getDomain().getKernelDomain().releaseDeltaDomain();
  }

  public boolean getReleased() {
    return (((SetVar) modifiedVar).getDomain()).getEnveloppeDomain().getReleasedDeltaDomain() &&
        (((SetVar) modifiedVar).getDomain()).getKernelDomain().getReleasedDeltaDomain();
  }

  public IntIterator getEnvEventIterator() {
    return (((SetVar) modifiedVar).getDomain()).getEnveloppeDomain().getDeltaIterator();
  }

  public IntIterator getKerEventIterator() {
    return (((SetVar) modifiedVar).getDomain()).getKernelDomain().getDeltaIterator();
  }

  /**
   * Propagates the event through calls to the propagation engine.
   *
   * @return true if the event has been fully propagated (and can thus be discarded), false otherwise
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
   */
  public boolean propagateEvent() throws ContradictionException {
    if (logger.isLoggable(Level.FINER))
      logger.finer("propagate " + this.toString());
    // first, mark event
    int evtType = eventType;
    int evtCause = cause;
    freeze();

    if (evtType >= INSTSETEVENT)
      propagateInstEvent(evtCause);
    else if (evtType <= BOUNDSEVENT) {
      if (evtType == ENVEVENT)
        propagateEnveloppeEvents(evtCause);
      else if (evtType == KEREVENT)
        propagateKernelEvents(evtCause);
      else if (evtType == BOUNDSEVENT) {
        propagateKernelEvents(evtCause);
        propagateEnveloppeEvents(evtCause);
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
        SetConstraint c = (SetConstraint) constraints.get(idx);
        if (c.isActive()) {
          int i = indices.get(idx);
          c.awakeOnInst(i);
        }
      }
    }
  }

  /**
   * Propagates a set of value removals
   */
  public void propagateKernelEvents(int evtCause) throws ContradictionException {
    AbstractVar v = getModifiedVar();
    PartiallyStoredVector constraints = v.getConstraintVector();
    PartiallyStoredIntVector indices = v.getIndexVector();

    for (IntIterator cit = constraints.getIndexIterator(); cit.hasNext();) {
      int idx = cit.next();
      if (idx != evtCause) {
        SetConstraint c = (SetConstraint) constraints.get(idx);
        if (c.isActive()) {
          int i = indices.get(idx);
          c.awakeOnkerAdditions(i, this.getKerEventIterator());
        }
      }
    }
  }

  /**
   * Propagates a set of value removals
   */
  public void propagateEnveloppeEvents(int evtCause) throws ContradictionException {
    AbstractVar v = getModifiedVar();
    PartiallyStoredVector constraints = v.getConstraintVector();
    PartiallyStoredIntVector indices = v.getIndexVector();

    for (IntIterator cit = constraints.getIndexIterator(); cit.hasNext();) {
      int idx = cit.next();
      if (idx != evtCause) {
        SetConstraint c = (SetConstraint) constraints.get(idx);
        if (c.isActive()) {
          int i = indices.get(idx);
          c.awakeOnEnvRemovals(i, this.getEnvEventIterator());
        }
      }
    }
  }

}
