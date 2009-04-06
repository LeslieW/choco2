// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.branch;

public abstract class AbstractBinIntBranching extends AbstractIntBranching {
  public int getFirstBranch(Object x) {
    return 1;
  }

  public int getNextBranch(Object x, int i) {
    assert(i == 1);
    return 2;
  }

  public boolean finishedBranching(Object x, int i) {
    return (i == 2);
  }

  /*
   * @deprecated replaced by the management incremental search (with a stack of BranchingTrace storing the
   *             environment (local variables) associated to each choice point
   */
  /*public boolean explore(int n) {
    AbstractGlobalSearchSolver algo = manager;
    AbstractProblem pb = algo.problem;
    Object x = selectBranchingObject();
    if (null != x) {
      try {
        return branchOn(x, n);
      } catch (ContradictionException e) {
        return false;
      }
    } else if (null != nextBranching) {
      return ((IntBranching) nextBranching).explore(n);
    } else {
      algo.recordSolution();
      algo.showSolution();
      return algo.stopAtFirstSol;
    }
  } */
}
