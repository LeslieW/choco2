// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.prop;

import i_want_to_use_this_old_version_of_choco.*;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.var.IntVarEvent;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.real.var.RealVarEvent;
import i_want_to_use_this_old_version_of_choco.set.SetVar;
import i_want_to_use_this_old_version_of_choco.set.var.SetVarEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of an {@link i_want_to_use_this_old_version_of_choco.prop.AbstractPropagationEngine} for Choco.
 */
public class ChocEngine extends AbstractPropagationEngine {

  /**
   * Reference to object for logging trace statements related to propagation events (using the java.util.logging package)
   */

  private static Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop");

  /**
   * the number of queues for storing constraint events
   */
  protected static int NB_CONST_QUEUES = 4;

  /**
   * The different queues for the constraint awake events.
   */

  private ConstraintEventQueue[] constEventQueues;

  /**
   * Number of pending init constraint awake events.
   */

  protected int nbPendingInitConstAwakeEvent;

  /**
   * The queue with all the variable events.
   */

  protected VarEventQueue varEventQueue;


  /**
   * Constructs a new engine by initializing the var queues.
   */

  public ChocEngine(AbstractProblem pb) {
    super(pb);
    constEventQueues = new ConstraintEventQueue[NB_CONST_QUEUES];
    for (int i = 0; i < NB_CONST_QUEUES; i++) {
      constEventQueues[i] = new ConstraintEventQueue(this);
    }
    nbPendingInitConstAwakeEvent = 0;
    varEventQueue = new VarEventQueue();
  }


  /**
   * Posts an IncInf event
   *
   * @param v   The variable the bound is modified.
   * @param idx The index of the constraint which is responsible of the var.
   */

  public void postUpdateInf(IntDomainVar v, int idx) {
    postEvent(v, idx, IntVarEvent.INCINF);
  }

  /**
   * Posts a DecSup event
   *
   * @param v   The variable the bound is modified.
   * @param idx The index of the constraint which is responsible of the var.
   */

  public void postUpdateSup(IntDomainVar v, int idx) {
    postEvent(v, idx, IntVarEvent.DECSUP);
  }

  /**
   * Private method for completing the bound var posting.
   *
   * @param basicEvt The basic event posted.
   * @param idx      The index of the constraint which is responsible of the var.
   */
  // idee: - si on est "frozen", devenir en plus "redondant" (ie: double).
  //       - par ailleurs, noter le changement (garder la vieille valeur de la borne ou
  //       - devenir enqueued
  public void postEvent(Var v, int idx, int basicEvt) {
    VarEvent event = v.getEvent();
    if (logger.isLoggable(Level.FINEST))
      logger.finest("post Event " + event.toString() + " for basicEvt:" + basicEvt);
    /*event.setEventType(basicEvt);
    event.setCause(idx);*/
    boolean alreadyEnqueued = event.isEnqueued();
    event.recordEventTypeAndCause(basicEvt, idx);
    if (!alreadyEnqueued) {
      varEventQueue.pushEvent(event);
    } else {
      varEventQueue.updatePriority(event);
    }
    if (logger.isLoggable(Level.FINEST))
      logger.finest("posted Event " + event.toString());
  }

  /**
   * Posts an Inst var.
   *
   * @param v   The variable that is instantiated.
   * @param idx The index of the constraint which is responsible of the var.
   */

  public void postInstInt(IntDomainVar v, int idx) {
    postEvent(v, idx, IntVarEvent.INSTINT);
  }


  /**
   * Posts an Remove var.
   *
   * @param v   The variable the value is removed from.
   * @param idx The index of the constraint which is responsible of the var.
   */

  public void postRemoveVal(IntDomainVar v, int x, int idx) {
    postEvent(v, idx, IntVarEvent.REMVAL);
  }

  /**
   * Posts an lower bound event for a real variable.
   *
   * @param v
   * @param idx
   */
  public void postUpdateInf(RealVar v, int idx) {
    postEvent(v, idx, RealVarEvent.INCINF);
  }

  /**
   * Posts an upper bound event for a real variable
   *
   * @param v
   * @param idx
   */
  public void postUpdateSup(RealVar v, int idx) {
    postEvent(v, idx, RealVarEvent.DECSUP);
  }

  /**
   * Posts a removal event on a set variable
   *
   * @param v   the variable the enveloppe is modified
   * @param idx the index of the constraint that causes the event
   */
  public void postRemEnv(SetVar v, int idx) {
    postEvent(v, idx, SetVarEvent.REMENV);
  }

  /**
   * Posts a kernel addition event on a set variable
   *
   * @param v   the variable the kernel is modified
   * @param idx the index of the constraint that causes the event
   */
  public void postAddKer(SetVar v, int idx) {
    postEvent(v, idx, SetVarEvent.ADDKER);
  }

  /**
   * Posts an Inst event on a set var.
   *
   * @param v   The variable that is instantiated.
   * @param idx The index of the constraint which is responsible of the var.
   */

  public void postInstSet(SetVar v, int idx) {
    postEvent(v, idx, SetVarEvent.INSTSET);
  }

  /**
   * Posts a constraint awake var.
   *
   * @param constraint The constraint that must be awaken.
   * @param init       Specifies if the constraint must be initialized
   *                   (awake instead of propagate).
   */

