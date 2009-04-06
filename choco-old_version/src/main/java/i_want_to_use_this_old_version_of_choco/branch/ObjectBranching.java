// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.branch;


/**
 * ObjectBranching objects are specific branching objects where each branch is labeled with an Object.
 * This is typically useful for palm control objects (where the label happens to be a List of Constraint).
 */
public interface ObjectBranching extends Branching {
  /**
   * Computes decisions that can be taken on the specified item by the solver.
   *
   * @param item The item the solver branchs on.
   */
  Object selectFirstBranch(Object item);

  /**
   * When several decisions can be taken (for unsure extension for instance), this methos allows to
   * choose next decisions.
   *
   * @param branchingItem  the branching object under scrutiny
   * @param previousBranch the object labelling the previous branch
   * @return the object labelling the current branch
   */
  Object getNextBranch(Object branchingItem, Object previousBranch);

  /**
   * Checks whether all branches have already been explored at the current choice point
   *
   * @return true if no more branches can be generated
   */
  public boolean finishedBranching(Object item, Object previousBranch);
}
