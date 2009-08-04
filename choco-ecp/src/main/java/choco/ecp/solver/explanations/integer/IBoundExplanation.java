package choco.ecp.solver.explanations.integer;

import choco.ecp.solver.variables.integer.dbt.PalmIntVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public interface IBoundExplanation {

  public int getPreviousValue();


  public PalmIntVar getVariable();


}
