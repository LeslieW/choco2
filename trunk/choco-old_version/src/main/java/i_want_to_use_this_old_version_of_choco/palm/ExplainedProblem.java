package i_want_to_use_this_old_version_of_choco.palm;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public interface ExplainedProblem {


  /**
   * Factory to create explanation.
   * It offers the possibility to make another kind of explanation, only by extending PalmProblem
   *
   * @return the new explanation object
   */
  public Explanation makeExplanation();

  /**
   * Factory to create a constraint plugin
   *
   * @param ct
   * @return
   */
  public ExplainedConstraintPlugin makeConstraintPlugin(AbstractConstraint ct);


  /**
   * throws a contradiction with the corresponding explanation
   *
   * @param exp
   * @throws ContradictionException
   */
  public void explainedFail(Explanation exp) throws ContradictionException;

  /**
   * @param nb Constraint number (number affected when posting and stored
   *           in the variable plugin)
   * @return Returns the constraint
   */
  public AbstractConstraint getConstraintNb(int nb);

  /**
   * set an explanation to know why the problem is over-constrained.
   *
   * @param contradictionExplanation
   */
  public void setContradictionExplanation(Explanation contradictionExplanation);
}
