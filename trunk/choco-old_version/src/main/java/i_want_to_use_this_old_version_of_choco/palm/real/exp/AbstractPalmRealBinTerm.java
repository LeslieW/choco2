//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, 
//                                   Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.real.exp;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ConstraintCollection;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedProblem;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.prop.PalmEngine;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealInterval;
import i_want_to_use_this_old_version_of_choco.real.RealExp;
import i_want_to_use_this_old_version_of_choco.real.RealInterval;
import i_want_to_use_this_old_version_of_choco.real.exp.AbstractRealBinTerm;

/**
 * Abstract implementation of a real binary term (like addition for instance).
 */
public abstract class AbstractPalmRealBinTerm extends AbstractRealBinTerm 
    implements PalmRealInterval {
  /**
   * Explanation of the last lower bound affectation.
   */
  protected Explanation explanationOnInf;

  /**
   * Explanation of the last upper bound affectation.
   */
  protected Explanation explanationOnSup;

  /**
   * Creates an abstract binary term with the two sub-expressions.
   * @param pb the problem of this constraint
   * @param exp1 the first expression operand
   * @param exp2 the second expression operand
   */
  public AbstractPalmRealBinTerm(final AbstractProblem pb,
      final RealExp exp1, final RealExp exp2) {
    super(pb, exp1, exp2);
    explanationOnInf = ((ExplainedProblem) pb).makeExplanation();
    explanationOnSup = ((ExplainedProblem) pb).makeExplanation();
  }

  /**
   * Updates the interval such that this interval is
   * included in the interval parameter.
   * @param interval The interval this expression should be included in
   * @param index    The index of the constraint responsible of this reduction
   * @throws ContradictionException if a domain becomes empty or if a
   * contradiction can be infered
   */
  public void intersect(final RealInterval interval, final int index)
  throws ContradictionException {
    if (interval.getInf() > inf.get()) {
      inf.set(interval.getInf());
      explanationOnInf.empties();
      ((PalmRealInterval) interval).self_explain(INF, explanationOnInf);
    }
    if (interval.getSup() < sup.get()) {
      sup.set(interval.getSup());
      explanationOnSup.empties();
      ((PalmRealInterval) interval).self_explain(SUP, explanationOnSup);
    }
    if (inf.get() > sup.get()) {
      PalmExplanation e = (PalmExplanation) 
      ((PalmProblem) this.getProblem()).makeExplanation();
      this.self_explain(DOM, e);
      ((PalmEngine) this.getProblem().getPropagationEngine())
      .raisePalmFakeContradiction(e);
    }
  }

  /**
   * Explains the state of this expression (lower/upper bounds or all domain).
   * @param select Specifies which part of the domain should be explained.
   * @param e      Specifies the explanation in which these explaining 
   * constraints should be added.
   */
  public void self_explain(final int select, final Explanation e) {
    switch (select) {
      case INF:
        e.merge((ConstraintCollection) this.explanationOnInf);
        break;
      case SUP:
        e.merge((ConstraintCollection) this.explanationOnSup);
        break;
      case DOM:
        e.merge((ConstraintCollection) this.explanationOnInf);
        e.merge((ConstraintCollection) this.explanationOnSup);
        break;
      default:
        throw new Error("Invalid request of explanation.");
    }
  }
}
