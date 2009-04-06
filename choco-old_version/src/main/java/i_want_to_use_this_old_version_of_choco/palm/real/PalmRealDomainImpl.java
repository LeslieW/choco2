//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.real;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ConstraintCollection;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.prop.PalmEngine;
import i_want_to_use_this_old_version_of_choco.palm.real.explain.RealBoundExplanation;
import i_want_to_use_this_old_version_of_choco.real.RealInterval;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.real.var.RealDomainImpl;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Default implementation of PaLM real domain.
 */
public class PalmRealDomainImpl extends RealDomainImpl implements PalmRealDomain {
  /**
   * A stack of explanations for lower bound modifications.
   */
  protected final LinkedList explanationOnInf;

  /**
   * A stack of explanations for upper bound modifications.
   */
  protected final LinkedList explanationOnSup;

  /**
   * Original lower bound.
   */
  protected final double originalInf;

  /**
   * Original upper bound.
   */
  protected final double originalSup;

  /**
   * All active decision constraints on this variable (except the last one).
   */
  protected PalmExplanation decisionConstraints;

  /**
   * Last decision constraint.
   */
  protected Constraint lastDC;

  /**
   * States if modfication should be quiet, that is no PaLM must be created when the bounds are
   * modified.
   */
  protected boolean silent = false;

  /**
   * Creates a real domain for the specified variable.
   */
  public PalmRealDomainImpl(RealVar v, double a, double b) {
    super(v, a, b);
    PalmProblem pb = (PalmProblem) this.getProblem();
    this.explanationOnInf = new LinkedList();
    this.explanationOnSup = new LinkedList();
    this.explanationOnInf.add(((PalmExplanation) pb.makeExplanation()).makeIncInfExplanation(this.getInf(), (PalmRealVar) this.variable));
    this.explanationOnSup.add(((PalmExplanation) pb.makeExplanation()).makeDecSupExplanation(this.getSup(), (PalmRealVar) this.variable));
    this.originalInf = a;
    this.originalSup = b;
    this.decisionConstraints = (PalmExplanation) pb.makeExplanation();
  }

  /**
   * Returns all decision constraints.
   */
  public ConstraintCollection getDecisionConstraints() {
    return decisionConstraints.copy();
  }

  /**
   * Adds a new decision constraint on this variable.
   */
  public void addDecisionConstraint(AbstractConstraint cst) {
    decisionConstraints.add(cst);
  }

  /**
   * Returns the original lower bound.
   */
  public double getOriginalInf() {
    return this.originalInf;
  }


  /**
   * Returns the original upper bound.
   */
  public double getOriginalSup() {
    return this.originalSup;
  }

  /**
   * Makes this domain be included in the specified interval.
   *
   * @throws ContradictionException
   */
  public void intersect(RealInterval interval, int index) throws ContradictionException {
    silent = false;
    boolean modified = false;
    double old_width = this.getSup() - this.getInf();
    double new_width = Math.min(interval.getSup(), this.getSup()) -
        Math.max(interval.getInf(), this.getInf());
    boolean toAwake = (variable.getProblem().getPrecision() / 100. <= old_width)
        && (new_width < old_width * variable.getProblem().getReduction());

    double oldInf = this.getInf();
    double newInf = interval.getInf();
    if (newInf > oldInf) {
      modified = true;
      PalmExplanation e = (PalmExplanation) ((PalmProblem) this.getProblem()).makeExplanation();
      ((PalmRealInterval) interval).self_explain(INF, e); // New bound
      this.self_explain(INF, e); //  Old bound ... TODO : really needed ?!
      explanationOnInf.add(e.makeIncInfExplanation(oldInf, (PalmRealVar) this.variable));
      this.inf.set(newInf);
      //RealStructureMaintainer.updateDataStructures(this.variable, INF, newInf, oldInf); // TODO
      if (toAwake) ((PalmEngine) this.getProblem().getPropagationEngine()).postUpdateInf(this.variable, index);
    }
    double oldSup = this.getSup();
    double newSup = interval.getSup();
    if (newSup < oldSup) {
      modified = true;
      PalmExplanation e = (PalmExplanation) ((PalmProblem) this.getProblem()).makeExplanation();
      ((PalmRealInterval) interval).self_explain(SUP, e);
      this.self_explain(SUP, e);
      explanationOnSup.add(e.makeDecSupExplanation(oldSup, (PalmRealVar) this.variable));
      this.sup.set(newSup);
      // RealStructureMaintainer.updateDataStructure(this.variable, SUP, newSup, oldSup); // TODO
      if (toAwake) ((PalmEngine) this.getProblem().getPropagationEngine()).postUpdateSup(this.variable, index);
    }

    if (modified && this.getInf() > this.getSup()) {
      ((PalmEngine) this.getProblem().getPropagationEngine()).raisePalmContradiction(this.variable);
    }
  }

