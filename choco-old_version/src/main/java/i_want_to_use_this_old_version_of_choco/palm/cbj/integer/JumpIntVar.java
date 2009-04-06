package i_want_to_use_this_old_version_of_choco.palm.cbj.integer;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntDomain;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class JumpIntVar extends IntDomainVarImpl implements ExplainedIntVar {

  public JumpIntVar(AbstractProblem pb, String name, int domainType, int inf, int sup) {
    super(pb, name, domainType, inf, sup);
    // Mise a jour du domaine
    domain = null;
    if (domainType == IntDomainVar.BITSET) {
      domain = new JumpBitSetIntDomain(this, inf, sup);
    } else {
      domain = new JumpIntervalIntDomain(this, inf, sup);
    }
  }

  public JumpIntVar(AbstractProblem pb, String name, int[] sortedValues) {
    super(pb, name, sortedValues);
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
    boolean change = false;
    change |= this.updateInf(value, idx, (Explanation) e.copy());
    change |= this.updateSup(value, idx, (Explanation) e.copy());
    return change;
  }

  public int[] getAllValues() {
    return ((ExplainedIntDomain) this.domain).getAllValues();
  }


}
