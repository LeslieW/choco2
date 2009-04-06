package i_want_to_use_this_old_version_of_choco.palm.cbj.explain;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class JumpConstraintPlugin implements ExplainedConstraintPlugin {

  protected int constraintIdx;

  protected AbstractConstraint touchedConstraint;

  public JumpConstraintPlugin(AbstractConstraint ct) {
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
