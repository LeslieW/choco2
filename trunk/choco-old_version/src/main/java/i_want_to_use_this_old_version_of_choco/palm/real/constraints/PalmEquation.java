//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran?ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.real.constraints;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedProblem;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntDomain;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntVar;
import i_want_to_use_this_old_version_of_choco.palm.dbt.prop.PalmEngine;
import i_want_to_use_this_old_version_of_choco.palm.dbt.search.PalmContradiction;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealInterval;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealVarListener;
import i_want_to_use_this_old_version_of_choco.palm.real.exp.PalmRealIntervalConstant;
import i_want_to_use_this_old_version_of_choco.real.RealExp;
import i_want_to_use_this_old_version_of_choco.real.RealInterval;
import i_want_to_use_this_old_version_of_choco.real.RealMath;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.real.constraint.Equation;
import i_want_to_use_this_old_version_of_choco.real.constraint.RealConstraint;
import i_want_to_use_this_old_version_of_choco.real.exp.RealIntervalConstant;

/**
 * Implements a constraint based on real expression that ensures that an equality
 * is true. It is based on revisited hull consistency and can use box consistency
 * for some variables.
 */
public class PalmEquation extends Equation implements RealConstraint, PalmRealVarListener {
  /**
   * Creates an equation with an equality wetween a real expression and the
   * constant insterval for the provided problem.
   * An expression instance should be associated to only one constraint.
   */
  public PalmEquation(AbstractProblem pb, RealVar[] collectedVars, RealExp exp, RealInterval cste) {
    super(pb, collectedVars, exp, cste);
    this.hook = new PalmConstraintPlugin(this);
    this.cste = cste;
  }

  /**
   * First propagation: update the explanation of the constant interval with
   * this constarint (since now we can be sure this constraint is posted).
   *
   * @throws ContradictionException
   */
  public void awake() throws ContradictionException {
    Explanation expl = ((PalmProblem) this.getProblem()).makeExplanation();
    ((PalmConstraintPlugin) this.hook).self_explain(expl);
    this.cste = new PalmRealIntervalConstant(cste.getInf(), cste.getSup(), (PalmExplanation) expl.copy(), expl);
    super.awake();
  }

  /**
   * On lower bound restoration, make the generic propagation called.
   *
   * @throws ContradictionException
   */
  public void awakeOnRestoreInf(int idx) throws ContradictionException {
    this.constAwake(false);
  }

  /**
   * On upper bound restoration, make the generic propagation called.
   *
   * @throws ContradictionException
   */
  public void awakeOnRestoreSup(int idx) throws ContradictionException {
    this.constAwake(false);
  }

  /**
   * Private method checking if a sub interval is consitent during box consistency
   * algorithm.
   *
   * @return
   */
  private boolean not_inconsistent(RealExp[] wx, Explanation expl) {
    ((PalmConstraintPlugin) this.hook).self_explain(expl);
    try {
      tighten(wx);
    } catch (PalmContradiction e) {
      PalmIntVar cause = (PalmIntVar) this.getProblem().getPropagationEngine().getContradictionCause();
      cause.self_explain(PalmIntDomain.DOM, expl);
      cause.restoreInf(1);
      cause.resetExplanationOnInf();
      ((PalmEngine) this.getProblem().getPropagationEngine()).setContradictory(false);
      return false;
    } catch (ContradictionException e) {
      System.err.println("Should not happen: Bug in PalmEquation !");
    }
    if (exp.getInf() > cste.getSup()) {
      ((PalmRealInterval) this.exp).self_explain(PalmRealInterval.INF, expl);
      return false;
    } else if (exp.getSup() < cste.getInf()) {
      ((PalmRealInterval) this.exp).self_explain(PalmRealInterval.SUP, expl);
      return false;
    }
    return true;
  }

