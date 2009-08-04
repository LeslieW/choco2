package choco.ecp.solver.variables.integer;

import choco.ecp.solver.explanations.ExplainedVar;
import choco.ecp.solver.explanations.Explanation;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public interface ExplainedIntVar extends ExplainedVar, IntDomainVar {

  public void self_explain(int select, int x, Explanation expl);

  public boolean updateInf(int x, int idx, Explanation e) throws ContradictionException;

  public boolean updateSup(int x, int idx, Explanation e) throws ContradictionException;

  public boolean removeVal(int value, int idx, Explanation e) throws ContradictionException;

  public boolean instantiate(int value, int idx, Explanation e) throws ContradictionException;

  /**
   * @deprecated
   */
  public int[] getAllValues();

}
