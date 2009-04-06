package i_want_to_use_this_old_version_of_choco.palm.cbj.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class JumpContradictionException extends ContradictionException {

  protected Explanation cause;

  public JumpContradictionException(AbstractProblem pb, Explanation exp) {
    super(pb);
    cause = exp;
  }

  public Explanation getExplanation() {
    return cause;
  }
}
