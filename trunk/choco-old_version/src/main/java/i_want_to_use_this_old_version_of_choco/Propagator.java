// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco;

import i_want_to_use_this_old_version_of_choco.prop.ConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.prop.PropagationEvent;
import i_want_to_use_this_old_version_of_choco.prop.VarEventListener;
import i_want_to_use_this_old_version_of_choco.reified.AbstractReifiedConstraint;

/**
 * An interface for all implementations of listeners.
 */
public interface Propagator extends VarEventListener, Constraint {

  /**
   * Returns the constraint plugin. Useful for extending the solver.
   * @return the constraint plugin
   */

  public ConstraintPlugin getPlugIn();


  /**
   * <i>Utility:</i>
   * Testing if all variables involved in the constraint are instantiated.
   * @return wehter all the variables are completly instantiated
   */

  public boolean isCompletelyInstantiated();


  /**
   * Forces a propagation of the constraint.
   * @param isInitialPropagation indicates if it is the initial propagation or not
   */

  public void constAwake(boolean isInitialPropagation);


  /**
   * <i>Propagation:</i>
   * Propagating the constraint for the very first time until local
   * consistency is reached.
   * @exception ContradictionException contradiction exception
   */

  public void awake() throws ContradictionException;


  /**
   * <i>Propagation:</i>
   * Propagating the constraint when the domain of a variable has been
   * modified (shrunk) since the last consistent state.
   */

  public void awakeOnVar(int idx) throws ContradictionException;


  /**
   * <i>Propagation:</i>
   * Propagating the constraint until local consistency is reached.
   * @exception ContradictionException contradiction exception
   */

  public void propagate() throws ContradictionException;


  /**
   * <i>Propagation:</i>
   * Accessing the priority level of the queue handling the propagation
   * of the constraint. Results range from 1 (most reactive, for listeners
   * with fast propagation algorithms) to 4 (most delayed, for listeners
   * with lengthy propagation algorithms).
   * @return the priority level of the queue handling the propagation of the constraint
   */

  public int getPriority();


  /**
   * Removes a constraint from the network.
   * Beware, this is a permanent removal, it may not be backtracked
   */

  public void delete();

  /**
   * Returns the constraint awake var associated with this constraint.
   * @return the constraint awake var associated with this constraint.
   */

  public PropagationEvent getEvent();

  /**
   * Checks whether the constraint is definitely satisfied, no matter what further restrictions
   * occur to the domain of its variables.
   * @return wether the constraint is entailed
   */
  public Boolean isEntailed();

  /**
   * tests if the constraint is consistent with respect to the current state of domains
   * @return wether the constraint is consistent
   */
  public boolean isConsistent();

  /**
   * performs the global numbering (wrt root) of the variables contained in the subtree this, starting from i
   *
   * @param root            the overall root constraint, for which the variables are numbered
   * @param i               the index that will assigned to the first variable in the subtree this (originally 0)
   * @param dynamicAddition whether the addition is undone automatically on backtracking
   * @return the index of the last variable in the subtree 
   */
  public int assignIndices(AbstractReifiedConstraint root, int i, boolean dynamicAddition);

}