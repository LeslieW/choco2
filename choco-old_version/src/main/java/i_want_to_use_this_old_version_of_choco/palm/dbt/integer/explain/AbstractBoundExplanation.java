//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.dbt.integer.explain;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.GenericExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntVar;

public abstract class AbstractBoundExplanation extends GenericExplanation implements IBoundExplanation {
  int previousValue;
  PalmIntVar variable;

  public AbstractBoundExplanation(AbstractProblem pb) {
    super(pb);
  }

  public int getPreviousValue() {
    return previousValue;
  }

  public PalmIntVar getVariable() {
    return variable;
  }
}
