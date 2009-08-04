//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.constraints.integer;

import choco.ecp.solver.constraints.AbstractPalmUnIntSConstraint;
import choco.ecp.solver.explanations.ExplainedConstraintPlugin;
import choco.ecp.solver.explanations.ExplainedSolver;
import choco.ecp.solver.explanations.Explanation;
import choco.ecp.solver.variables.integer.ExplainedIntDomain;
import choco.ecp.solver.variables.integer.ExplainedIntVar;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 25 ao?t 2003
 * Time: 16:36:29
 * To change this template use Options | File Templates.
 */
public class PalmGreaterOrEqualXC extends AbstractPalmUnIntSConstraint {
  protected final int cste;

  public PalmGreaterOrEqualXC(IntDomainVar v0, int cste) {
      super(v0);
    this.v0 = v0;
    this.cste = cste;
    this.hook = ((ExplainedSolver) this.getSolver()).makeConstraintPlugin(this);
  }

  public String toString() {
    return this.v0 + " >= " + this.cste;
  }

  public void propagate() throws ContradictionException {
    Explanation expl = ((ExplainedSolver) this.getSolver()).makeExplanation();
    ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
    ((ExplainedIntVar) this.v0).updateInf(this.cste, this.cIdx0, expl);
  }

  public void awakeOnInf(int idx) throws ContradictionException {
  }

  public void awakeOnSup(int idx) throws ContradictionException {
  }

  public void awakeOnRem() throws ContradictionException {
  }

  public void awakeOnRestoreVal(int idx, int val) throws ContradictionException {
    if (val < this.cste) {
      Explanation expl = ((ExplainedSolver) this.getSolver()).makeExplanation();
      ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
      ((ExplainedIntVar) this.v0).removeVal(val, this.cIdx0, expl);
    }
  }

  public Boolean isEntailed() {
    if (this.v0.getInf() >= this.cste)
      return Boolean.TRUE;
    else if (this.v0.getSup() < this.cste) return Boolean.FALSE;
    return null;
  }

  public boolean isSatisfied() {
    return this.v0.getVal() >= this.cste;
  }


  public Set whyIsTrue() {
    Explanation expl = ((ExplainedSolver) this.getSolver()).makeExplanation();
    ((ExplainedIntVar) this.v0).self_explain(ExplainedIntDomain.INF, expl);
    return expl.toSet();
  }

  public Set whyIsFalse() {
    Explanation expl = ((ExplainedSolver) this.getSolver()).makeExplanation();
    ((ExplainedIntVar) this.v0).self_explain(ExplainedIntDomain.SUP, expl);
    return expl.toSet();
  }
}
