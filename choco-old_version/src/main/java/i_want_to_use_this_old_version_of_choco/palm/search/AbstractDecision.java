package i_want_to_use_this_old_version_of_choco.palm.search;

import i_want_to_use_this_old_version_of_choco.*;
import i_want_to_use_this_old_version_of_choco.palm.dbt.search.DecisionConstraint;
import i_want_to_use_this_old_version_of_choco.prop.ConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.prop.PropagationEvent;
import i_want_to_use_this_old_version_of_choco.reified.AbstractReifiedConstraint;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 29 d�c. 2004
 * Time: 16:41:23
 * To change this template use File | Settings | File Templates.
 */

/**
 * A class representing a decision as a constraint (An adapter of
 * decision constraint). It is useful to represent decision in
 * a uniform way when the search algorithm performs assignments instead of
 * posting constraints (As done by the AssignVar branching).
 */
public abstract class AbstractDecision extends AbstractEntity implements DecisionConstraint {

  public AbstractDecision(AbstractProblem problem) {
    super(problem);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public ConstraintPlugin getPlugIn() {
    return null;
  }

  public void takeIntoAccountStatusChange(int index) {
    throw new UnsupportedOperationException();
  }

  public void updateDataStructuresOnConstraint(int idx, int select, int newValue, int oldValue) {
    throw new UnsupportedOperationException();
  }

  public void updateDataStructuresOnRestoreConstraint(int idx, int select, int newValue, int oldValue) {
    throw new UnsupportedOperationException();
  }

  public Constraint negate() {
    throw new UnsupportedOperationException();
  }

  public void addListener(boolean dynamicAddition) {
    throw new UnsupportedOperationException();
  }

  public void deactivateListener(int varIndex) {
    throw new UnsupportedOperationException();
  }

  public void deactivateListener() {
    throw new UnsupportedOperationException();
  }

  public void activateListener() {
    throw new UnsupportedOperationException();
  }


  public void setConstraintIndex(int i, int idx) {
    throw new UnsupportedOperationException();
  }


  public int getConstraintIdx(int idx) {
    throw new UnsupportedOperationException();
  }

  public void awake() throws ContradictionException {
    throw new UnsupportedOperationException();
  }

  public void awakeOnVar(int idx) throws ContradictionException {
    throw new UnsupportedOperationException();
  }

  public void propagate() throws ContradictionException {
    throw new UnsupportedOperationException();
  }

  public int getPriority() {
    throw new UnsupportedOperationException();
  }

  public boolean isActive() {
    throw new UnsupportedOperationException();
  }

  public void setActive() {
    throw new UnsupportedOperationException();
  }

  public void setPassive() {
    throw new UnsupportedOperationException();
  }

  public PropagationEvent getEvent() {
    throw new UnsupportedOperationException();
  }

  public Boolean isEntailed() {
    throw new UnsupportedOperationException();
  }

  public boolean isConsistent() {
    throw new UnsupportedOperationException();
  }

  public int getVarIdxInOpposite(int i) {
    throw new UnsupportedOperationException();
  }

  public AbstractConstraint opposite() {
    throw new UnsupportedOperationException();
  }

  public boolean isEquivalentTo(Constraint compareTo) {
    throw new UnsupportedOperationException();
  }

  public int assignIndices(AbstractReifiedConstraint root, int i, boolean dynamicAddition) {
    throw new UnsupportedOperationException();
  }


}

