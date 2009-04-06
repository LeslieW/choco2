package i_want_to_use_this_old_version_of_choco.palm.dbt;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedVar;


public interface PalmVar extends ExplainedVar {

  /**
   * Returns the decision constraint for the ith branch on the current domain.
   */

  public Constraint getDecisionConstraint(int val);
}
