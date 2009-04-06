package i_want_to_use_this_old_version_of_choco.palm.real.constraints;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntVar;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealInterval;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealVar;
import i_want_to_use_this_old_version_of_choco.palm.real.exp.PalmRealIntervalConstant;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.real.constraint.MixedCstElt;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.Set;

/**
 * Let x be an integer variable with n values and v be a real variable. Given n constant values a1 to an,
 * this constraint ensures that:
 * <p/>
 * <code>x = i iff v = ai</code>
 * <p/>
 * a1... an sequence is supposed to be ordered (a1&lt;a2&lt;... an)
 */
public class PalmMixedCstElt extends MixedCstElt implements PalmMixedConstraint {

  public PalmMixedCstElt(RealVar v0, IntDomainVar v1, double[] values) {
    super(v0, v1, values);
    this.hook = new PalmConstraintPlugin(this);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public void awake() throws ContradictionException {
    // Ensures that integer domain is correct !
    Explanation e = ((PalmProblem) this.problem).makeExplanation();
    ((PalmConstraintPlugin) this.getPlugIn()).self_explain(e);
    ((PalmIntVar) v1).updateSup(values.length - 1, cIdx1, (PalmExplanation) e.copy());
    ((PalmIntVar) v1).updateInf(0, cIdx1, e);
    this.propagate();
  }

  public void awakeOnRemovals(int varIdx, IntIterator deltaDomain) throws ContradictionException {
    this.propagate();
  }

  public void updateIInf() throws ContradictionException {
    Explanation e = ((PalmProblem) this.problem).makeExplanation();
    ((PalmRealVar) v0).self_explain(PalmRealInterval.INF, e);
    ((PalmConstraintPlugin) this.getPlugIn()).self_explain(e);

    int inf = v1.getInf();
    while (values[inf] < v0.getInf()) {
      inf++;
    }
    ((PalmIntVar) v1).updateInf(inf, cIdx1, e);
  }

  public void updateISup() throws ContradictionException {
    Explanation e = ((PalmProblem) this.problem).makeExplanation();
    ((PalmRealVar) v0).self_explain(PalmRealInterval.SUP, e);
    ((PalmConstraintPlugin) this.getPlugIn()).self_explain(e);

    int sup = v1.getSup();
    while (values[sup] > v0.getSup()) {
      sup--;
    }
    ((PalmIntVar) v1).updateSup(sup, cIdx1, e);
  }

  public void updateReal() throws ContradictionException {
    Explanation inf = ((PalmProblem) this.problem).makeExplanation();
    Explanation sup = ((PalmProblem) this.problem).makeExplanation();
    ((PalmIntVar) v1).self_explain(PalmRealInterval.INF, inf);
    ((PalmConstraintPlugin) this.getPlugIn()).self_explain(inf);
    ((PalmIntVar) v1).self_explain(PalmRealInterval.SUP, sup);
    ((PalmConstraintPlugin) this.getPlugIn()).self_explain(sup);
    v0.intersect(new PalmRealIntervalConstant(values[v1.getInf()], values[v1.getSup()], inf, sup));
  }

  public void awakeOnRestoreInf(int idx) throws ContradictionException {
    if (idx == 0) {
      updateReal();
    } else {
      updateIInf();
      updateReal();
    }
  }

  public void awakeOnRestoreSup(int idx) throws ContradictionException {
    if (idx == 0) {
      updateReal();
    } else {
      updateISup();
      updateReal();
    }
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
