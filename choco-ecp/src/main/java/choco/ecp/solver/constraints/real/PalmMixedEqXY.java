package choco.ecp.solver.constraints.real;

import choco.cp.solver.constraints.real.MixedEqXY;
import choco.ecp.solver.PalmSolver;
import choco.ecp.solver.constraints.real.exp.PalmRealIntervalConstant;
import choco.ecp.solver.explanations.Explanation;
import choco.ecp.solver.explanations.dbt.PalmConstraintPlugin;
import choco.ecp.solver.variables.integer.dbt.PalmIntDomain;
import choco.ecp.solver.variables.integer.dbt.PalmIntVar;
import choco.ecp.solver.variables.real.PalmRealInterval;
import choco.ecp.solver.variables.real.PalmRealVar;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.real.RealVar;

import java.util.Set;

public class PalmMixedEqXY extends MixedEqXY implements PalmMixedConstraint {
  public PalmMixedEqXY(RealVar v0, IntDomainVar v1) {
    super(v0, v1);
    this.hook = new PalmConstraintPlugin(this);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  protected void updateIInf() throws ContradictionException {
    Explanation e = ((PalmSolver) getProblem()).makeExplanation();
    ((PalmRealVar) v0).self_explain(PalmRealInterval.INF, e);
    ((PalmConstraintPlugin) hook).self_explain(e);
    ((PalmIntVar) v1).updateInf((int) Math.ceil(v0.getInf()), cIdx1, e);
  }

  protected void updateISup() throws ContradictionException {
    Explanation e = ((PalmSolver) getProblem()).makeExplanation();
    ((PalmRealVar) v0).self_explain(PalmRealInterval.SUP, e);
    ((PalmConstraintPlugin) hook).self_explain(e);
    ((PalmIntVar) v1).updateSup((int) Math.floor(v0.getSup()), cIdx1, e);
  }

  protected void updateReal() throws ContradictionException {
    Explanation ei = ((PalmSolver) getProblem()).makeExplanation();
    Explanation es = ((PalmSolver) getProblem()).makeExplanation();
    ((PalmConstraintPlugin) hook).self_explain(ei);
    ((PalmConstraintPlugin) hook).self_explain(es);
    ((PalmIntVar) v1).self_explain(PalmIntDomain.INF, ei);
    ((PalmIntVar) v1).self_explain(PalmIntDomain.SUP, es);
    ((PalmRealVar) v0).intersect(new PalmRealIntervalConstant(v1.getInf(), v1.getSup(), ei, es), cIdx0);
  }

  public void awakeOnRemovals(int varIdx, DisposableIntIterator deltaDomain) throws ContradictionException {
    this.propagate();
  }

  public void awakeOnRestoreInf(int idx) throws ContradictionException {
    if (idx == 0)
      awakeOnInf(1);
    else
      awakeOnInf(0);
  }

  public void awakeOnRestoreSup(int idx) throws ContradictionException {
    if (idx == 0)
      awakeOnSup(1);
    else
      awakeOnSup(0);
  }

  public void awakeOnRestoreVal(int idx, int val) throws ContradictionException {
  }

  public void awakeOnRestoreVal(int idx, DisposableIntIterator it) throws ContradictionException {
    this.propagate();
  }

  public Set whyIsTrue() {
    return null;
  }

  public Set whyIsFalse() {
    return null;
  }

  public void updateDataStructuresOnConstraint(int idx, int select, int newValue, int oldValue) {
  }

  public void updateDataStructuresOnRestoreConstraint(int idx, int select, int newValue, int oldValue) {
  }

}
