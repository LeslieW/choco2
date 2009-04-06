package i_want_to_use_this_old_version_of_choco.palm;

import i_want_to_use_this_old_version_of_choco.ConstraintCollection;
import i_want_to_use_this_old_version_of_choco.Propagator;
import i_want_to_use_this_old_version_of_choco.palm.search.SymbolicDecision;

import java.util.Set;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public interface Explanation extends ConstraintCollection {


  /**
   * Creates a set with all the constraints in the explain..
   *
   * @return The explain as a set.
   */

  public Set toSet();

  /**
   * Removes all constraints from this explanation.
   */

  public void empties();

  /**
   * return the i-th constraint of the explanation
   *
   * @param i : the number of the constraint to return
   */

  public Propagator getConstraint(int i);

  /**
   * return the nogood associated to the explanation
   */
  public SymbolicDecision[] getNogood();
}
