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
import i_want_to_use_this_old_version_of_choco.integer.constraints.IntLinComb;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedProblem;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.PalmConstraint;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntDomain;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntVar;
import i_want_to_use_this_old_version_of_choco.palm.integer.PalmIntVarListener;
import i_want_to_use_this_old_version_of_choco.util.Arithm;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: rochart
 * Date: Jan 13, 2004
 * Time: 7:58:45 AM
 * To change this template use Options | File Templates.
 */
public class PalmIntLinComb extends IntLinComb implements PalmIntVarListener, PalmConstraint {
  public PalmIntLinComb(IntDomainVar[] vars, int[] coeffs, int nbPositive, int c, int linOperator) {
    super(vars, coeffs, nbPositive, c, linOperator);
    this.hook = ((ExplainedProblem) this.getProblem()).makeConstraintPlugin(this);
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    throw new Error("AwakeOnRem in IntLinComb should not be called");
    //this.propagate();
  }

  public void awakeOnVar(int idx) throws ContradictionException {
    throw new Error("Appel au awakeOnvar");
  }

  public void awakeOnRemovals(int idx, IntIterator it) throws ContradictionException {
    this.propagate();
  }

  public void awakeOnRestoreInf(int idx) throws ContradictionException {
    this.propagate();
  }

  public void awakeOnRestoreSup(int idx) throws ContradictionException {
    this.propagate();
  }

  public void awakeOnRestoreVal(int idx, int val) throws ContradictionException {
    throw new Error("AwakeOnRestoreVal in IntLinComb should not be called");
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

  public void takeIntoAccountStatusChange(int index) {
  }

  private Explanation explainVariablesLB() {
    Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
    ((ExplainedConstraintPlugin) this.hook).self_explain(expl);
    for (int i = 0; i < nbPosVars; i++) {
      ((ExplainedIntVar) this.vars[i]).self_explain(ExplainedIntDomain.INF, expl);
    }
    for (int i = nbPosVars; i < this.getNbVars(); i++) {
      ((ExplainedIntVar) this.vars[i]).self_explain(ExplainedIntDomain.SUP, expl);
    }
    return expl;
  }

  private Explanation explainVariablesUB() {
    Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
    ((ExplainedConstraintPlugin) this.hook).self_explain(expl);
    for (int i = 0; i < nbPosVars; i++) {
      ((ExplainedIntVar) this.vars[i]).self_explain(ExplainedIntDomain.SUP, expl);
    }
    for (int i = nbPosVars; i < this.getNbVars(); i++) {
      ((ExplainedIntVar) this.vars[i]).self_explain(ExplainedIntDomain.INF, expl);
    }
    return expl;
  }

  protected boolean propagateNewLowerBound(int mylb) throws ContradictionException {
    Explanation expl = this.explainVariablesLB();
    boolean anyChange = false;
    int nbVars = getNbVars();
    if (mylb > 0) {
      logger.finer("lb = " + mylb + " > 0 => fail");
      ((ExplainedProblem) this.getProblem()).explainedFail(expl);
    }
    int i;
    for (i = 0; i < nbPosVars; i++) {
      int newSupi = Arithm.divFloor(-(mylb), coeffs[i]) + vars[i].getInf();
      if (((ExplainedIntVar) vars[i]).updateSup(newSupi, cIndices[i], expl)) {
        logger.finer("SUP(" + vars[i].toString() + ") <= " + -(mylb) + "/" + coeffs[i] + " + " + vars[i].getInf() + " = " + newSupi);
        anyChange = true;
      }
    }
    for (i = nbPosVars; i < nbVars; i++) {
      int newInfi = Arithm.divCeil(mylb, -(coeffs[i])) + vars[i].getSup();
      if (((ExplainedIntVar) vars[i]).updateInf(newInfi, cIndices[i], expl)) {
        logger.finer("INF(" + vars[i].toString() + ") >= " + mylb + "/" + -(coeffs[i]) + " + " + vars[i].getSup() + " = " + newInfi);
        anyChange = true;
      }
    }
    return anyChange;
  }

  protected boolean propagateNewUpperBound(int myub) throws ContradictionException {
    Explanation expl = this.explainVariablesUB();
    boolean anyChange = false;
    int nbVars = getNbVars();
    if (myub < 0) {
      logger.finer("ub = " + myub + " < 0 => fail");
      ((ExplainedProblem) this.getProblem()).explainedFail(expl);
    }
    int i;
    for (i = 0; i < nbPosVars; i++) {
      int newInfi = Arithm.divCeil(-(myub), coeffs[i]) + vars[i].getSup();
      if (((ExplainedIntVar) vars[i]).updateInf(newInfi, cIndices[i], expl)) {
        logger.finer("INF(" + vars[i].toString() + ") >= " + -(myub) + "/" + coeffs[i] + " + " + vars[i].getSup() + " = " + newInfi);
        anyChange = true;
      }
    }
    for (i = nbPosVars; i < nbVars; i++) {
      int newSupi = Arithm.divFloor(myub, -(coeffs[i])) + vars[i].getInf();
      if (((ExplainedIntVar) vars[i]).updateSup(newSupi, cIndices[i], expl)) {
        logger.finer("SUP(" + vars[i].toString() + ") <= " + myub + "/" + -(coeffs[i]) + " + " + vars[i].getInf() + " = " + newSupi);
        anyChange = true;
      }
    }
    return anyChange;
  }

}
