//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.real;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;
import i_want_to_use_this_old_version_of_choco.palm.real.constraints.PalmSplitLeft;
import i_want_to_use_this_old_version_of_choco.palm.real.constraints.PalmSplitRight;
import i_want_to_use_this_old_version_of_choco.real.exp.RealIntervalConstant;
import i_want_to_use_this_old_version_of_choco.real.var.RealVarImpl;

/**
 * Default implementation of PaLM real variables.
 */
public class PalmRealVarImpl extends RealVarImpl implements PalmRealVar {

  /**
   * Creates a variable with the specified bounds and the specified name.
   */
  public PalmRealVarImpl(AbstractProblem pb, String name, double a, double b) {
    super(pb, name, a, b);

    // Helps GC
    this.event = null;
    this.domain = null;

    // New Palm event and domain
    this.event = new PalmRealVarEvent(this);
    this.domain = new PalmRealDomainImpl(this, a, b);
  }

  /**
   * The name of the variable.
   */
  public String toString() {
    return name;
  }

  /**
   * Updates lower bound explanations: it removes not up-to-date explanations.
   */
  public void resetExplanationOnInf() {
    ((PalmRealDomain) this.domain).resetExplanationOnInf();
  }

  /**
   * Updates upper bound explanations: it removes not up-to-date explanations.
   */
  public void resetExplanationOnSup() {
    ((PalmRealDomain) this.domain).resetExplanationOnSup();
  }

  /**
   * Updates decision constraints on this variable: it removes all erased constraints.
   */
  public void updateDecisionConstraints() {
    ((PalmRealDomain) this.domain).updateDecisionConstraints();
  }

  /**
   * Lower bound of the domain should be restored to the specified value.
   */
  public void restoreInf(double newValue) {
    ((PalmRealDomain) this.domain).restoreInf(newValue);
  }

  /**
   * Upper bound of the domain should be restored to the specified value.
   */
  public void restoreSup(double newValue) {
    ((PalmRealDomain) this.domain).restoreSup(newValue);
  }

  /**
   * Merge explanation of the specified part of the domain to a constraint collection.
   */
  public void self_explain(int select, Explanation e) {
    ((PalmRealDomain) this.domain).self_explain(select, e);
  }

  public Constraint getDecisionConstraint(int val) {
    AbstractConstraint cst = null;
    if (val == 1)
      cst = new PalmSplitLeft(this, new RealIntervalConstant(this));
    else // val == 2
      cst = new PalmSplitRight(this, new RealIntervalConstant(this));
    PalmExplanation expl = (PalmExplanation) ((PalmRealDomain) this.getDomain()).getDecisionConstraints();
    if (expl.size() > 0) ((PalmConstraintPlugin) cst.getPlugIn()).setDepending(expl);
    return cst;
  }

  /**
   * Adds a new constraints, and makes it active if needed.
   */
  // TODO: this needs to become backtrackable
  public int addConstraint(Constraint c, int varIdx) {
    int idx;
    constraints.staticAdd(c);
    indices.staticAdd(varIdx);
    idx = constraints.size() - 1;
    return idx;
  }
}
