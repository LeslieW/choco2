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

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.search.IntBranchingDecision;


/**
 * IntBranching objects are specific branching objects where each branch 
 * is labeled with an integer.
 * This is typically useful for choice points in search trees.
 */
 public interface IntBranching extends Branching {

  /**
   * Performs the action, 
   * so that we go down a branch from the current choice point.
   * @param decision the decision to apply.
   * @throws choco.kernel.solver.ContradictionException if a domain empties or a contradiction is
   * infered
   */
  void goDownBranch(IntBranchingDecision decision) throws ContradictionException;

  /**
   * Performs the action,
   * so that we go up the current branch to the father choice point.
   * @param decision the decision that has been set at the father choice point
   * @throws choco.kernel.solver.ContradictionException if a domain empties or a contradiction is
   * infered
   */
  void goUpBranch(IntBranchingDecision decision) throws ContradictionException;

  /**
   * compute the first decision by setting a branching value or modifying the branching object
   * @param x the current decision
   */
  void setFirstBranch(IntBranchingDecision decision);

  /**
   * compute the next decision by setting a branching value or modifying the branching object
   * @param x the current decision
   */
  void setNextBranch(IntBranchingDecision decision);

  /**
   * Checks whether all branches have already been explored at the
   * current choice point.
   * @param decision the last decision applied
   * @return true if no more branches can be generated
   */
  boolean finishedBranching(IntBranchingDecision decision);
  
  
  /**
   * The logging message associated with the current decision.
   * @param decision current decision
   * @return logging message.
   */
  String getDecisionLogMessage(IntBranchingDecision decision);

}

 
///**
// * IntBranching objects are specific branching objects where each branch 
// * is labeled with an integer.
// * This is typically useful for choice points in search trees.
// */
//public interface IntBranching extends Branching {
//
//  /**
//   * Performs the action, 
//   * so that we go down a branch from the current choice point.
//   * @param x the object on which the alternative is set
//   * @param i the label of the branch that we want to go down
//   * @throws choco.kernel.solver.ContradictionException if a domain empties or a contradiction is
//   * infered
//   */
//  void goDownBranch(Object x, int i) throws ContradictionException;
//
//  /**
//   * Performs the action,
//   * so that we go up the current branch to the father choice point.
//   * @param x the object on which the alternative has been set 
//   * at the father choice point
//   * @param i the label of the branch that has been travelled down 
//   * from the father choice point
//   * @throws choco.kernel.solver.ContradictionException if a domain empties or a contradiction is
//   * infered
//   */
//  void goUpBranch(Object x, int i) throws ContradictionException;
//
//  /**
//   * Computes the search index of the first branch of the choice point.
//   * @param x the object on which the alternative is set
//   * @return the index of the first branch
//   */
//  int getFirstBranch(Object x);
//
//  /**
//   * Computes the search index of the next branch of the choice point.
//   * @param x the object on which the alternative is set
//   * @param i the index of the current branch
//   * @return the index of the next branch
//   */
//  int getNextBranch(Object x, int i);
//
//  /**
//   * Checks whether all branches have already been explored at the
//   * current choice point.
//   * @param x the object on which the alternative is set
//   * @param i the index of the last branch
//   * @return true if no more branches can be generated
//   */
//  boolean finishedBranching(Object x, int i);
//
//  /**
//   * A method exploring the i-th branch of choice point.
//   * @param x the current branching object
//   * @param i the index of the branch
//   * @return true if the subtree below that branch lead to a solution or not
//   * @throws ContradictionException if a domain empties or a contradiction is
//   * infered
//   * @deprecated
//   */
//  //boolean branchOn(Object x, int i) throws ContradictionException;
//
//  /**
//   * A method launching the exploration of a subtree in order to satisfy
//   * the current goal.
//   * @param n current depth in the search tree
//   * @return true iff the search was successful
//   * @deprecated
//   */
//  //boolean explore(int n);
//}