  public boolean postConstAwake(Propagator constraint, boolean init) {
    ConstraintEvent event = (ConstraintEvent) constraint.getEvent();
    ConstraintEventQueue queue = this.getQueue(event);
    if (queue.pushEvent(event)) {
      event.setInitialized(!init);
      if (init) this.incPendingInitConstAwakeEvent();
      return true;
    } else
      return false;
  }


  /**
   * Gets the queue for a given priority of var.
   *
   * @param event The var for which the queue is searched.
   */

  public ConstraintEventQueue getQueue(ConstraintEvent event) {
    int prio = event.getPriority();
    if (prio < NB_CONST_QUEUES) {
      return constEventQueues[prio];
    } else {
      if (logger.isLoggable(Level.WARNING))
        logger.warning("wrong constraint priority. It should be between 0 and 3.");
      return constEventQueues[3];
    }
  }


  /**
   * Registers an event in the queue. It should be called before using the queue to add
   * the var in the available events of the queue.
   *
   * @param event
   */

  public void registerEvent(ConstraintEvent event) {
    ConstraintEventQueue queue = this.getQueue(event);
    queue.add(event);
  }


  /**
   * Returns the variable var queue.
   */

  public VarEventQueue getVarEventQueue() {
    return varEventQueue;
  }


  /**
   * Throws a contradiction without cause.
   *
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
   */

  public void raiseContradiction() throws ContradictionException {
    throw(new ContradictionException(this.getProblem()));
  }


  /**
   * Throws a contradiction with the specified cause.
   *
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
   */

  public void raiseContradiction(Entity cause) throws ContradictionException {
    throw(new ContradictionException(cause));
  }


  /**
   * Decrements the number of init constraint awake events.
   */

  public void decPendingInitConstAwakeEvent() {
    this.nbPendingInitConstAwakeEvent--;
  }


  /**
   * Increments the number of init constraint awake events.
   */

  public void incPendingInitConstAwakeEvent() {
    this.nbPendingInitConstAwakeEvent++;
  }


  /**
   * Returns the next constraint var queue from which an event should be propagated.
   */

  public EventQueue getNextActiveConstraintEventQueue() {
    for (int i = 0; i < NB_CONST_QUEUES; i++) {
      if (!this.constEventQueues[i].isEmpty()) return this.constEventQueues[i];
    }
    return null;
  }


  /**
   * Returns the next queue from which an event should be propagated.
   */

  public EventQueue getNextActiveEventQueue() {
    if (this.nbPendingInitConstAwakeEvent > 0) {
      return this.getNextActiveConstraintEventQueue();
    } else if (!this.varEventQueue.isEmpty()) {
      return this.varEventQueue;
    } else
      return this.getNextActiveConstraintEventQueue();
  }

  public int getNbPendingEvents() {
    int nbEvts = varEventQueue.size();
    for (int i = 0; i < NB_CONST_QUEUES; i++) {
      nbEvts += constEventQueues[i].size();
    }
    return nbEvts;
  }

  /**
   * getter without side effect:
   * returns the i-ht pending event (without popping any event from the queues)
   */
  public PropagationEvent getPendingEvent(int idx) {
    if (nbPendingInitConstAwakeEvent > 0) {
      idx += varEventQueue.size();
    }
    if (idx < varEventQueue.size()) {
      return varEventQueue.get(idx);
    } else {
      EventQueue q = varEventQueue;
      int qidx = 0;
      do {
        idx = idx - q.size();
        q = constEventQueues[qidx];
        qidx++;
      } while (idx > q.size() && qidx < NB_CONST_QUEUES);
      if (idx <= q.size()) {
        return q.get(idx);               // return an event from one of the constraint event queues
      } else if ((nbPendingInitConstAwakeEvent > 0) && (idx < varEventQueue.size())) {
        return varEventQueue.get(idx);   // return an event from the variable event queues
      } else {
        return null;              // return no event, as the index is greater than the total number of pending events
      }
    }
  }

  /**
   * Removes all pending events (used when interrupting a propagation because
   * a contradiction has been raised)
   */
  public void flushEvents() {
    for (int i = 0; i < NB_CONST_QUEUES; i++) {
      this.constEventQueues[i].flushEventQueue();
    }
    this.nbPendingInitConstAwakeEvent = 0;
    varEventQueue.flushEventQueue();
  }

  public boolean checkCleanState() {
    boolean ok = true;
    AbstractProblem pb = getProblem();
    int nbiv = pb.getNbIntVars();
    for (int i = 0; i < nbiv; i++) {
      IntVarEvent evt = (IntVarEvent) pb.getIntVar(i).getEvent();
      if (!(evt.getReleased())) {
        logger.severe("var event non released " + evt.toString());
        new Exception().printStackTrace();
        ok = false;
      }
    }
    int nbsv = pb.getNbSetVars();
    for (int i = 0; i < nbsv; i++) {
      SetVarEvent evt = (SetVarEvent) pb.getSetVar(i).getEvent();
      if (!(evt.getReleased())) {
        logger.severe("var event non released " + evt.toString());
        new Exception().printStackTrace();
        ok = false;
      }
    }
    return ok;
  }

}
