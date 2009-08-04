//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.variables.real;

import choco.kernel.common.util.objects.ConstraintCollection;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.variables.real.RealDomain;

/**
 * Interface for a PaLM real domain.
 */
public interface PalmRealDomain extends RealDomain, PalmRealInterval {
  /**
   * Lower bound of the domain should be restored to the specified value.
   */
  public void restoreInf(double newValue);

  /**
   * Upper bound of the domain should be restored to the specified value.
   */
  public void restoreSup(double newValue);

  /**
   * Updates lower bound explanations: removes all explanation that are no up-to-date anymore.
   */
  public void resetExplanationOnInf();

  /**
   * Updates upper bound explanations: removes all explanation that are no up-to-date anymore.
   */
  public void resetExplanationOnSup();

  /**
   * Returns current decisions constraints involving this domain/variable.
   */
  public ConstraintCollection getDecisionConstraints();

  /**
   * Adds a new decision constraint involving this domain/variable.
   */
  public void addDecisionConstraint(AbstractSConstraint cst);

  /**
   * Updates decision constraints: removes all removed decision constraints.
   */
  public void updateDecisionConstraints();
}
