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
import i_want_to_use_this_old_version_of_choco.palm.dbt.PalmVar;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntDomain;
import i_want_to_use_this_old_version_of_choco.palm.dbt.prop.PalmEngine;

import java.util.List;

public class PalmUnsureExtend extends PalmExtend {

  public void explore(PalmAbstractBranching branching) throws ContradictionException {
    Object item = branching.selectBranchingObject();
    if (item != null) {
      this.getManager().newTreeNode();
      ((PalmProblem) this.manager.getProblem()).propagateAllDecisionsConstraints((List) this.selectAuthorizedDecisions(branching, item));
    } else {
      AbstractBranching br = branching.getNextBranching();
      if (br != null) {
        this.explore((PalmAbstractBranching) br);
      } else {
        this.manager.setFinished(true);
      }
    }
  }

  /**
   * Check if a decision is valid (according to the learner)
   */
  public boolean checkAcceptable(List csts) {
    return true;
  }


  /**
   * check if the variable has been emptied
   */

  public void learnFromRejection(PalmAbstractBranching branching, Object item, Object decisionList) throws ContradictionException {
    if (branching.finishedBranching(item, decisionList)) {
      PalmExplanation expl = (PalmExplanation) ((PalmProblem) this.getManager().getProblem()).makeExplanation();
      ((PalmVar) item).self_explain(PalmIntDomain.DOM, expl);
      ((PalmEngine) this.getManager().getProblem().getPropagationEngine()).raisePalmFakeContradiction(expl);
    }
  }

  /*
   * Computes decisions authorized by the learner that can be taken on the specified item by the solver.
   * @param var The item the solver branchs on.
   */
  public Object selectAuthorizedDecisions(PalmAbstractBranching branching, Object item) throws ContradictionException {
    PalmLearn learner = this.getManager().getLearning();
    List decisionlist = (List) branching.selectFirstBranch(item);
    while (!checkAcceptable(decisionlist) | !learner.checkAcceptable(decisionlist)) {
      learnFromRejection(branching, item, decisionlist);
      decisionlist = (List) branching.getNextBranch(item, decisionlist);
    }
    return decisionlist;
  }

}
