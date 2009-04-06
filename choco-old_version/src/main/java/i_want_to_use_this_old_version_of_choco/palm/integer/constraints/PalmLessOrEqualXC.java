//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.integer.constraints;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedProblem;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.integer.AbstractPalmUnIntConstraint;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntDomain;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntVar;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 25 ao?t 2003
 * Time: 17:11:59
 * To change this template use Options | File Templates.
 */
public class PalmLessOrEqualXC extends AbstractPalmUnIntConstraint {
  protected final int cste;

  public PalmLessOrEqualXC(IntDomainVar v0, int cste) {
    this.v0 = v0;
    this.cste = cste;
    this.hook = ((ExplainedProblem) this.getProblem()).makeConstraintPlugin(this);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public String toString() {
    return this.v0 + " <= " + this.cste;
  }

  public void propagate() throws ContradictionException {
    Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
    ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
    ((ExplainedIntVar) this.v0).updateSup(this.cste, this.cIdx0, expl);
  }

  public void awakeOnInf(int idx) {
  }

  public void awakeOnSup(int idx) {
  }

  public void awakeOnRem() {
  }

  public void awakeOnRestoreVal(int idx, int val) throws ContradictionException {
    if (val > this.cste) {
      Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
      ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
      ((ExplainedIntVar) this.v0).removeVal(val, this.cIdx0, expl);
    }
  }

  public Boolean isEntailed() {
    if (this.v0.getInf() <= this.cste)
      return Boolean.TRUE;
    else if (this.v0.getSup() > this.cste) return Boolean.FALSE;
    return null;
  }

  public boolean isSatisfied() {
    return this.v0.getVal() <= this.cste;
  }


  public Set whyIsTrue() {
    Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
    ((ExplainedIntVar) this.v0).self_explain(ExplainedIntDomain.SUP, expl);
    return expl.toSet();
  }

  public Set whyIsFalse() {
    Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
    ((ExplainedIntVar) this.v0).self_explain(ExplainedIntDomain.INF, expl);
    return expl.toSet();
  }
}
