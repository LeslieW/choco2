package choco.ecp.solver.variables;


import choco.ecp.solver.explanations.ExplainedVar;
import choco.kernel.solver.constraints.SConstraint;


public interface PalmVar extends ExplainedVar {

  /**
   * Returns the decision constraint for the ith branch on the current domain.
   */

  public SConstraint getDecisionConstraint(int val);
}
