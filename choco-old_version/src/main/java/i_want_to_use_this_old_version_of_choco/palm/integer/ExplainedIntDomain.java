package i_want_to_use_this_old_version_of_choco.palm.integer;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomain;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedDomain;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public interface ExplainedIntDomain extends ExplainedDomain, IntDomain {
  int DOM = 0;
  int INF = 1;
  int SUP = 2;
  int VAL = 3;

  public void self_explain(int select, int x, Explanation expl);

  public boolean updateInf(int x, int idx, Explanation e) throws ContradictionException;

  public boolean updateSup(int x, int idx, Explanation e) throws ContradictionException;

  public boolean removeVal(int value, int idx, Explanation e) throws ContradictionException;

  /**
   * Returns all the value currently in the domain.
   */

  public int[] getAllValues();
}
