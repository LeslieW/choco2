package i_want_to_use_this_old_version_of_choco.real.search;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.branch.AbstractIntBranching;
import i_want_to_use_this_old_version_of_choco.branch.IntBranching;
import i_want_to_use_this_old_version_of_choco.integer.search.ValIterator;
import i_want_to_use_this_old_version_of_choco.real.RealMath;
import i_want_to_use_this_old_version_of_choco.real.RealVar;

import java.util.logging.Level;

/**
 * A binary branching assigning interval to subinterval.
 */

public class AssignInterval extends AbstractIntBranching implements IntBranching {
  protected RealVarSelector varSelector;
  protected ValIterator valIterator;
  String[] LOG_DECISION_MSG = new String[]{"in first half of", "in second half of"};

  public AssignInterval(RealVarSelector varSelector, ValIterator valIterator) {
    this.varSelector = varSelector;
    this.valIterator = valIterator;
  }

  public Object selectBranchingObject() throws ContradictionException {
    return varSelector.selectRealVar();
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
    int i = getFirstBranch(x);
    algo.newTreeNode();
    try {
      do {
        boolean branchSuccess = false;
        try {
          //pb.getPropagationEngine().checkCleanState();
          pb.getEnvironment().worldPush();
          goDownBranch(x, i);
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
        goUpBranch(x, i);
        if (branchSuccess) {
          nodeSuccess = true;
        }
        if (!finishedBranching(x, i)) {
          i = getNextBranch(x, i);
        } else {
          nodeFinished = true;
        }
      } while (!nodeSuccess && !nodeFinished);
    } catch (ContradictionException e) {
      nodeSuccess = false;
    }
    return nodeSuccess;
  }*/

  public void goDownBranch(Object x, int i) throws ContradictionException {
    super.goDownBranch(x, i);
    if (i == 1) {
      ((RealVar) x).intersect(RealMath.firstHalf((RealVar) x));
      manager.problem.propagate();
    } else if (i == 2) {
      ((RealVar) x).intersect(RealMath.secondHalf((RealVar) x));
      manager.problem.propagate();
    } else {
      if (logger.isLoggable(Level.SEVERE))
        logger.severe("!! Not a valid value for AssignInterval branching !!");
    }
  }

  public void goUpBranch(Object x, int i) throws ContradictionException {
    super.goUpBranch(x, i);
  }

  public int getFirstBranch(Object x) {
    return valIterator.getFirstVal((RealVar) x);
  }

  public int getNextBranch(Object x, int i) {
    return valIterator.getNextVal((RealVar) x, i);
  }

  public boolean finishedBranching(Object x, int i) {
    return !valIterator.hasNextVal((RealVar) x, i);
  }

  public String getDecisionLogMsg(int i) {
    if (i == 1) return LOG_DECISION_MSG[0];
    else if (i == 2) return LOG_DECISION_MSG[1];
    else return "";
  }

}
