/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.search.integer.branching;

import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractLargeIntBranching;
import choco.kernel.solver.branch.VarSelector;
import choco.kernel.solver.search.integer.ValIterator;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

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

  @Override
public void goDownBranch(Object x, int i) throws ContradictionException {
    super.goDownBranch(x, i);
    IntDomainVarImpl y = (IntDomainVarImpl) x;
    y.setVal(i);
    //manager.model.propagate();
  }

  @Override
public void goUpBranch(Object x, int i) throws ContradictionException {
    super.goUpBranch(x, i);
    IntDomainVarImpl y = (IntDomainVarImpl) x;
    y.remVal(i);
    //manager.model.propagate();
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

}
