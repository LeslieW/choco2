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
package choco.kernel.solver.search;

import choco.kernel.solver.branch.IntBranching;

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
