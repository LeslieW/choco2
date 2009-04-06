//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.dbt.search;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.branch.AbstractBranching;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;

import java.util.List;

/**
 * An extension algorithm for Palm solvers.
 */

public class PalmExtend extends PalmAbstractSolverTool {

  /**
   * A branching or a linked list of branchings for solving the problem.
   */

  protected PalmAbstractBranching branching;


  /**
   * Gets the branching used by this extension.
   */

  public PalmAbstractBranching getBranching() {
    return branching;
  }


  /**
   * Sets the branching used by this extension.
   */

  public void setBranching(PalmAbstractBranching branching) {
    this.branching = branching;
  }


  /**
   * Extension algorithm. In this default one, it selects a branchiong item. If an not null item is returned,
   * te propagates decisions on this item, else, it launchs the same algorithm with the next branching,
   * if available..
   *
   * @param branching
   * @throws ContradictionException
   */

  public void explore(PalmAbstractBranching branching) throws ContradictionException {
    Object item = branching.selectBranchingObject();
    if (item != null) {
      this.getManager().newTreeNode();
      ((PalmProblem) this.manager.getProblem()).propagateAllDecisionsConstraints((List) branching.selectFirstBranch(item));
    } else {
      AbstractBranching br = branching.getNextBranching();
      if (br != null) {
        this.explore((PalmAbstractBranching) br);
      } else {
        this.manager.setFinished(true);
      }
    }
  }
}
