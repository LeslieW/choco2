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

import choco.cp.solver.variables.real.RealVarImpl;
import choco.ecp.solver.constraints.real.PalmSplitLeft;
import choco.ecp.solver.constraints.real.PalmSplitRightS;
import choco.ecp.solver.explanations.Explanation;
import choco.ecp.solver.explanations.dbt.PalmConstraintPlugin;
import choco.ecp.solver.explanations.dbt.PalmExplanation;
import choco.ecp.solver.propagation.PalmRealVarEvent;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.real.RealIntervalConstant;
import choco.kernel.solver.variables.real.RealVar;

/**
 * Default implementation of PaLM real variables.
 */
public class PalmRealVarImpl extends RealVarImpl implements PalmRealVar {

  /**
   * Creates a variable with the specified bounds and the specified name.
   */
  public PalmRealVarImpl(Solver pb, String name, double a, double b) {
    super(pb, name, a, b, RealVar.BOUNDS);

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

  public SConstraint getDecisionConstraint(int val) {
    AbstractSConstraint cst = null;
    if (val == 1)
      cst = new PalmSplitLeft(this, new RealIntervalConstant(this));
    else // val == 2
      cst = new PalmSplitRightS(this, new RealIntervalConstant(this));
    PalmExplanation expl = (PalmExplanation) ((PalmRealDomain) this.getDomain()).getDecisionConstraints();
    if (expl.size() > 0) ((PalmConstraintPlugin) cst.getPlugIn()).setDepending(expl);
    return cst;
  }

  /**
   * Adds a new constraints, and makes it active if needed.
   */
  // TODO: this needs to become backtrackable
  public int addConstraint(SConstraint c, int varIdx) {
    int idx;
    constraints.staticAdd(c);
    indices.staticAdd(varIdx);
    idx = constraints.size() - 1;
    return idx;
  }
}
