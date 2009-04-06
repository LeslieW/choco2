// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.prop;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Entity;
import i_want_to_use_this_old_version_of_choco.Propagator;
import i_want_to_use_this_old_version_of_choco.Var;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.set.SetVar;

/**
 * An interface for all implementations of propagation engines.
 */
public interface PropagationEngine extends Entity {

  /**
   * Raising a contradiction with no cause.
   */

  public void raiseContradiction() throws ContradictionException;


  /**
   * Raising a contradiction with a cause.
   */

  public void raiseContradiction(Entity cause) throws ContradictionException;

  /**
   * Recording that there was no known cause for the last contradiction.
   */

  public void setNoContradictionCause();

  /**
   * Recording the cause of the last contradiction.
   */

  public void setContradictionCause(Entity cause);

  /**
   * Retrieving the cause of the last contradiction.
   */

  public Entity getContradictionCause();


  /**
   * Returns the next active var queue to propagate some events.
   * If it returns null, the propagation is finished.
   */

  public EventQueue getNextActiveEventQueue();

  /**
   * Removes all pending events (used when interrupting a propagation because
   * a contradiction has been raised)
   */
  public void flushEvents();

  /**
   * checking that the propagation engine remains in a proper state
   */
  public boolean checkCleanState();

  void postUpdateInf(IntDomainVar v, int idx);

  void postUpdateSup(IntDomainVar v, int idx);

  void postInstInt(IntDomainVar v, int idx);

  void postRemoveVal(IntDomainVar v, int x, int idx);

  void postUpdateInf(RealVar v, int idx);

  void postUpdateSup(RealVar v, int idx);

  void postRemEnv(SetVar v, int idx);

  void postAddKer(SetVar v, int idx);

  void postInstSet(SetVar v, int idx);

  /**
   * Generic method to post events. The caller is reponsible of basic event
   * type field: it should be meaningful for the the associate kind of event.
   * @param v The modified variable.
   * @param idx The index of the constraint which deduced the domain filtering.
   * @param basicEvt A integer specifying mdofication kind for the attached
   * event.
   */
  void postEvent(Var v, int idx, int basicEvt);

  boolean postConstAwake(Propagator constraint, boolean init);

  void registerEvent(ConstraintEvent event);

  VarEventQueue getVarEventQueue();
}
