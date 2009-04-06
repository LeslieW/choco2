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
import i_want_to_use_this_old_version_of_choco.palm.integer.AbstractPalmBinIntConstraint;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntDomain;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntVar;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 26 ao?t 2003
 * Time: 10:57:55
 * To change this template use Options | File Templates.
 */
public class PalmNotEqualXYC extends AbstractPalmBinIntConstraint {
  protected final int cste;

  public PalmNotEqualXYC(IntDomainVar v0, IntDomainVar v1, int cste) {
    super(v0, v1);
    this.cste = cste;
    this.hook = ((ExplainedProblem) this.getProblem()).makeConstraintPlugin(this);
  }

  public String toString() {
    return this.v0 + " !== " + this.v1 + " + " + this.cste;
  }

  public void propagate() throws ContradictionException {
    if (this.v0.hasEnumeratedDomain() && this.v1.hasEnumeratedDomain()) {
      if (this.v0.getDomainSize() == 1) {
        Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
        ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
        ((ExplainedIntVar) this.v0).self_explain(ExplainedIntDomain.DOM, expl);
        ((ExplainedIntVar) this.v1).removeVal(((ExplainedIntVar) this.v0).getInf() - this.cste, this.cIdx1, expl);
      }
      if (this.v1.getDomainSize() == 1) {
        Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
        ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
        ((ExplainedIntVar) this.v1).self_explain(ExplainedIntDomain.DOM, expl);
        ((ExplainedIntVar) this.v0).removeVal(((ExplainedIntVar) this.v1).getInf() + this.cste, this.cIdx0, expl);
      }
    } else {
      if (this.v0.getInf() == this.v0.getSup()) {
        Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
        ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
        ((ExplainedIntVar) this.v0).self_explain(ExplainedIntDomain.DOM, expl);
        if (this.v1.getInf() == this.v0.getInf() - this.cste) {
          ((ExplainedIntVar) this.v1).updateInf(this.v0.getInf() - this.cste + 1, this.cIdx1, expl);
        }
        if (this.v1.getSup() == this.v0.getInf() - this.cste) {
          ((ExplainedIntVar) this.v1).updateSup(this.v0.getInf() - this.cste - 1, this.cIdx1, (Explanation) expl.copy());
        }
      } else if (this.v1.getInf() == this.v1.getSup()) {
        Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
        ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
        ((ExplainedIntVar) this.v1).self_explain(ExplainedIntDomain.DOM, expl);
        if (this.v0.getInf() == this.v1.getInf() + this.cste) {
          ((ExplainedIntVar) this.v0).updateInf(this.v1.getInf() + this.cste + 1, this.cIdx0, expl);
        }
        if (this.v0.getSup() == this.v1.getInf() + this.cste) {
          ((ExplainedIntVar) this.v0).updateSup(this.v1.getInf() + this.cste - 1, this.cIdx0, (Explanation) expl.copy());
        }
      }
    }
  }

  public void awakeOnInf(int idx) throws ContradictionException {
    if ((idx == 0) && (this.v0.getInf() == this.v0.getSup())) {
      Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
      ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
      ((ExplainedIntVar) this.v0).self_explain(ExplainedIntDomain.DOM, expl);
      if (this.v1.getInf() == this.v0.getInf() - this.cste) {
        ((ExplainedIntVar) this.v1).updateInf(this.v0.getInf() - this.cste + 1, this.cIdx1, expl);
      }
      if (this.v1.getSup() == this.v0.getInf() - this.cste) {
        ((ExplainedIntVar) this.v1).updateSup(this.v0.getInf() - this.cste - 1, this.cIdx1, (Explanation) expl.copy());
      }
    } else if ((idx == 1) && (this.v1.getInf() == this.v1.getSup())) {
      Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
      ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
      ((ExplainedIntVar) this.v1).self_explain(ExplainedIntDomain.DOM, expl);
      if (this.v0.getInf() == this.v1.getInf() + this.cste) {
        ((ExplainedIntVar) this.v0).updateInf(this.v1.getInf() + this.cste + 1, this.cIdx0, expl);
      }
      if (this.v0.getSup() == this.v1.getInf() + this.cste) {
        ((ExplainedIntVar) this.v0).updateSup(this.v1.getInf() + this.cste - 1, this.cIdx0, (Explanation) expl.copy());
      }
    }
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    this.awakeOnInf(idx);
  }

  public void awakeOnRestoreInf(int idx) throws ContradictionException {
    this.awakeOnInf(idx);
  }

  public void awakeOnRestoreSup(int idx) throws ContradictionException {
    this.awakeOnInf(idx);
  }

  public void awakeOnRem(int idx, int val) throws ContradictionException {
    if (idx == 0) {
      if (this.v0.getDomainSize() == 1) {
        Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
        ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
        ((ExplainedIntVar) this.v0).self_explain(ExplainedIntDomain.DOM, expl);
        ((ExplainedIntVar) this.v1).removeVal(((ExplainedIntVar) this.v0).getInf() - this.cste, this.cIdx1, expl);
      }
    } else {
      if (this.v1.getDomainSize() == 1) {
        Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
        ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
        ((ExplainedIntVar) this.v1).self_explain(ExplainedIntDomain.DOM, expl);
        ((ExplainedIntVar) this.v0).removeVal(((ExplainedIntVar) this.v1).getInf() + this.cste, this.cIdx0, expl);
      }
    }
  }

  // Warning : when a value is restored : it must be checked if the value can be removed again or not.
  // but if we come back from an empty domain, then it must be checked if the value make some other values impossible !
  public void awakeOnRestoreVal(int idx, int val) throws ContradictionException {
    if (this.v1.getDomainSize() == 1) {
      Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
      ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
      ((ExplainedIntVar) this.v1).self_explain(ExplainedIntDomain.DOM, expl);
      ((ExplainedIntVar) this.v0).removeVal(((ExplainedIntVar) this.v1).getInf() + this.cste, this.cIdx0, expl);
    }
    if (this.v0.getDomainSize() == 1) {
      Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
      ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
      ((ExplainedIntVar) this.v0).self_explain(ExplainedIntDomain.DOM, expl);
      ((ExplainedIntVar) this.v1).removeVal(((ExplainedIntVar) this.v0).getInf() - this.cste, this.cIdx1, expl);
    }
  }

  public Boolean isEntailed() {
    if ((this.v0.getSup() < this.v1.getInf() + this.cste) || (this.v1.getSup() < this.v0.getInf() - this.cste))
      return Boolean.TRUE;
    else if ((this.v0.getInf() == this.v0.getSup()) && (this.v1.getInf() == this.v1.getSup())
        && (this.v0.getInf() == this.v1.getInf() + this.cste))
      return Boolean.FALSE;
    return null;
  }

  public boolean isSatisfied() {
    return this.v0.getVal() != (this.v1.getVal() + this.cste);
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