  /**
   * Box consistency algorithm on one variable
   *
   * @throws ContradictionException
   */
  protected void bc(RealVar var, RealExp[] wx, RealExp[] wox) throws ContradictionException {
    RealInterval[] unexplored = new RealInterval[this.boxConsistencyDepth * 2];
    int[] depths = new int[this.boxConsistencyDepth * 2];
    int depth = 0;
    int idx = 0;
    boolean fin = false;

    double leftB = 0, rightB = 0;
    Explanation expOnInf, expOnSup;
    expOnInf = ((PalmProblem) this.getProblem()).makeExplanation();
    ((PalmRealInterval) var).self_explain(PalmRealInterval.INF, expOnInf);
    expOnSup = ((PalmProblem) this.getProblem()).makeExplanation();
    ((PalmRealInterval) var).self_explain(PalmRealInterval.SUP, expOnSup);
    RealInterval oldValue = new RealIntervalConstant(var);

    tighten(wox);

    // Left bound !
    while (!fin) {
      if (not_inconsistent(wx, expOnInf)) {
        if (this.boxConsistencyDepth <= depth) {
          leftB = var.getInf();
          rightB = var.getSup(); // Valeur provisoire
          fin = true;
        } else {
          RealInterval left = RealMath.firstHalf(var);
          RealInterval right = RealMath.secondHalf(var);

          var.silentlyAssign(left);
          depth++;
          unexplored[idx] = right;
          depths[idx] = depth;
          idx++;
        }
      } else if (idx != 0) {
        var.silentlyAssign(unexplored[--idx]);
        depth = depths[idx];
      } else {
        var.silentlyAssign(oldValue);
        var.intersect(oldValue);
        ((PalmRealInterval) var).self_explain(PalmRealInterval.SUP, expOnInf);
        ((ExplainedProblem) this.getProblem()).explainedFail(expOnInf);
      }
    }

    // Reversing not explored intervals (in order to avoid to check already checked parts of the search space.

    RealInterval[] tmp1 = new RealInterval[this.boxConsistencyDepth * 2];
    int[] tmp2 = new int[this.boxConsistencyDepth * 2];

    for (int i = 0; i < idx; i++) {
      int j = idx - i - 1;
      tmp1[i] = unexplored[j];
      tmp2[i] = depths[j];
    }

    unexplored = tmp1;
    depths = tmp2;

    // Right bound if needed
    if (idx != 0) {
      var.silentlyAssign(unexplored[--idx]);
      depth = depths[idx];
      fin = false;

      while (!fin) {
        if (not_inconsistent(wx, expOnSup)) {
          if (this.boxConsistencyDepth <= depth) {
            rightB = var.getSup();
            fin = true;
          } else {
            RealInterval left = RealMath.firstHalf(var);
            RealInterval right = RealMath.secondHalf(var);

            var.silentlyAssign(right);
            depth++;
            unexplored[idx] = left;
            depths[idx] = depth;
            idx++;
          }
        } else if (idx != 0) {
          var.silentlyAssign(unexplored[--idx]);
          depth = depths[idx];
        } else {
          fin = true;
        }
      }
    }

    // Propagation
    var.silentlyAssign(oldValue);
    var.intersect(new PalmRealIntervalConstant(leftB, rightB, expOnInf, expOnSup));
  }

  /**
   * No synchronous event handling (void method).
   */
  public void updateDataStructuresOnConstraint(int idx, int select, int newValue, int oldValue) {
  }

  /**
   * No synchronous event handling (void method).
   */
  public void updateDataStructuresOnRestoreConstraint(int idx, int select, int newValue, int oldValue) {
  }

  public void tighten(RealExp[] exps) throws ContradictionException {
    for (int i = 0; i < exps.length; i++) {
      RealExp exp = exps[i];
      exp.tighten();
      if (exp.getInf() > exp.getSup()) {
        Explanation e = ((PalmProblem) this.problem).makeExplanation();
        ((PalmConstraintPlugin) this.hook).self_explain(e);
        ((PalmRealInterval) exp).self_explain(PalmRealInterval.DOM, e);
        ((ExplainedProblem) this.getProblem()).explainedFail(e);
      }
    }
  }
}
