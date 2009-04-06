package i_want_to_use_this_old_version_of_choco.set.search;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.branch.AbstractIntBranching;
import i_want_to_use_this_old_version_of_choco.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public abstract class AbstractSetBranching extends AbstractIntBranching {

  public int getNextBranch(Object x, int i) {
    if (i == 1) return 2;
    return 0;
  }

  public boolean finishedBranching(Object x, int i) {

    return i == 2;
  }

  public void goDownBranch(Object x, int numBranch) throws ContradictionException {
    Object[] xx = (Object[]) x;
    Object var = xx[0];
    int val = ((Integer) xx[1]).intValue();
    super.goDownBranch(var, val);
    if (numBranch == 1) {
      SetVar y = (SetVar) var;
      //System.out.println("addToKer[" + y + "," + i + "]");
      y.setValIn(val);
      manager.problem.propagate();
    } else if (numBranch == 2) {
      SetVar y = (SetVar) var;
      //System.out.println("remFromEnv[" + y + "," + i + "]");
      y.setValOut(val);
      manager.problem.propagate();
    }
  }

  public void goUpBranch(Object x, int i, int numBranch) throws ContradictionException {
    super.goUpBranch(x, i);

  }

  /*
   * @deprecated replaced by the management incremental search (with a stack of BranchingTrace storing the
   *             environment (local variables) associated to each choice point
   */
  /*public boolean branchOn(Object x, int n) throws ContradictionException {
    AbstractGlobalSearchSolver algo = manager;
    AbstractProblem pb = algo.problem;
    boolean nodeSuccess = false;
    boolean nodeFinished = false;
    int numBranch = getFirstBranch(x);
    algo.newTreeNode();
    try {
      do {
        boolean branchSuccess = false;
        try {
          //pb.getPropagationEngine().checkCleanState();
          pb.getEnvironment().worldPush();
          goDownBranch(x, numBranch);
          if (explore(n + 1)) {
            branchSuccess = true;
          }
        } catch (ContradictionException e) {
          ;
        }
        if (!branchSuccess) {
          pb.worldPop();
        }
        algo.endTreeNode();
        algo.postDynamicCut();
        goUpBranch(x, numBranch);
        if (branchSuccess) {
          nodeSuccess = true;
        }
        if (numBranch == 1) {
          numBranch = 2;
        } else {
          nodeFinished = true;
        }
      } while (!nodeSuccess && !nodeFinished);
    } catch (ContradictionException e) {
      nodeSuccess = false;
    }
    return nodeSuccess;
  }*/

}
