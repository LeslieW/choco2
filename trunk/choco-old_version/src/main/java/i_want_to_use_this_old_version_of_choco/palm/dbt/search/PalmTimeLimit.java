package i_want_to_use_this_old_version_of_choco.palm.dbt.search;

import i_want_to_use_this_old_version_of_choco.search.AbstractGlobalSearchSolver;
import i_want_to_use_this_old_version_of_choco.search.TimeLimit;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class PalmTimeLimit extends TimeLimit {

  public PalmTimeLimit(AbstractGlobalSearchSolver theSolver, int theLimit) {
    super(theSolver, theLimit);
  }

  public boolean endNode(AbstractGlobalSearchSolver solver) {
    nb = (int) (System.currentTimeMillis() - starth);
    return (nb < nbMax);
  }

}
