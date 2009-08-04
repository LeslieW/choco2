//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.propagation;

import choco.cp.solver.propagation.RealVarEvent;
import choco.ecp.solver.propagation.dbt.PalmVarEvent;
import choco.ecp.solver.propagation.dbt.PalmVarEventQueue;
import choco.ecp.solver.variables.real.PalmRealVar;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.structure.PartiallyStoredIntVector;
import choco.kernel.memory.structure.PartiallyStoredVector;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.propagation.VarEvent;
import choco.kernel.solver.variables.AbstractVar;

import java.util.BitSet;

/**
 * Implements an event for real variable of PaLM.
 */
public class PalmRealVarEvent extends RealVarEvent implements PalmVarEvent {
  public int oldEventType;

  /**
   * This boolean was before in the queue
   */
  public boolean isPopping = false;

  /**
   * Constant value associated to the inf bound restoration prop.
   */
  public final static int RESTINF = 5;

  /**
   * Constant value associated to the sup bound restoration prop.
   */
  public final static int RESTSUP = 6;

  /**
   * Creates new event for the specified variable (should be a Palm real variable).
   *
   * @param var
   */
  public PalmRealVarEvent(AbstractVar var) {
    super(var);
  }

  /**
   * Computes the priority of the prop. Actually, it returns 0 for restoration events (that is urgent
   * events) and 1 for the others.
   *
   * @return The priority of this prop.
   */

  public int getPriority() {
    if (BitSet.getHeavierBit(this.getEventType()) >= RESTINF) return 0;
    return 1;
  }

  /**
   * Generic propagation method. Calls relevant methods depending on the kind of event. It handles some
   * new events for restoration purpose and call the Choco method <code>super.propagateEvent()</code>.
   *
   * @throws ContradictionException
   */

  public boolean propagateEvent() throws ContradictionException {
    // isPopping, oldEventType et oldCause permettent de remettre en etat l'evenement si une contradiction
    // a lieu pendant le traitement de cet evenement. Cf public void reset() et
    // PalmVarEventQueue.resetPopping().
    isPopping = true;
    this.oldEventType = this.eventType;
    assert (this.oldEventType != RealVarEvent.EMPTYEVENT);
    int oldCause = this.cause;

    // Traitement des restarations
    this.cause = VarEvent.NOEVENT;
    this.eventType = RealVarEvent.EMPTYEVENT;
    if (BitSet.getBit(this.oldEventType, RESTINF))
      propagateRestInfEvent(oldCause);
    if (BitSet.getBit(this.oldEventType, RESTSUP))
      propagateRestSupEvent(oldCause);

    // On sauvegarde l'etat apres le traitement des restaurations
    int newEventType = this.eventType;
    int newCause = this.cause;
    if (this.eventType != RealVarEvent.EMPTYEVENT) {
      this.modifiedVar.getSolver().getPropagationEngine().getVarEventQueue().remove(this);
    }

    // On fait comme si Palm n'etait pas la pour Choco
    this.eventType = this.oldEventType & 31;
    this.cause = oldCause;

    // On laisse Choco propager
    boolean ret = super.propagateEvent();

    // On retablit des valeurs consistantes avec les resultats obtenus avec Palm
    if (this.eventType == RealVarEvent.EMPTYEVENT && newEventType != RealVarEvent.EMPTYEVENT) {
      this.modifiedVar.getSolver().getPropagationEngine().getVarEventQueue().pushEvent(this);
    }
    this.eventType |= newEventType;
    if (cause == VarEvent.NOEVENT) {
      this.cause = newCause;
    } else if (newCause != VarEvent.NOEVENT) {
      this.cause = VarEvent.NOCAUSE;
    }

    // On reinitialise l'evenement puisqu'il n'y a pas eu de contradiction !
    isPopping = false;

    // Rappel : ^ = xor logique
    assert (this.eventType == RealVarEvent.EMPTYEVENT ^ ((PalmVarEventQueue) this.modifiedVar.getSolver().getPropagationEngine().getVarEventQueue()).contains(this));

    return ret;
  }

  /**
   * Propagates the lower bound restoration event.
   */

  public void propagateRestInfEvent(int evtCause) throws ContradictionException {
    AbstractVar v = getModifiedVar();
    PartiallyStoredVector constraints = v.getConstraintVector();
    PartiallyStoredIntVector indices = v.getIndexVector();

    DisposableIntIterator cit = constraints.getIndexIterator();
    for (; cit.hasNext();) {
      int idx = cit.next();
      if (idx != evtCause) {
        PalmRealVarListener c = (PalmRealVarListener) constraints.get(idx);
        if (c.isActive()) {
          int i = indices.get(idx);
          c.awakeOnRestoreInf(i);
        }
      }
    }
  }

  /**
   * Propagates the upper bound restoration event.
   */

  public void propagateRestSupEvent(int evtCause) throws ContradictionException {
    AbstractVar v = getModifiedVar();
    PartiallyStoredVector constraints = v.getConstraintVector();
    PartiallyStoredIntVector indices = v.getIndexVector();

      DisposableIntIterator cit = constraints.getIndexIterator();
    for (;cit.hasNext();) {
      int idx = cit.next();
      if (idx != evtCause) {
        PalmRealVarListener c = (PalmRealVarListener) constraints.get(idx);
        if (c.isActive()) {
          int i = indices.get(idx);
          c.awakeOnRestoreSup(i);
        }
      }
    }
  }

  /**
   * Updates variable states after restoration (like explanation or decision constraints for instance).
   */
  public void restoreVariableExplanation() {
    if (BitSet.getBit(this.eventType, RESTINF))
      ((PalmRealVar) this.getModifiedVar()).resetExplanationOnInf();
    if (BitSet.getBit(this.eventType, RESTSUP))
      ((PalmRealVar) this.getModifiedVar()).resetExplanationOnSup();
    ((PalmRealVar) this.getModifiedVar()).updateDecisionConstraints();
  }

  /**
   * States if this event is currently being popped.
   */
  public boolean isPopping() {
    return this.isPopping;
  }

  /**
   * Specifies if this event is being popped.
   */
  public void setPopping(boolean b) {
    this.isPopping = b;
  }

  /**
   * Resets this event if interrupted during execution.
   */
  public void reset() {
    this.eventType = this.oldEventType;
    this.cause = VarEvent.NOCAUSE;

    assert (this.eventType != RealVarEvent.EMPTYEVENT);
  }
}
