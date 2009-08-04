// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package choco.ecp.solver.propagation;

/**
 * An interface for all objects that can be plugged to a constraint.
 */
public interface ConstraintPlugin {
  public void addListener();

  public void activateListener();

  public void deactivateListener();
}