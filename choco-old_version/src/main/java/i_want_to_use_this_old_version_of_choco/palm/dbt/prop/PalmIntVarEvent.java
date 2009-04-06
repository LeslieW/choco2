//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.dbt.prop;

import i_want_to_use_this_old_version_of_choco.AbstractVar;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Var;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.var.IntVarEvent;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredIntVector;
import i_want_to_use_this_old_version_of_choco.mem.PartiallyStoredVector;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmBitSetIntDomain;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntVar;
import i_want_to_use_this_old_version_of_choco.palm.integer.PalmIntVarListener;
import i_want_to_use_this_old_version_of_choco.prop.VarEvent;
import i_want_to_use_this_old_version_of_choco.util.BitSet;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

public class PalmIntVarEvent extends IntVarEvent implements PalmVarEvent {

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
   * Constant value associated to the value restoration prop.
   */

  public final static int RESTVAL = 7;


  /**
   * Creates an prop for the specified variable.
   *
   * @param var The variable this prop is reponsible for.
   */

  public PalmIntVarEvent(Var var) {
    super((AbstractVar) var);
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
    assert (this.oldEventType != EMPTYEVENT);
    int oldCause = this.cause;

    // Traitement des restarations
    this.cause = VarEvent.NOEVENT;
    this.eventType = EMPTYEVENT;
    if (BitSet.getBit(this.oldEventType, RESTINF))
      propagateRestInfEvent(oldCause);
    if (BitSet.getBit(this.oldEventType, RESTSUP))
      propagateRestSupEvent(oldCause);
    if (BitSet.getBit(this.oldEventType, RESTVAL))
      propagateRestValEvent(oldCause); //,getRestoreIterator());

    // On sauvegarde l'etat apres le traitement des restaurations
    int newEventType = this.eventType;
    int newCause = this.cause;
    if (this.eventType != EMPTYEVENT) {
      this.modifiedVar.getProblem().getPropagationEngine().getVarEventQueue().remove(this);
    }

    // On fait comme si Palm n'etait pas la pour Choco
    this.eventType = this.oldEventType & 31;
    this.cause = oldCause;

    // On laisse Choco propager
    boolean ret = super.propagateEvent();

    // On retablit des valeurs consistantes avec les resultats obtenus avec Palm
    if (this.eventType == EMPTYEVENT && newEventType != EMPTYEVENT) {
      this.modifiedVar.getProblem().getPropagationEngine().getVarEventQueue().pushEvent(this);
    }
    this.eventType |= newEventType;
    if (cause == NOEVENT) {
      this.cause = newCause;
    } else if (newCause != NOEVENT) {
      this.cause = NOCAUSE;
    }

    // On reinitialise l'evenement puisqu'il n'y a pas eu de contradiction !
    isPopping = false;
    if (((PalmIntVar) this.modifiedVar).hasEnumeratedDomain())
      ((PalmBitSetIntDomain) ((PalmIntVar) this.modifiedVar).getDomain()).releaseRepairDomain();

    // Rappel : ^ = xor logique
    assert (this.eventType == EMPTYEVENT ^ ((PalmVarEventQueue) this.modifiedVar.getProblem().getPropagationEngine().getVarEventQueue()).contains(this));

    return ret;
  }


  /**
   * Propagates the lower bound restoration event.
   */

  public void propagateRestInfEvent(int evtCause) throws ContradictionException {
    AbstractVar v = getModifiedVar();
    PartiallyStoredVector constraints = v.getConstraintVector();
    PartiallyStoredIntVector indices = v.getIndexVector();

    for (IntIterator cit = constraints.getIndexIterator(); cit.hasNext();) {
      int idx = cit.next();
      if (idx != evtCause) {
        PalmIntVarListener c = (PalmIntVarListener) constraints.get(idx);
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

    for (IntIterator cit = constraints.getIndexIterator(); cit.hasNext();) {
      int idx = cit.next();
      if (idx != evtCause) {
        PalmIntVarListener c = (PalmIntVarListener) constraints.get(idx);
        if (c.isActive()) {
          int i = indices.get(idx);
          c.awakeOnRestoreSup(i);
        }
      }
    }
  }

  /**
   * Propagates a value restoration event.
   */
  public void propagateRestValEvent(int evtCause) throws ContradictionException {
    AbstractVar v = getModifiedVar();
    PartiallyStoredVector constraints = v.getConstraintVector();
    PartiallyStoredIntVector indices = v.getIndexVector();

    for (IntIterator cit = constraints.getIndexIterator(); cit.hasNext();) {
      int idx = cit.next();
      if (idx != evtCause) {
        PalmIntVarListener c = (PalmIntVarListener) constraints.get(idx);
        if (c.isActive()) {
          int i = indices.get(idx);
          c.awakeOnRestoreVal(i, this.getRestoreIterator());
        }
      }
    }
  }


  /**
   * Updates explanations for the variable: when bounds are completely restored, the unrelevant explanations
   * are removed.
   */

  public void restoreVariableExplanation() { // TODO : should be renamed ? not only explanation...
    if (BitSet.getBit(this.eventType, RESTINF))
      ((PalmIntVar) this.getModifiedVar()).resetExplanationOnInf();
    if (BitSet.getBit(this.eventType, RESTSUP))
      ((PalmIntVar) this.getModifiedVar()).resetExplanationOnSup();
    // removal chain has to be checked to avoid inconsistent state after a value restoration
    // cf scenario 1
    if (((PalmIntVar) this.getModifiedVar()).hasEnumeratedDomain())
      ((PalmBitSetIntDomain) ((PalmIntVar) this.getModifiedVar()).getDomain()).checkRemovalChain();
  }


  /**
   * Returns an iterator on the chain containing all the restored values.
   */

  public IntIterator getRestoreIterator() {
    return ((PalmBitSetIntDomain) ((IntDomainVar) modifiedVar).getDomain()).getRepairIterator();
  }


  /**
   * If a contradiction occurs when the event is handled, the event is reinitialized.
   */

  public void reset() {
    // Il faut remettre le bon type d'evenement pour eviter de ne remettre cet evenement dans la queue alors
    // qu'il y ait deja. Cela peut entrainer des probleme de chaines cycliques pour la liste des valeurs
    // restaurees.
    this.eventType = this.oldEventType;
    this.cause = VarEvent.NOCAUSE;
    if (((IntDomainVar) this.modifiedVar).hasEnumeratedDomain()) {
      ((PalmBitSetIntDomain) ((IntDomainVar) this.modifiedVar).getDomain()).resetRemovalChain();
    }

    assert (this.eventType != EMPTYEVENT);
  }

  public boolean isPopping() {
    return this.isPopping;
  }

  public void setPopping(boolean b) {
    this.isPopping = b;
  }
}
