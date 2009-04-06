package i_want_to_use_this_old_version_of_choco.palm;

import i_want_to_use_this_old_version_of_choco.Var;

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
