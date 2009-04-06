// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.search;

import i_want_to_use_this_old_version_of_choco.branch.IntBranching;

/**
 * A class for keeping a trace of the search algorithm, through an IntBranching
 * (storing the current branching object, as well as the label of the current branch)
 */
public class IntBranchingTrace {
  private IntBranching branching;
  private Object branchingObject;
  private int branchIndex;

  public IntBranchingTrace() {
  }

  public IntBranchingTrace(IntBranching ibranching) {
    branching = ibranching;
  }

  public void setBranching(IntBranching ibranching) {
    branching = ibranching;
  }

  public void setBranchingObject(Object branchingObj) {
    branchingObject = branchingObj;
  }

  public void setBranchIndex(int bIdx) {
    branchIndex = bIdx;
  }

  public IntBranching getBranching() {
    return branching;
  }

  public Object getBranchingObject() {
    return branchingObject;
  }

  public int getBranchIndex() {
    return branchIndex;
  }

  public void clear() {
    branching = null;
    branchingObject = null;
    branchIndex = -999;
  }

}
