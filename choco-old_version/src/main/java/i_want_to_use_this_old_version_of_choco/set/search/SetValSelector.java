package i_want_to_use_this_old_version_of_choco.set.search;

import i_want_to_use_this_old_version_of_choco.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/**
 * An interface for control objects that model a binary choice to an search value
 */
public interface SetValSelector {

  /**
   * returns the best value choice
   */
  public int getBestVal(SetVar x);
}
