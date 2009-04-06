package i_want_to_use_this_old_version_of_choco.palm.integer.constraints;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedProblem;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.integer.AbstractPalmTernIntConstraint;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntDomain;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntVar;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Administrateur
 * Date: 30 janv. 2004
 * Time: 11:04:19
 * To change this template use Options | File Templates.
 */
public class PalmElt2D extends AbstractPalmTernIntConstraint {

  /**
   * uses the cste slot: l[i + cste] = x
   * (ex: cste = 1 allows to use and index from 0 to length(l) - 1
   */

  protected int[][] lvals;
  int dim1;
  int dim2;

  /**
   * 2D Element constraint
   *
   * @param v0    index1
   * @param v1    index2
   * @param v2    valeur
   * @param lvals
   */
// On ne peut plus avoir d'offset
  public PalmElt2D(IntDomainVar v0, IntDomainVar v1, IntDomainVar v2, int[][] lvals, int dim1, int dim2) {
    super(v0,v1,v2);
    this.lvals = lvals;
    this.dim1 = dim1;
    this.dim2 = dim2;
    this.hook = ((ExplainedProblem) this.getProblem()).makeConstraintPlugin(this);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public void updateValueFromIndex() throws ContradictionException {
    int minVal = Integer.MAX_VALUE, maxVal = Integer.MIN_VALUE, val, k = 0, l = 0;
    boolean found = false;
    Explanation e = ((ExplainedProblem) this.getProblem()).makeExplanation();
    ((ExplainedConstraintPlugin) this.hook).self_explain(e);
    ((ExplainedIntVar) this.v0).self_explain(ExplainedIntDomain.DOM, e);
    ((ExplainedIntVar) this.v1).self_explain(ExplainedIntDomain.DOM, e);
    int[] idx1 = ((ExplainedIntVar) this.v0).getAllValues();
    int[] idx2 = ((ExplainedIntVar) this.v1).getAllValues();
    for (int i = 0; i < idx1.length; i++) {
      for (int j = 0; j < idx2.length; j++) {
        val = lvals[idx1[i]][idx2[j]];
        if (minVal > val) minVal = val;
        if (maxVal < val) maxVal = val;
      }
    }
    ((ExplainedIntVar) v2).updateSup(maxVal, this.cIdx2, (Explanation) e.copy());
    ((ExplainedIntVar) v2).updateInf(minVal, this.cIdx2, (Explanation) e.copy());

    //values = ((ExplainedIntVar) this.v0).getAllValues();
    int[] values = ((ExplainedIntVar) this.v2).getAllValues();  // TODO : remplacer par des it�rateurs
    idx1 = ((ExplainedIntVar) this.v0).getAllValues();
    idx2 = ((ExplainedIntVar) this.v1).getAllValues();
    // propagate on holes
    if (v2.hasEnumeratedDomain()) {
      for (int i = 0; i < values.length; i++) {  // on parcourt la valeur
        while (!found & k < idx1.length) {
          while (!found & l < idx2.length) {
            if (lvals[idx1[k]][idx2[l]] == values[i])
              found = true;
            l++;
          }
          l = 0;
          k++;
        }
        if (!found) ((ExplainedIntVar) v2).removeVal(values[i], this.cIdx2, (Explanation) e.copy());
        found = false;
        l = 0;
        k = 0;
      }
    }
  }

  public boolean testValueVarV0(int idx) {
    boolean ret = false;
    IntIterator domIt = v1.getDomain().getIterator();
    while (!ret & domIt.hasNext()) {
      ret = v2.canBeInstantiatedTo(lvals[idx][domIt.next()]);
    }
    return ret;
  }

  public boolean testValueVarV1(int idx) {
    boolean ret = false;
    IntIterator domIt = v0.getDomain().getIterator();
    while (!ret & domIt.hasNext()) {
      ret = v2.canBeInstantiatedTo(lvals[domIt.next()][idx]);
    }
    return ret;
  }

  public void updateIndexFromValue() throws ContradictionException {
    ExplainedProblem pb = (ExplainedProblem) this.getProblem();
    Explanation e = pb.makeExplanation();
    ((ExplainedConstraintPlugin) this.hook).self_explain(e);
    ((ExplainedIntVar) this.v2).self_explain(ExplainedIntDomain.DOM, e);
    int minFeasibleIndex1 = v0.getInf(), minFeasibleIndex2 = v1.getInf();
    int maxFeasibleIndex1 = v0.getSup(), maxFeasibleIndex2 = v1.getSup();
    int thecause1 = 0, thecause2 = 0;
    if (v0.getSup() > (dim1 - 1)) maxFeasibleIndex1 = dim1 - 1;
    if (v1.getSup() > (dim2 - 1)) maxFeasibleIndex2 = dim2 - 1;
    if (v2.hasEnumeratedDomain()) thecause1 = cIdx0;
    if (v2.hasEnumeratedDomain()) thecause2 = cIdx1;
    // update index1
    Explanation e1 = pb.makeExplanation();
    e1.merge(e);
    ((ExplainedIntVar) this.v1).self_explain(ExplainedIntDomain.DOM, e1);
    while ((minFeasibleIndex1 < (dim1 - 1)) & v0.canBeInstantiatedTo(minFeasibleIndex1) &
        !testValueVarV0(minFeasibleIndex1))
      minFeasibleIndex1++;
    ((ExplainedIntVar) v0).updateInf(minFeasibleIndex1, thecause1, (Explanation) e1.copy());
    while ((maxFeasibleIndex1 > 0) & v0.canBeInstantiatedTo(maxFeasibleIndex1) &
        !testValueVarV0(maxFeasibleIndex1))
      maxFeasibleIndex1--;
    ((ExplainedIntVar) v0).updateSup(maxFeasibleIndex1, thecause1, (Explanation) e1.copy());
    if (v0.hasEnumeratedDomain()) {
      for (int i = minFeasibleIndex1 + 1; i < maxFeasibleIndex1; i++) {
        if (v0.canBeInstantiatedTo(i) & !testValueVarV0(i)) {
          Explanation expl = pb.makeExplanation();
          ((ExplainedConstraintPlugin) this.hook).self_explain(expl);
          IntIterator domIt = v1.getDomain().getIterator();
          while (domIt.hasNext())
            ((ExplainedIntVar) this.v2).self_explain(ExplainedIntDomain.VAL, lvals[i][domIt.next()], expl);
          ((ExplainedIntVar) this.v1).self_explain(ExplainedIntDomain.DOM, expl);
          ((ExplainedIntVar) this.v0).removeVal(i, thecause1, expl);
        }
      }
    }
    // update index2
    Explanation e2 = pb.makeExplanation();
    e2.merge(e);
    ((ExplainedIntVar) this.v0).self_explain(ExplainedIntDomain.DOM, e2);
    while ((minFeasibleIndex2 < (dim2 - 1)) & v1.canBeInstantiatedTo(minFeasibleIndex2) &
        !testValueVarV1(minFeasibleIndex2))
      minFeasibleIndex2++;
    ((ExplainedIntVar) v1).updateInf(minFeasibleIndex2, thecause2, (Explanation) e2.copy());
    while ((maxFeasibleIndex1 > 0) & v1.canBeInstantiatedTo(maxFeasibleIndex2) &
        !testValueVarV1(maxFeasibleIndex2))
      maxFeasibleIndex2--;
    ((ExplainedIntVar) v1).updateSup(maxFeasibleIndex2, thecause2, (Explanation) e2.copy());
    if (v1.hasEnumeratedDomain()) {
      for (int i = minFeasibleIndex2 + 1; i < maxFeasibleIndex2; i++) {
        if (v1.canBeInstantiatedTo(i) & !testValueVarV1(i)) {
          Explanation expl = pb.makeExplanation();
          ((ExplainedConstraintPlugin) this.hook).self_explain(expl);
          IntIterator domIt = v0.getDomain().getIterator();
          while (domIt.hasNext())
            ((ExplainedIntVar) this.v2).self_explain(ExplainedIntDomain.VAL, lvals[domIt.next()][i], expl);
          ((ExplainedIntVar) this.v0).self_explain(ExplainedIntDomain.DOM, expl);
          ((ExplainedIntVar) this.v1).removeVal(i, thecause2, expl);
        }
      }
    }

  }

  public void propagate() throws ContradictionException {
    updateIndexFromValue();
    updateValueFromIndex();
  }

  public void awakeOnInf(int idx) throws ContradictionException {
    if (idx <= 2)
      updateValueFromIndex();
    else
      updateIndexFromValue();
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    if (idx <= 2)
      updateValueFromIndex();
    else
      updateIndexFromValue();
  }

  public void awakeOnRem(int idx, int val) throws ContradictionException {
    if (idx <= 2)
      updateValueFromIndex();
    else
      updateIndexFromValue();
  }

  public void awakeOnRestore(int idx) throws ContradictionException {
    Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
    ((ExplainedConstraintPlugin) this.hook).self_explain(expl);
    ((ExplainedIntVar) v0).updateInf(0, cIdx0, (Explanation) expl.copy());
    ((ExplainedIntVar) v0).updateSup(dim1 - 1, cIdx0, (Explanation) expl.copy());
    ((ExplainedIntVar) v1).updateInf(0, cIdx0, (Explanation) expl.copy());
    ((ExplainedIntVar) v1).updateSup(dim2 - 1, cIdx1, (Explanation) expl.copy());
    if (idx <= 2)
      updateIndexFromValue();  // on l'appelle � l'envers pour voir les valeurs qu'on peut remettre
    else
      updateValueFromIndex();
  }

  public void awakeOnRestoreInf(int idx) throws ContradictionException {
    awakeOnRestore(idx);
  }

  public void awakeOnRestoreSup(int idx) throws ContradictionException {
    awakeOnRestore(idx);
  }

  public void awakeOnRestoreVal(int idx, int val) throws ContradictionException {
    awakeOnRestore(idx);
  }

  public Boolean isEntailed() {
    if (Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop.const").isLoggable(Level.WARNING))
      Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop.const").warning("Not Yet implemented : NotEqual.isEntailed");
    return null;
  }

  public boolean isSatisfied() {
    return true;
  }

  public Set whyIsTrue() {
    if (Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop.const").isLoggable(Level.WARNING))
      Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop.const").warning("Not Yet implemented : NotEqual.whyIsTrue");
    return null;
  }

  public Set whyIsFalse() {
    if (Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop.const").isLoggable(Level.WARNING))
      Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop.const").warning("Not Yet implemented : NotEqual.whyIsFalse");
    return null;
  }
}
