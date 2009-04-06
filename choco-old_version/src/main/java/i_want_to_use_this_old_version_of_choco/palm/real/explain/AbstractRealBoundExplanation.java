//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.real.explain;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.GenericExplanation;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealVar;

/**
 * Abstract implementation of a real bound explanation.
 */
public abstract class AbstractRealBoundExplanation extends GenericExplanation
    implements RealBoundExplanation {
  /**
   * Previous value of the variable bound.
   */
  protected double previousValue;

  /**
   * Touched variable.
   */
  protected PalmRealVar variable;

  /**
   * Creates such an explanation for the specified problem.
   */
  public AbstractRealBoundExplanation(AbstractProblem pb) {
    super(pb);
  }

  /**
   * Returns the previous value of the variable bound.
   */
  public double getPreviousValue() {
    return previousValue;
  }

  /**
   * Returns the touched variable (the variable which this explanations explains the domain).
   */
  public PalmRealVar getVariable() {
    return variable;
  }
}
