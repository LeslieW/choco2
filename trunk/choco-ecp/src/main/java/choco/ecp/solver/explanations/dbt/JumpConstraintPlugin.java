package choco.ecp.solver.explanations.dbt;

import choco.ecp.solver.explanations.ExplainedConstraintPlugin;
import choco.ecp.solver.explanations.Explanation;
import choco.kernel.solver.constraints.AbstractSConstraint;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class JumpConstraintPlugin implements ExplainedConstraintPlugin {

  protected int constraintIdx;

  protected AbstractSConstraint touchedConstraint;

  public JumpConstraintPlugin(AbstractSConstraint ct) {
    touchedConstraint = ct;
  }

  public int getConstraintIdx() {
    return constraintIdx;
  }

  public void setConstraintIdx(int constraintIdx) {
    this.constraintIdx = constraintIdx;
  }

  public void self_explain(Explanation e) {
    e.add(this.touchedConstraint);
  }

  public void addListener() {
  }

  public void activateListener() {
  }

  public void deactivateListener() {
  }

}
