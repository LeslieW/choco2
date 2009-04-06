package i_want_to_use_this_old_version_of_choco.branch;

import i_want_to_use_this_old_version_of_choco.Constraint;

/**
 * A class that applies ta heuristic for selecting a constraint
 *  (which, in turn, can be used later to select a variable, by means of a CompositeIntVarSelector)
 */
public interface ConstraintSelector {
  public Constraint getConstraint();
}
