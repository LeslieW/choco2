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

package choco.kernel.solver.branch;


/**
 * ObjectBranching objects are specific branching objects where each branch is labeled with an Object.
 * This is typically useful for palm control objects (where the label happens to be a List of Constraint).
 */
public interface ObjectBranching extends BranchingStrategy {
  /**
   * Computes decisions that can be taken on the specified item by the strategy.
   *
   * @param item The item the strategy branchs on.
   */
  Object selectFirstBranch(Object item);

  /**
   * When several decisions can be taken (for unsure extension for instance), this methos allows to
   * choose next decisions.
   *
   * @param branchingItem  the branching object under scrutiny
   * @param previousBranch the object labelling the previous branch
   * @return the object labelling the current branch
   */
  Object getNextBranch(Object branchingItem, Object previousBranch);

  /**
   * Checks whether all branches have already been explored at the current choice point
   *
   * @return true if no more branches can be generated
   */
  public boolean finishedBranching(Object item, Object previousBranch);
}
