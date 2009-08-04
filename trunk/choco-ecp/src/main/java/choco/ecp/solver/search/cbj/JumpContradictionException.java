package choco.ecp.solver.search.cbj;

import choco.ecp.solver.explanations.Explanation;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class JumpContradictionException extends ContradictionException {

  protected Explanation cause;

  public JumpContradictionException(Solver solver, Explanation exp) {
    super(solver);
    cause = exp;
  }

  public Explanation getExplanation() {
    return cause;
  }
}
