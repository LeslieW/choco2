package i_want_to_use_this_old_version_of_choco.palm.integer;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedVar;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;

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
