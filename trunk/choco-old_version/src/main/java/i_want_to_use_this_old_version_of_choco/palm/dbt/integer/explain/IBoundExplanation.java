package i_want_to_use_this_old_version_of_choco.palm.dbt.integer.explain;

import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntVar;

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
