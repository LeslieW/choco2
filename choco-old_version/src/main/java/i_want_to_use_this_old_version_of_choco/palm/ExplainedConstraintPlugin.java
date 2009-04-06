package i_want_to_use_this_old_version_of_choco.palm;

import i_want_to_use_this_old_version_of_choco.prop.ConstraintPlugin;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public interface ExplainedConstraintPlugin extends ConstraintPlugin {

  /**
   * Computes the explain associated to this constraint.
   *
   * @param e The explanation on which this explain should be merged.
   */
  public void self_explain(Explanation e);

}
