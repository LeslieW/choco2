package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.branch.AbstractBinIntBranching;
import i_want_to_use_this_old_version_of_choco.branch.VarSelector;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

import java.util.logging.Level;

/**
 * A class for branching schemes that consider two branches:
 *   - one assigning a value to an IntVar (X == v)
 *   - and the other forbidding this assignment (X != v)
 */
public class AssignOrForbidIntVarVal extends AbstractBinIntBranching {
  private VarSelector varHeuristic;
  private ValSelector valSHeuristic;
  private VarValPairSelector pairHeuristic;

  public AssignOrForbidIntVarVal(VarSelector varHeuristic,
                                 ValSelector valSHeuristic) {
    this.varHeuristic = varHeuristic;
    this.valSHeuristic = valSHeuristic;
  }

  public AssignOrForbidIntVarVal(VarValPairSelector pairh) {
    this.pairHeuristic = pairh;
  }

  public String getDecisionLogMsg(int branchIndex) {
    switch(branchIndex) {
      case 1:
        return "==";
      case 2:
        return "!=";
      default:
        return "??";
    }
  }

  public void goDownBranch(Object x, int i) throws ContradictionException {
    super.goDownBranch(x, i);
    IntVarValPair p = (IntVarValPair) x;
    switch(i) {
      case 1:
        p.var.setVal(p.val);
        p.getProblem().propagate();
        break;
      case 2:
        p.var.remVal(p.val);
        p.getProblem().propagate();
        break;
    }
  }

  protected void logDownBranch(final Object x, final int i) {
    IntVarValPair p = (IntVarValPair) x;
    if (logger.isLoggable(Level.FINE)) {
      int n = manager.problem.getEnvironment().getWorldIndex();
      if (n <= manager.getLoggingMaxDepth()) {
        logger.log(Level.FINE, LOG_DOWN_MSG, new Object[]{new Integer(n),p.var,getDecisionLogMsg(i), new Integer(p.val)});
      }
    }
  }

  protected void logUpBranch(final Object x, final int i) {
    IntVarValPair p = (IntVarValPair) x;
    if (logger.isLoggable(Level.FINE)) {
      int n = manager.problem.getEnvironment().getWorldIndex();
      if (n <= manager.getLoggingMaxDepth()) {
        logger.log(Level.FINE, LOG_UP_MSG, new Object[]{new Integer(n + 1),p.var,getDecisionLogMsg(i), new Integer(p.val)});
      }
    }
  }

  public void goUpBranch(Object x, int i) throws ContradictionException {
    super.goUpBranch(x, i);
  }

  public Object selectBranchingObject() throws ContradictionException {
    if (pairHeuristic != null) {
      return pairHeuristic.selectVarValPair();
    } else {
      IntDomainVar v = (IntDomainVar) varHeuristic.selectVar();
      if (v == null) return null;
      else {
        int i = valSHeuristic.getBestVal(v);
        return new IntVarValPair(v, i);
      }
    }
  }
}
