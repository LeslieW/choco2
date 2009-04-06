package i_want_to_use_this_old_version_of_choco.palm.search;

import i_want_to_use_this_old_version_of_choco.palm.PalmConstraint;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public interface SymbolicDecision extends PalmConstraint {

  /**
   * Returns the number identifying the current branch.
   */

  public int getBranch();

}
