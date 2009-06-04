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
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 26 ao?t 2003
 * Time: 17:10:01
 * To change this template use Options | File Templates.
 */
public class PalmEqualXYC extends AbstractPalmBinIntConstraint {
  protected final int cste;

  public PalmEqualXYC(IntDomainVar v0, IntDomainVar v1, int cste) {
    super(v0, v1);
    this.cste = cste;
    this.hook = ((ExplainedProblem) this.getProblem()).makeConstraintPlugin(this);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public String toString() {
    return this.v0 + " == " + this.v1 + " + " + this.cste;
  }

  public void propagate() throws ContradictionException {
    ExplainedProblem pb = (ExplainedProblem) this.getProblem();
    if (((ExplainedIntVar) this.v0).hasEnumeratedDomain()) {
      IntIterator it = ((ExplainedIntVar) this.v0).getDomain().getIterator();
      while (it.hasNext()) {
        int value = it.next();
        if (!(this.v1.canBeInstantiatedTo(value - this.cste))) {
          Explanation expl = pb.makeExplanation();
          ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
          ((ExplainedIntVar) this.v1).self_explain(ExplainedIntDomain.VAL, value - this.cste, expl);
          ((ExplainedIntVar) this.v0).removeVal(value, this.cIdx0, expl);
        }
      }
    } else {
      Explanation expl = pb.makeExplanation();
      ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
      ((ExplainedIntVar) this.v1).self_explain(ExplainedIntDomain.INF, expl);
      ((ExplainedIntVar) this.v0).updateInf(this.v1.getInf() + this.cste, this.cIdx0, expl);

      expl = pb.makeExplanation();
      ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
      ((ExplainedIntVar) this.v1).self_explain(ExplainedIntDomain.SUP, expl);
      ((ExplainedIntVar) this.v0).updateSup(this.v1.getSup() + this.cste, this.cIdx0, expl);
    }
    if (((ExplainedIntVar) this.v1).hasEnumeratedDomain()) {
      IntIterator it2 = ((ExplainedIntVar) this.v1).getDomain().getIterator();
      while (it2.hasNext()) {
        int value = it2.next();
        if (!(this.v0.canBeInstantiatedTo(value + this.cste))) {
          Explanation expl = pb.makeExplanation();
          ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
          ((ExplainedIntVar) this.v0).self_explain(ExplainedIntDomain.VAL, value + this.cste, expl);
          ((ExplainedIntVar) this.v1).removeVal(value, this.cIdx1, expl);
        }
      }
    } else {
      Explanation expl = pb.makeExplanation();
      expl = pb.makeExplanation();
      ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
      ((ExplainedIntVar) this.v0).self_explain(ExplainedIntDomain.INF, expl);
      ((ExplainedIntVar) this.v1).updateInf(this.v0.getInf() - this.cste, this.cIdx1, expl);

      expl = pb.makeExplanation();
      ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
      ((ExplainedIntVar) this.v0).self_explain(ExplainedIntDomain.SUP, expl);
      ((ExplainedIntVar) this.v1).updateSup(this.v0.getSup() - this.cste, this.cIdx1, expl);
    }
  }

  public void awakeOnInf(int idx) throws ContradictionException {
    ExplainedProblem pb = (ExplainedProblem) this.getProblem();
    if (idx == 0) {
      Explanation expl = pb.makeExplanation();
      ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
      ((ExplainedIntVar) this.v0).self_explain(ExplainedIntDomain.INF, expl);
      ((ExplainedIntVar) this.v1).updateInf(this.v0.getInf() - this.cste, this.cIdx1, expl);
    } else {
      Explanation expl = pb.makeExplanation();
      ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
      ((ExplainedIntVar) this.v1).self_explain(ExplainedIntDomain.INF, expl);
      ((ExplainedIntVar) this.v0).updateInf(this.v1.getInf() + this.cste, this.cIdx0, expl);
    }
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    ExplainedProblem pb = (ExplainedProblem) this.getProblem();
    if (idx == 0) {
      Explanation expl = pb.makeExplanation();
      ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
      ((ExplainedIntVar) this.v0).self_explain(ExplainedIntDomain.SUP, expl);
      ((ExplainedIntVar) this.v1).updateSup(this.v0.getSup() - this.cste, this.cIdx1, expl);
    } else {
      Explanation expl = pb.makeExplanation();
      ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
      ((ExplainedIntVar) this.v1).self_explain(ExplainedIntDomain.SUP, expl);
      ((ExplainedIntVar) this.v0).updateSup(this.v1.getSup() + this.cste, this.cIdx0, expl);
    }
  }

  public void awakeOnRestoreInf(int idx) throws ContradictionException {
    this.awakeOnInf(1);
    this.awakeOnInf(0);
  }

  public void awakeOnRestoreSup(int idx) throws ContradictionException {
    this.awakeOnSup(1);
    this.awakeOnSup(0);
  }

  public void awakeOnRem(int idx, int value) throws ContradictionException {
    ExplainedProblem pb = (ExplainedProblem) this.getProblem();
    if (idx == 0) {
      if (this.v1.canBeInstantiatedTo(value - this.cste)) {
        Explanation expl = pb.makeExplanation();
        ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
        ((ExplainedIntVar) this.v0).self_explain(ExplainedIntDomain.VAL, value, expl);
        ((ExplainedIntVar) this.v1).removeVal(value - this.cste, this.cIdx1, expl);
      }
    } else {
      if (this.v0.canBeInstantiatedTo(value + this.cste)) {
        Explanation expl = pb.makeExplanation();
        ((ExplainedConstraintPlugin) this.getPlugIn()).self_explain(expl);
        ((ExplainedIntVar) this.v1).self_explain(ExplainedIntDomain.VAL, value, expl);
        ((ExplainedIntVar) this.v0).removeVal(value + this.cste, this.cIdx0, expl);
      }
    }
  }

  // Warning : when a value is restored : it must be checked if the value can be removed again or not.
  // but if we come back from an empty domain, then it must be checked if the value make some other values impossible !
  public void awakeOnRestoreVal(int idx, int value) throws ContradictionException {
    propagate(); // we are forced to check all other values of the variable which may be in a inconsistent state with the restored one
  }

  public Boolean isEntailed() {
    if ((this.v0.getSup() < this.v1.getInf() + this.cste) || (this.v0.getInf() > this.v1.getSup() + this.cste))
      return Boolean.FALSE;
    else if ((this.v0.getInf() == this.v0.getSup()) && (this.v1.getInf() == this.v1.getSup()) &&
        (this.v0.getInf() == this.v1.getInf() + this.cste))
      return Boolean.TRUE;
    return null;
  }

  public boolean isSatisfied() {
    return this.v0.getVal() == (this.v1.getVal() + this.cste);
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