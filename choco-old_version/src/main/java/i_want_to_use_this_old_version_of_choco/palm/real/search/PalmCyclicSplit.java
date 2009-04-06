//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.real.search;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.search.PalmAbstractBranching;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealDomain;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealVar;
import i_want_to_use_this_old_version_of_choco.palm.real.constraints.PalmSplitLeft;
import i_want_to_use_this_old_version_of_choco.real.exp.RealIntervalConstant;

import java.util.LinkedList;
import java.util.List;

/**
 * A default branching for continuous variables: each variable is choosen cyclicly.
 */
public class PalmCyclicSplit extends PalmAbstractBranching {
  /**
   * The index of the last splitted variable.
   */
  protected int current = -1;

  String[] LOG_DECISION_MSG = new String[]{""};

  /**
   * Returns the next variable to split.
   */
  public Object selectBranchingObject() throws ContradictionException {
    PalmProblem problem = ((PalmProblem) this.extender.getManager().getProblem());
    int nbvars = problem.getNbRealVars();
    if (nbvars == 0) return null;
    int start = current == -1 ? nbvars - 1 : current;
    int n = (current + 1) % nbvars;
    while (n != start && problem.getRealVar(n).isInstantiated()) {
      n = (n + 1) % nbvars;
    }
    if (problem.getRealVar(n).isInstantiated()) return null;
    current = n;
    return problem.getRealVar(n);
  }

  /**
   * Returns the decision constraint to add (w.r.t. a specified variable).
   */
  public Object selectFirstBranch(Object item) {
    List list = new LinkedList();
    PalmRealVar var = (PalmRealVar) item;
    AbstractConstraint cst = new PalmSplitLeft(var, new RealIntervalConstant(var));
    PalmExplanation expl = (PalmExplanation) ((PalmRealDomain) var.getDomain()).getDecisionConstraints();
    if (expl.size() > 0) ((PalmConstraintPlugin) cst.getPlugIn()).setDepending(expl);
    list.add(cst);
    return list;
  }

  /**
   * Checks if the constraints that should be posted are acceptable w.r.t. the learner component.
   * Not used here.
   */
  public boolean checkAcceptable(List csts) {
    throw (new UnsupportedOperationException());
  }

  /**
   * Learns from rejection: it allows to avoid to fail again for the same reason.
   * Not used here.
   */
  public void learnFromRejection() {
    throw (new UnsupportedOperationException());
  }

  /**
   * Returns the next decision constraints for a specified variable.
   * Not used here.
   */
  public Object getNextBranch(Object branchingItem, Object previousBranch) {
    throw (new UnsupportedOperationException());
  }

  public boolean finishedBranching(Object item, Object previousBranch) {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String getDecisionLogMsg(int branchIndex) {
    return LOG_DECISION_MSG[0];
  }
}
