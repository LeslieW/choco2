package choco.ecp.solver.search;

import choco.ecp.solver.propagation.ConstraintPlugin;
import choco.ecp.solver.search.dbt.DecisionSConstraint;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.PropagationEvent;

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
public abstract class AbstractDecision implements DecisionSConstraint {

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

  public SConstraint negate() {
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

  public AbstractSConstraint opposite() {
    throw new UnsupportedOperationException();
  }

  public boolean isEquivalentTo(SConstraint compareTo) {
    throw new UnsupportedOperationException();
  }


}

