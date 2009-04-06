package i_want_to_use_this_old_version_of_choco.palm.cbj.integer;

import i_want_to_use_this_old_version_of_choco.ConstraintCollection;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl;
import i_want_to_use_this_old_version_of_choco.integer.var.IntervalIntDomain;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedProblem;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.JumpProblem;
import i_want_to_use_this_old_version_of_choco.palm.cbj.search.JumpContradictionException;
import i_want_to_use_this_old_version_of_choco.palm.dbt.prop.StructureMaintainer;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntDomain;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntVar;
import i_want_to_use_this_old_version_of_choco.prop.VarEvent;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class JumpIntervalIntDomain extends IntervalIntDomain implements ExplainedIntDomain {

  /**
   * A stack of explanations for lower bound modifications.
   */

  protected final LinkedList explanationOnInf;


  /**
   * A stack of explanations for upper bound modifications.
   */

  protected final LinkedList explanationOnSup;

  /**
   * The number of valid inf explanation at the current world level
   */
  protected IStateInt nbexpinf;


  /**
   * The number of valid inf explanation at the current world level
   */
  protected IStateInt nbexpsup;

  /**
   * Original lower bound.
   */

  protected final int originalInf;


  /**
   * Original upper bound.
   */

  protected final int originalSup;


  /**
   * Builds a interval domain for the specified variable.
   *
   * @param v Involved variable.
   * @param a Lower bound.
   * @param b Upper bound.
   */

  public JumpIntervalIntDomain(IntDomainVarImpl v, int a, int b) {
    super(v, a, b);
    JumpProblem pb = (JumpProblem) this.getProblem();
    this.explanationOnInf = new LinkedList();
    this.explanationOnSup = new LinkedList();
    this.explanationOnInf.add((pb.makeExplanation()));
    this.explanationOnSup.add((pb.makeExplanation()));
    this.nbexpinf = pb.getEnvironment().makeInt();
    this.nbexpinf.set(1);
    this.nbexpsup = pb.getEnvironment().makeInt();
    this.nbexpsup.set(1);
    this.originalInf = a;
    this.originalSup = b;
  }


  /**
   * Returns the original lower bound.
   */

  public int getOriginalInf() {
    return this.originalInf;
  }


  /**
   * Returns the original upper bound.
   */

  public int getOriginalSup() {
    return this.originalSup;
  }


  /**
   * Returns all the value currently in the domain.
   */

  public int[] getAllValues() {
    int[] ret = new int[getSup() - getInf() + 1];
    for (int i = 0; i < ret.length; i++)
      ret[i] = getInf() + i;
    return ret;
  }

  /**
   * Updates the upper bound and posts the event.
   */

  public boolean updateSup(int x, int idx, Explanation e) throws ContradictionException {
    if (this.updateSup(x, e)) {
      int cause = VarEvent.NOCAUSE;
      if (x == this.getSup()) cause = idx;
      problem.getPropagationEngine().postUpdateSup(variable, cause);
      if (x < this.getInf()) {
        this.variable.value.set(IStateInt.UNKNOWN_INT);
        Explanation exp = ((ExplainedProblem) problem).makeExplanation();
        this.self_explain(ExplainedIntDomain.DOM, exp);
        throw (new JumpContradictionException(this.getProblem(), exp));
        //((PalmEngine) this.getProblem().getPropagationEngine()).raisePalmContradiction((PalmIntVar) this.variable);
      }
      return true;
    }
    return false;
  }


  /**
   * Updates the lower bound and posts the event.
   */

  public boolean updateInf(int x, int idx, Explanation e) throws ContradictionException {
    if (this.updateInf(x, e)) {
      int cause = VarEvent.NOCAUSE;
      if (x == this.getInf()) cause = idx;
      problem.getPropagationEngine().postUpdateInf(variable, cause);
      if (x > this.getSup()) {
        this.variable.value.set(IStateInt.UNKNOWN_INT);
        Explanation exp = ((ExplainedProblem) problem).makeExplanation();
        this.self_explain(ExplainedIntDomain.DOM, exp);
        throw (new JumpContradictionException(this.getProblem(), exp));
        //((PalmEngine) this.getProblem().getPropagationEngine()).raisePalmContradiction((PalmIntVar) this.variable);
      }
      return true;
    }
    return false;
  }


  /**
   * Removes a value and posts the event.
   */

  public boolean removeVal(int value, int idx, Explanation e) throws ContradictionException {
    if (value == this.getInf()) {
      return this.updateInf(value + 1, idx, e);
    } else if (value == this.getSup()) {
      return this.updateSup(value - 1, idx, e);
    }
    return false;
  }

  /**
   * Allows to get an explanation for the domain or a bound of the variable. This explanation is merge to the
   * explanation in parameter.
   *
   * @param select Should be <code>PalmIntDomain.INF</code>, <code>PalmIntDomain.SUP</code>, or <code>PalmIntDomain.DOM</code>
   */

  public void self_explain(int select, Explanation expl) {
    switch (select) {
      case DOM:
        ensureUpToDateExplanations();
        expl.merge((ConstraintCollection) this.explanationOnInf.getLast());
        expl.merge((ConstraintCollection) this.explanationOnSup.getLast());
        break;
      case INF:
        ensureUpToDateExplanations();
        expl.merge((ConstraintCollection) this.explanationOnInf.getLast());
        break;
      case SUP:
        ensureUpToDateExplanations();
        expl.merge((ConstraintCollection) this.explanationOnSup.getLast());
        break;
      default:
        if (Logger.getLogger("choco").isLoggable(Level.WARNING))
          Logger.getLogger("choco").warning("PaLM: VAL needs another parameter in self_explain (IntDomainVar)");
    }
  }

  public void ensureUpToDateExplanations() {
    while (nbexpinf.get() < explanationOnInf.size()) {
      explanationOnInf.removeLast();
    }
    while (nbexpsup.get() < explanationOnSup.size()) {
      explanationOnSup.removeLast();
    }
  }

  /**
   * Allows to get an explanation for a value removal from the variable. This explanation is merge to the
   * explanation in parameter.
   *
   * @param select Should be <code>PalmIntDomain.VAL</code>
   */

  public void self_explain(int select, int x, Explanation expl) {
    if (select == VAL) {
      ensureUpToDateExplanations();
      // TODO : on ne peut pas prendre une explication plus precise ?
      if (x < this.getInf())
        expl.merge((ConstraintCollection) this.explanationOnInf.getLast());
      else if (x > this.getSup())
        expl.merge((ConstraintCollection) this.explanationOnSup.getLast());
    } else {
      if (Logger.getLogger("choco").isLoggable(Level.WARNING))
        Logger.getLogger("choco").warning("PaLM: INF, SUP or DOM do not need a supplementary parameter in self_explain (IntDomainVar)");
    }
  }

  protected boolean updateSup(int x, Explanation e) {
    if (x < this.getSup()) {
      int oldValue = this.getSup();
      ((ExplainedIntVar) this.variable).self_explain(SUP, e);
      this.explanationOnSup.add(e);//.makeDecSupExplanation(this.getSup(), (PalmIntVar) this.variable));
      this.nbexpsup.add(1);
      this.updateSup(x);
      if (this.inf.get() == this.sup.get()) {
        this.variable.value.set(this.getInf());
      }
      StructureMaintainer.updateDataStructures(this.variable, SUP, x, oldValue);
      //((PalmIntVar) this.variable).updateDataStructures(PalmIntVar.SUP, x, oldValue);
      return true;
    }
    return false;
  }

  protected boolean updateInf(int x, Explanation e) {
    if (x > this.getInf()) {
      int oldValue = this.getInf();
      ((ExplainedIntVar) this.variable).self_explain(INF, e);
      this.explanationOnInf.add(e); //.makeIncInfExplanation(this.getInf(), (PalmIntVar) this.variable));
      this.nbexpinf.add(1);
      this.updateInf(x);
      if (this.inf.get() == this.sup.get()) {
        this.variable.value.set(this.getInf());
      }
      StructureMaintainer.updateDataStructures(this.variable, INF, x, oldValue);
      //((PalmIntVar) this.variable).updateDataStructures(PalmIntVar.INF, x, oldValue);
      return true;
    }
    return false;
  }
}