  /**
   * Quietly assign this domain to the values of the specified interval. No PaLM events are trown.
   */
  public void silentlyAssign(RealInterval i) {
    inf.set(i.getInf());
    sup.set(i.getSup());
    silent = true;
  }

  /**
   * Explains the state of this domain
   *
   * @param select The part of the domain that should be explained
   * @param e      Constraint collection this explanation must be added to.
   */
  public void self_explain(int select, Explanation e) {
    if (!silent)
      switch (select) {
        case INF:
          e.merge((ConstraintCollection) this.explanationOnInf.getLast());
          break;
        case SUP:
          e.merge((ConstraintCollection) this.explanationOnSup.getLast());
          break;
        case DOM:
          e.merge((ConstraintCollection) this.explanationOnInf.getLast());
          e.merge((ConstraintCollection) this.explanationOnSup.getLast());
          break;
      }
  }

  /**
   * Restores lower bound to the specified value.
   */
  public void restoreInf(double newValue) {
    if (this.getInf() > newValue) {
      double oldValue = this.getInf();
      this.inf.set(newValue);
      // RealStructureMaintainer.updateDataStructuresOnRestore(this.variable, INF, newValue, oldValue); // TODO
      ((PalmEngine) this.getProblem().getPropagationEngine()).postRestoreInf((PalmRealVar) this.variable);
    }
  }

  /**
   * Restores upper bound to the specified value.
   */
  public void restoreSup(double newValue) {
    if (this.getSup() < newValue) {
      double oldValue = this.getSup();
      this.sup.set(newValue);
      // RealStructureMaintainer.updateDataStructuresOnRestore(this.variable, SUP, newValue, oldValue); // TODO
      ((PalmEngine) this.getProblem().getPropagationEngine()).postRestoreSup((PalmRealVar) this.variable);
    }
  }

  /**
   * Reset lower bound explanatioins: not up-to-date explanations are removed.
   */
  public void resetExplanationOnInf() {
    boolean keep = true;
    for (ListIterator iterator = explanationOnInf.listIterator(); iterator.hasNext();) {
      RealBoundExplanation expl = (RealBoundExplanation) iterator.next();
      if (expl.getPreviousValue() >= this.getInf()) {
        if (expl.getPreviousValue() == this.getOriginalInf() && keep) {
          keep = false;
        } else {
          iterator.remove();
        }
      }
    }
  }

  /**
   * Reset upper bound explanatioins: not up-to-date explanations are removed.
   */
  public void resetExplanationOnSup() {
    boolean keep = true;
    for (ListIterator iterator = explanationOnSup.listIterator(); iterator.hasNext();) {
      RealBoundExplanation expl = (RealBoundExplanation) iterator.next();
      if (expl.getPreviousValue() <= this.getSup()) {
        if (expl.getPreviousValue() == this.getOriginalSup() && keep) {
          keep = false;
        } else {
          iterator.remove();
        }
      }
    }
  }

  /**
   * Updates decision constraints by removing not up-to-date ones.
   */
  public void updateDecisionConstraints() {
    java.util.BitSet constraints = decisionConstraints.getBitSet();
    for (int i = constraints.nextSetBit(0); i >= 0; i = constraints.nextSetBit(i + 1)) {
      if (((PalmProblem) this.problem).getConstraintNb(i) == null || !((PalmProblem) this.problem).getConstraintNb(i).isActive()) {
        constraints.clear(i);
      }
    }
  }
}
