package choco.ecp.solver.search;

import choco.ecp.solver.constraints.PalmSConstraint;


// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public interface SymbolicDecision extends PalmSConstraint {

  /**
   * Returns the number identifying the current branch.
   */

  public int getBranch();

}
