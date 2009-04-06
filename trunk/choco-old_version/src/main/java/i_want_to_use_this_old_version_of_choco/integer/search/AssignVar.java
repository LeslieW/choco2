// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.branch.AbstractLargeIntBranching;
import i_want_to_use_this_old_version_of_choco.branch.VarSelector;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl;

public class AssignVar extends AbstractLargeIntBranching {
  private VarSelector varHeuristic;
  private ValIterator valHeuristic;
  private ValSelector valSHeuristic;
  protected ValueChooserWrapper wrapper;

  String[]  LOG_DECISION_MSG = new String[]{"=="};

  public AssignVar(VarSelector varSel, ValIterator valHeuri) {
    varHeuristic = varSel;
    valHeuristic = valHeuri;
    wrapper = new ValIteratorWrapper();
  }

  public AssignVar(VarSelector varSel, ValSelector valHeuri) {
    varHeuristic = varSel;
    valSHeuristic = valHeuri;
    wrapper = new ValSelectorWrapper();
  }

  /**
   * selecting the object under scrutiny (that object on which an alternative will be set)
   *
   * @return the object on which an alternative will be set (often  a variable)
   */
  public Object selectBranchingObject() throws ContradictionException {
    return varHeuristic.selectVar();
  }

  public boolean finishedBranching(Object x, int i) {
    return wrapper.finishedBranching(x, i);
  }

  public int getFirstBranch(Object x) {
    return wrapper.getFirstBranch(x);
  }

  public int getNextBranch(Object x, int i) {
    return wrapper.getNextBranch(x, i);
  }

  public void goDownBranch(Object x, int i) throws ContradictionException {
    super.goDownBranch(x, i);
    IntDomainVarImpl y = (IntDomainVarImpl) x;
    y.setVal(i);
    //manager.problem.propagate();
  }

  public void goUpBranch(Object x, int i) throws ContradictionException {
    super.goUpBranch(x, i);
    IntDomainVarImpl y = (IntDomainVarImpl) x;
    y.remVal(i);
    //manager.problem.propagate();
  }

  protected interface ValueChooserWrapper {
    public boolean finishedBranching(Object x, int i);

    public int getFirstBranch(Object x);

    public int getNextBranch(Object x, int i);
  }

  protected class ValIteratorWrapper implements ValueChooserWrapper {
    public boolean finishedBranching(Object x, int i) {
      return (!valHeuristic.hasNextVal((IntDomainVar) x, i));
    }

    public int getFirstBranch(Object x) {
      return valHeuristic.getFirstVal((IntDomainVar) x);
    }

    public int getNextBranch(Object x, int i) {
      return valHeuristic.getNextVal((IntDomainVar) x, i);
    }
  }

  protected class ValSelectorWrapper implements ValueChooserWrapper {
    public boolean finishedBranching(Object x, int i) {
      return ((IntDomainVar) x).getDomainSize() == 0;
    }

    public int getFirstBranch(Object x) {
      return valSHeuristic.getBestVal((IntDomainVar) x);
    }

    public int getNextBranch(Object x, int i) {
      return valSHeuristic.getBestVal((IntDomainVar) x);
    }
  }

  public String getDecisionLogMsg(int branchIndex) {
    return LOG_DECISION_MSG[0];
  }
}
