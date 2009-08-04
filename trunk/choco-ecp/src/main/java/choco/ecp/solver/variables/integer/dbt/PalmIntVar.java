//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.variables.integer.dbt;

import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.ecp.solver.explanations.Explanation;
import choco.ecp.solver.explanations.dbt.PalmExplanation;
import choco.ecp.solver.propagation.dbt.PalmIntVarEvent;
import choco.ecp.solver.variables.PalmVar;
import choco.ecp.solver.variables.integer.ExplainedIntVar;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class PalmIntVar extends IntDomainVarImpl implements PalmVar, ExplainedIntVar {

  public PalmIntVar(Solver pb, String name, int domainType, int inf, int sup) {
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

  public PalmIntVar(Solver pb, String name, int[] sortedValues) {
    super(pb, name, IntDomainVar.BITSET, sortedValues);
    // Mise a jour de l'evenement
    this.event = null;   // force GC
    this.event = new PalmIntVarEvent(this);
    domain = null;
    domain = new PalmBitSetIntDomain(this, sortedValues);
  }

  public PalmIntVar(Solver pb, int domainType, int inf, int sup) {
    this(pb, "", domainType, inf, sup);
  }


  // Some delagation methods...

  public SConstraint getDecisionConstraint(int val) {
    return ((PalmIntDomain) this.domain).getDecisionConstraint(val);
  }

  public SConstraint getNegDecisionConstraint(int val) {
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

  public void restoreInf(int newValue) throws ContradictionException {
    ((PalmIntervalIntDomain) this.domain).restoreInf(newValue);
  }

  public void restoreSup(int newValue) throws ContradictionException {
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
    boolean change = this.updateInf(value, idx, (PalmExplanation) e.copy());
    change |= this.updateSup(value, idx, (PalmExplanation) e.copy());
    return change;
  }
}
