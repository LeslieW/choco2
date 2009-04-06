//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.dbt.integer;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.PalmVar;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.prop.PalmIntVarEvent;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntVar;

public class PalmIntVar extends IntDomainVarImpl implements PalmVar, ExplainedIntVar {

  public PalmIntVar(AbstractProblem pb, String name, int domainType, int inf, int sup) {
    super(pb, name, domainType, inf, sup);

    // Mise a jour de l'evenement
    this.event = null;   // force GC
    this.event = new PalmIntVarEvent(this);

    // Mise a jour du domaine
    domain = null;
    if (domainType == IntDomainVar.BITSET) {
      domain = new PalmBitSetIntDomain(this, inf, sup);
    } else {
      domain = new PalmIntervalIntDomain(this, inf, sup);
    }
  }

  public PalmIntVar(AbstractProblem pb, String name, int[] sortedValues) {
    super(pb, name, sortedValues);
    // Mise a jour de l'evenement
    this.event = null;   // force GC
    this.event = new PalmIntVarEvent(this);
    domain = null;
    domain = new PalmBitSetIntDomain(this, sortedValues);
  }

  public PalmIntVar(AbstractProblem pb, int domainType, int inf, int sup) {
    this(pb, "", domainType, inf, sup);
  }


  // Some delagation methods...

  public Constraint getDecisionConstraint(int val) {
    return ((PalmIntDomain) this.domain).getDecisionConstraint(val);
  }

  public Constraint getNegDecisionConstraint(int val) {
    return ((PalmIntDomain) this.domain).getNegDecisionConstraint(val);
  }

  public void resetExplanationOnInf() {
    ((PalmIntDomain) this.domain).resetExplanationOnInf();
  }

  public void resetExplanationOnSup() {
    ((PalmIntDomain) this.domain).resetExplanationOnSup();
  }

  public void resetExplanationOnVal(int value) {
    ((PalmIntDomain) this.domain).resetExplanationOnVal(value);
  }

  public void self_explain(int select, Explanation expl) {
    ((PalmIntDomain) this.domain).self_explain(select, expl);
  }

  public void self_explain(int select, int x, Explanation expl) {
    ((PalmIntDomain) this.domain).self_explain(select, x, expl);
  }

  public void restoreInf(int newValue) {
    ((PalmIntervalIntDomain) this.domain).restoreInf(newValue);
  }

  public void restoreSup(int newValue) {
    ((PalmIntervalIntDomain) this.domain).restoreSup(newValue);
  }

  public void restoreVal(int val) {
    ((PalmBitSetIntDomain) this.domain).restoreVal(val);
  }

  public boolean updateInf(int x, int idx, Explanation e) throws ContradictionException {
    return ((PalmIntDomain) this.domain).updateInf(x, idx, e);
  }

  public boolean updateSup(int x, int idx, Explanation e) throws ContradictionException {
    return ((PalmIntDomain) this.domain).updateSup(x, idx, e);
  }

  public boolean removeVal(int value, int idx, Explanation e) throws ContradictionException {
    return ((PalmIntDomain) this.domain).removeVal(value, idx, e);
  }

  public int[] getAllValues() {
    return ((PalmIntDomain) this.domain).getAllValues();
  }

  public boolean instantiate(int value, int idx, Explanation e) throws ContradictionException {
    boolean change = false;
    change |= this.updateInf(value, idx, (PalmExplanation) e.copy());
    change |= this.updateSup(value, idx, (PalmExplanation) e.copy());
    return change;
  }
}
