// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

/**
 * An interface for control objects that model a binary choice to an search value
 */
public interface ValSelector {
  /**
   * A method selecting the search value used for the alternative
   */
  int getBestVal(IntDomainVar x);
}
