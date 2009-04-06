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

import i_want_to_use_this_old_version_of_choco.*;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntVar;
import i_want_to_use_this_old_version_of_choco.palm.dbt.search.PalmContradiction;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealVar;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealVarEvent;
import i_want_to_use_this_old_version_of_choco.prop.ChocEngine;
import i_want_to_use_this_old_version_of_choco.prop.VarEvent;

public class PalmEngine extends ChocEngine {
  /**
   * Indicates is a contradiction has be found.
   */

  private boolean contradictory;


  /**
   * A dummy variable. Useful for raising PalmFakeContradictions.
   */

  private PalmIntVar dummyVariable;


  /**
   * Constructs an engine with the specified problem.
   *
   * @param pb The problem to associate with this engine.
   */

  public PalmEngine(AbstractProblem pb) {
    super(pb);
    this.varEventQueue = null; // force le GC pour la queue cr??e
    this.varEventQueue = new PalmVarEventQueue();
    this.problem = pb;
  }


  /**
   * Resets all the events in the queue (no cause, and in the queue (no popping events anymore)).
   */

  public void resetEvents() {
    ((PalmVarEventQueue) this.varEventQueue).reset();
  }


  /**
   * Posts an inf bound restoration prop.
   *
   * @param v The variable on which the inf bound is restored.
   */

  public void postRestoreInf(PalmIntVar v) {
    postEvent(v, PalmIntVarEvent.NOCAUSE, PalmIntVarEvent.RESTINF);
  }


  /**
   * Posts a sup bound restoration prop.
   *
   * @param v The variable on which the sup bound is restored.
   */

  public void postRestoreSup(PalmIntVar v) {
    postEvent(v, PalmIntVarEvent.NOCAUSE, PalmIntVarEvent.RESTSUP);
  }


  /**
   * Posts an inf bound restoration prop.
   *
   * @param v The variable on which the inf bound is restored.
   */

  public void postRestoreInf(PalmRealVar v) {
    postEvent(v, PalmIntVarEvent.NOCAUSE, PalmRealVarEvent.RESTINF);
  }


  /**
   * Posts a sup bound restoration prop.
   *
   * @param v The variable on which the sup bound is restored.
   */

  public void postRestoreSup(PalmRealVar v) {
    postEvent(v, PalmIntVarEvent.NOCAUSE, PalmRealVarEvent.RESTSUP);
  }


  /**
   * Posts value restoration.
   *
   * @param v     The variable that should be modified.
   * @param value The value restored.
   */

  public void postRestoreVal(PalmIntVar v, int value) {
    postEvent(v, PalmIntVarEvent.NOCAUSE, PalmIntVarEvent.RESTVAL);
  }


  /**
   * Posts value removal. Needs to be overriden by Palm ?
   * @param v The modified variable.
   * @param x The removed value.
   * @param idx The index of the responsible constraint.
   */

  /*public void postRemoveVal(PalmIntVar v, int x, int idx) {
      postEvent(v,idx,PalmIntVarEvent.REMVAL);
      // TODO : ask naren
  }   */


  /**
   * Deletes all the events. <b>Sould be in Choco, not here !!</b>
   */

/*    public void flushEvents() {
        // c'est dans Choco !
        this.varEventQueue.flushEventQueue();
    }  */


  /**
   * Raises a Palm Contradiction caused by the specified variable.
   *
   * @param var The variable which is responsible of the contradiction.
   */

  public void raisePalmContradiction(Var var) throws ContradictionException {
    this.contradictionCause = var;
    this.contradictory = true;
    this.resetEvents();
    throw new PalmContradiction(var);
  }


  /**
   * Raises a fake Contradiction with the specified explain. Useful when the contradiction is not
   * due to only one domain.
   *
   * @param expl The explain of the contradiction.
   */

  public void raisePalmFakeContradiction(PalmExplanation expl) throws ContradictionException {
    if (dummyVariable == null) {
      dummyVariable = (PalmIntVar) ((PalmProblem) this.problem).makeBoundIntVar("*dummy*", 0, 1, false);
    }
    dummyVariable.updateInf(2, VarEvent.NOCAUSE, expl);
  }


  /**
   * Raise a System Contradiction, that is a Choco Contradiction, that means that no solution can be found
   * anymore without removing constraint with a level upper that <code>PalmProblem.MAX_RELAX_LEVEL</code>.
   *
   * @throws ContradictionException
   */

  public void raiseSystemContradiction() throws ContradictionException {
    this.contradictory = false;
    this.flushEvents();
    throw new ContradictionException(this.problem);
  }


  /**
   * Removes properly a constraint: the constraint is deactivated, and all depending filtering decisions are
   * undone.
   *
   * @param constraint The constraint to be removed.
   */

  public void remove(Propagator constraint) {
    PalmConstraintPlugin pi = (PalmConstraintPlugin) constraint.getPlugIn();
    pi.removeDependance();
    constraint.setPassive();
    pi.undo();
    this.restoreVariableExplanations();
  }


  /**
   * Removes several constraints.
   *
   * @param constraints An array with all the constraints to remove.
   */

  public void remove(Constraint[] constraints) {
    for (int i = 0; i < constraints.length; i++) {
      AbstractConstraint constraint = (AbstractConstraint) constraints[i];
      constraint.setPassive();
      ((PalmConstraintPlugin) constraint.getPlugIn()).undo();
    }
    this.restoreVariableExplanations();
  }


  /**
   * Updates explanations when constraints are removed.
   */

  private void restoreVariableExplanations() {
    ((PalmVarEventQueue) this.varEventQueue).restoreVariableExplanations();
  }


  /**
   * Checks if a contradiction has been reached.
   *
   * @return True if a contradiciton happened.
   */

  public boolean isContradictory() {
    return contradictory;
  }


  /**
   * Sets if a contradiciton has been reached.
   *
   * @param c The value to set.
   */

  public void setContradictory(boolean c) {
    this.contradictory = c;
  }


  /**
   * Resets the dummy variable.
   */

  public void resetDummyVariable() {
    this.dummyVariable = null;
  }
}
