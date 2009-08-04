package choco.ecp.solver.search.dbt;

import choco.cp.solver.search.limit.TimeLimit;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class PalmTimeLimit extends TimeLimit {

  public PalmTimeLimit(AbstractGlobalSearchStrategy theSolver, int theLimit) {
    super(theSolver, theLimit);
  }

  public boolean endNode(AbstractGlobalSearchStrategy solver) {
    nb = (int) (System.currentTimeMillis() - starth);
    return (nb < nbMax);
  }

}
