package i_want_to_use_this_old_version_of_choco.palm.cbj.search;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.branch.VarSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.AssignVar;
import i_want_to_use_this_old_version_of_choco.integer.search.ValIterator;
import i_want_to_use_this_old_version_of_choco.integer.search.ValSelector;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.JumpProblem;
import i_want_to_use_this_old_version_of_choco.palm.cbj.explain.JumpExplanation;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/**
 * An variable assigning heuristic used by search algorithm.
 */
public class JumpAssignVar extends AssignVar {

  /**
   * Builds an assign variable heuristic.
   * @param varSel a variable selector
   * @param valHeuri a value iterator
   */
  public JumpAssignVar(final VarSelector varSel,
      final ValIterator valHeuri) {
    super(varSel, valHeuri);
  }

  /**
   * Builds an assign variable heuristic.
   * @param varSel a variable selector
   * @param valHeuri a value selector
   */
  public JumpAssignVar(final VarSelector varSel,
      final ValSelector valHeuri) {
    super(varSel, valHeuri);
  }

  /**
   * Actually posts the choice taken if this search tree node. Here the
   * variable will be instantiated to the value i.
   * @param x the variable involved in the choice
   * @param i the value chosen for this variable
   * @throws ContradictionException if a contradiction occurs due to this
   * choice
   */
  public void goDownBranch(final Object x, final int i) 
  throws ContradictionException {
    logDownBranch(x, i);
    ExplainedIntVar y = (ExplainedIntVar) x;
    Explanation exp = ((JumpProblem) manager.problem).
        makeExplanation(manager.problem.getWorldIndex() - 1);    
    y.instantiate(i, -1, exp);
    manager.problem.propagate();
  }

  /**
   * A previous choice is undone. Here the bad value is removed from the
   * domain and correctly explained.
   * @param x the involved variable 
   * @param i the bad value
   * @param e the explanation about inconsistancy
   * @throws ContradictionException if a contradiction occurs due to the
   * implied domain reduction
   */
  public void goUpBranch(final Object x, final int i, final Explanation e) 
  throws ContradictionException {
    logUpBranch(x, i);
    ExplainedIntVar y = (ExplainedIntVar) x;
    //if (((JumpExplanation)e).contains(manager.problem.getWorldIndex() + 2))
    ((JumpExplanation) e).delete(manager.problem.getWorldIndex());
    y.removeVal(i, -1, e);
    manager.problem.propagate();
  }
}
