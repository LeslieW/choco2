package choco.ecp.solver.explanations;

import choco.kernel.solver.variables.Var;


// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public interface ExplainedVar extends Var {

  public void self_explain(int select, Explanation e);
}
