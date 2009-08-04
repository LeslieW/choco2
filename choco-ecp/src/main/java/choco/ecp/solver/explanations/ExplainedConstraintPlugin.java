package choco.ecp.solver.explanations;

import choco.ecp.solver.propagation.ConstraintPlugin;


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
