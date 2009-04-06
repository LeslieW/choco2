package i_want_to_use_this_old_version_of_choco.palm.real.constraints;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntDomain;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntVar;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealInterval;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealVar;
import i_want_to_use_this_old_version_of_choco.palm.real.exp.PalmRealIntervalConstant;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.real.constraint.MixedEqXY;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

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
    Explanation e = ((PalmProblem) getProblem()).makeExplanation();
    ((PalmRealVar) v0).self_explain(PalmRealInterval.INF, e);
    ((PalmConstraintPlugin) hook).self_explain(e);
    ((PalmIntVar) v1).updateInf((int) Math.ceil(v0.getInf()), cIdx1, e);
  }

  protected void updateISup() throws ContradictionException {
    Explanation e = ((PalmProblem) getProblem()).makeExplanation();
    ((PalmRealVar) v0).self_explain(PalmRealInterval.SUP, e);
    ((PalmConstraintPlugin) hook).self_explain(e);
    ((PalmIntVar) v1).updateSup((int) Math.floor(v0.getSup()), cIdx1, e);
  }

  protected void updateReal() throws ContradictionException {
    Explanation ei = ((PalmProblem) getProblem()).makeExplanation();
    Explanation es = ((PalmProblem) getProblem()).makeExplanation();
    ((PalmConstraintPlugin) hook).self_explain(ei);
    ((PalmConstraintPlugin) hook).self_explain(es);
    ((PalmIntVar) v1).self_explain(PalmIntDomain.INF, ei);
    ((PalmIntVar) v1).self_explain(PalmIntDomain.SUP, es);
    ((PalmRealVar) v0).intersect(new PalmRealIntervalConstant(v1.getInf(), v1.getSup(), ei, es), cIdx0);
  }

  public void awakeOnRemovals(int varIdx, IntIterator deltaDomain) throws ContradictionException {
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

  public void awakeOnRestoreVal(int idx, IntIterator it) throws ContradictionException {
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
