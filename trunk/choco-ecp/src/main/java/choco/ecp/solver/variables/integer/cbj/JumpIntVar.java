package choco.ecp.solver.variables.integer.cbj;

import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.ecp.solver.explanations.Explanation;
import choco.ecp.solver.variables.integer.ExplainedIntDomain;
import choco.ecp.solver.variables.integer.ExplainedIntVar;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class JumpIntVar extends IntDomainVarImpl implements ExplainedIntVar {

  public JumpIntVar(Solver pb, String name, int domainType, int inf, int sup) {
    super(pb, name, domainType, inf, sup);
    // Mise a jour du domaine
    domain = null;
    if (domainType == IntDomainVar.BITSET) {
      domain = new JumpBitSetIntDomain(this, inf, sup);
    } else {
      domain = new JumpIntervalIntDomain(this, inf, sup);
    }
  }

  public JumpIntVar(Solver pb, String name, int[] sortedValues) {
    super(pb, name, IntDomainVar.BITSET, sortedValues);
    domain = new JumpBitSetIntDomain(this, sortedValues);
  }

  public void self_explain(int select, int x, Explanation expl) {
    ((ExplainedIntDomain) this.domain).self_explain(select, x, expl);
  }

  public void self_explain(int select, Explanation e) {
    ((ExplainedIntDomain) this.domain).self_explain(select, e);
  }

  public boolean updateInf(int x, int idx, Explanation e) throws ContradictionException {
    return ((ExplainedIntDomain) this.domain).updateInf(x, idx, e);
  }

  public boolean updateSup(int x, int idx, Explanation e) throws ContradictionException {
    return ((ExplainedIntDomain) this.domain).updateSup(x, idx, e);
  }

  public boolean removeVal(int value, int idx, Explanation e) throws ContradictionException {
    return ((ExplainedIntDomain) this.domain).removeVal(value, idx, e);
  }

  public boolean instantiate(int value, int idx, Explanation e) throws ContradictionException {
    boolean change = this.updateInf(value, idx, (Explanation) e.copy());
    change |= this.updateSup(value, idx, (Explanation) e.copy());
    return change;
  }

  public int[] getAllValues() {
    return ((ExplainedIntDomain) this.domain).getAllValues();
  }


}
