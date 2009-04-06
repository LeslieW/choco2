package i_want_to_use_this_old_version_of_choco.palm;


// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public interface ExplainedDomain {

  public void self_explain(int select, Explanation e);

  /**
   * Returns the original lower bound.
   */

  public int getOriginalInf();


  /**
   * Returns the original upper bound.
   */

  public int getOriginalSup();
}
