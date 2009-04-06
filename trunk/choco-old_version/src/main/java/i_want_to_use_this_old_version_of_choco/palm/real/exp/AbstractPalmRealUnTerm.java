package i_want_to_use_this_old_version_of_choco.palm.real.exp;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ConstraintCollection;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedProblem;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealInterval;
import i_want_to_use_this_old_version_of_choco.real.RealExp;
import i_want_to_use_this_old_version_of_choco.real.RealInterval;
import i_want_to_use_this_old_version_of_choco.real.exp.AbstractRealUnTerm;

/**
 * J-CHOCO
 * Copyright (C) F. Laburthe, 1999-2003
 * <p/>
 * An open-source Constraint Programming Kernel
 * for Research and Education
 * <p/>
 * Created by: Guillaume on 24 juin 2004
 */

/**
 * Abstract implementation of a real binary term (like addition for instance).
 */
public abstract class AbstractPalmRealUnTerm extends AbstractRealUnTerm implements PalmRealInterval {
  /**
   * PalmExplanation of the last lower bound affectation.
   */
  protected Explanation explanationOnInf;

  /**
   * PalmExplanation of the last upper bound affectation.
   */
  protected Explanation explanationOnSup;

  /**
   * Creates an abstract binary term with the two sub-expressions.
   */
  public AbstractPalmRealUnTerm(AbstractProblem pb, RealExp exp1) {
    super(pb, exp1);
    explanationOnInf = ((PalmProblem) pb).makeExplanation();
    explanationOnSup = ((PalmProblem) pb).makeExplanation();
  }

  /**
   * Updates the interval such that this interval is included in the interval parameter.
   *
   * @param interval The interval this expression should be included in.
   * @param index    The index of the constraint responsible of this reduction.
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
   */
  public void intersect(RealInterval interval, int index) throws ContradictionException {
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
      Explanation e = ((PalmProblem) this.getProblem()).makeExplanation();
      this.self_explain(DOM, e);
      ((ExplainedProblem) this.getProblem()).explainedFail(e);
    }
  }

  /**
   * Explains the state of this expression (lower/upper bounds or all domain)
   *
   * @param select Specifies which part of the domain should be explained.
   * @param e      Specifies the explanation in which these explaining constraints should be added.
   */
  public void self_explain(int select, ConstraintCollection e) {
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
    }
  }
}
