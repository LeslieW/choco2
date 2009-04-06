//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.dbt.integer;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ConstraintCollection;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl;
import i_want_to_use_this_old_version_of_choco.integer.var.IntervalIntDomain;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.explain.IBoundExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.prop.PalmEngine;
import i_want_to_use_this_old_version_of_choco.palm.dbt.prop.StructureMaintainer;
import i_want_to_use_this_old_version_of_choco.palm.integer.constraints.PalmAssignment;
import i_want_to_use_this_old_version_of_choco.palm.integer.constraints.PalmNotEqualXC;
import i_want_to_use_this_old_version_of_choco.prop.VarEvent;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PalmIntervalIntDomain extends IntervalIntDomain implements PalmIntDomain {
  /**
   * A stack of explanations for lower bound modifications.
   */

  protected final LinkedList explanationOnInf;


  /**
   * A stack of explanations for upper bound modifications.
   */

  protected final LinkedList explanationOnSup;


  /**
   * Decision constraints on the variable for branching purpose.
   */

  protected final Hashtable decisionConstraints;


  /**
   * Negation of decision constraints on the variable for branching purpose.
   */

  protected final Hashtable negDecisionConstraints;


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

  public PalmIntervalIntDomain(IntDomainVarImpl v, int a, int b) {
    super(v, a, b);
    PalmProblem pb = (PalmProblem) this.getProblem();
    this.explanationOnInf = new LinkedList();
    this.explanationOnSup = new LinkedList();
    this.explanationOnInf.add(((PalmExplanation) (pb.makeExplanation())).makeIncInfExplanation(this.getInf(), (PalmIntVar) this.variable));
    this.explanationOnSup.add(((PalmExplanation) (pb.makeExplanation())).makeDecSupExplanation(this.getSup(), (PalmIntVar) this.variable));
    this.decisionConstraints = new Hashtable();
    this.negDecisionConstraints = new Hashtable();
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
    //if (bucket != null) {
    //    return bucket.domainSet();
    //} else {
    int[] ret = new int[getSup() - getInf() + 1];
    for (int i = 0; i < ret.length; i++)
      ret[i] = getInf() + i;
    return ret;
    //}
  }


  /**
   * Returns the decision constraint assigning the domain to the specified value. The constraint is created if
   * it is not yet created.
   */

  public Constraint getDecisionConstraint(int val) {
    Constraint cons = (Constraint) this.decisionConstraints.get(new Integer(val - this.getOriginalInf()));
    if (cons != null) {
      return cons;
    } else {
      cons = new PalmAssignment(this.variable, val);
      this.decisionConstraints.put(new Integer(val - this.getOriginalInf()), cons);
      this.negDecisionConstraints.put(new Integer(val - this.getOriginalInf()), new PalmNotEqualXC(this.variable, val));
      return cons;
    }
  }


  /**
   * Returns the negated decision constraint.
   */

  public Constraint getNegDecisionConstraint(int val) {
    return (Constraint) this.negDecisionConstraints.get(new Integer(val - this.getOriginalInf()));
  }


  /**
   * Updates the upper bound and posts the event.
   */

  public boolean updateSup(int x, int idx, Explanation e) throws ContradictionException {
    if (this.updateSup(x, e)) {
      int cause = VarEvent.NOCAUSE;
      if (x == this.getSup()) cause = idx;
      ((PalmEngine) this.getProblem().getPropagationEngine()).postUpdateSup(this.variable, cause);
      if (x < this.getInf()) {
        this.variable.value.set(IStateInt.UNKNOWN_INT);
        ((PalmEngine) this.getProblem().getPropagationEngine()).raisePalmContradiction((PalmIntVar) this.variable);
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
      ((PalmEngine) this.getProblem().getPropagationEngine()).postUpdateInf(this.variable, cause);
      if (x > this.getSup()) {
        this.variable.value.set(IStateInt.UNKNOWN_INT);
        ((PalmEngine) this.getProblem().getPropagationEngine()).raisePalmContradiction((PalmIntVar) this.variable);
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
   * Restores a lower bound and posts the event.
   */

  public void restoreInf(int newValue) {
    if (this.getInf() > newValue) {
      int oldValue = this.getInf();
      this.inf.set(newValue);
      if (this.getInf() != this.getSup()) {
        this.variable.value.set(IStateInt.UNKNOWN_INT);
      } else {
        this.variable.value.set(this.getInf());
      }
      StructureMaintainer.updateDataStructuresOnRestore(this.variable, INF, newValue, oldValue);
      //((PalmIntVar) this.variable).updateDataStructuresOnRestore(PalmIntVar.INF, newValue, oldValue);
      ((PalmEngine) this.getProblem().getPropagationEngine()).postRestoreInf((PalmIntVar) this.variable);
    }
  }


  /**
   * Restores an upper bound and posts the event.
   */

  public void restoreSup(int newValue) {
    if (this.getSup() < newValue) {
      int oldValue = this.getSup();
      this.sup.set(newValue);
      if (this.getInf() != this.getSup()) {
        this.variable.value.set(IStateInt.UNKNOWN_INT);
      } else {
        this.variable.value.set(this.getInf());
      }
      StructureMaintainer.updateDataStructuresOnRestore(this.variable, SUP, newValue, oldValue);
      //((PalmIntVar) this.variable).updateDataStructuresOnRestore(PalmIntVar.SUP, newValue, oldValue);
      ((PalmEngine) this.getProblem().getPropagationEngine()).postRestoreSup((PalmIntVar) this.variable);
    }
  }


  /**
   * Restores a value and posts the event. Not supported for such a domain.
   */

  public void restoreVal(int val) {
    System.err.println("restoreVal should not be called on a IntervalIntdomain !");
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
        this.self_explain(INF, expl);
        this.self_explain(SUP, expl);
        break;
      case INF:
        expl.merge((Explanation) this.explanationOnInf.getLast());
        break;
      case SUP:
        expl.merge((Explanation) this.explanationOnSup.getLast());
        break;
      default:
        if (Logger.getLogger("choco").isLoggable(Level.WARNING))
          Logger.getLogger("choco").warning("PaLM: VAL needs another parameter in self_explain (IntDomainVar)");
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


  /**
   * When a value is restored, it deletes the explanation associated to the value removal.
   */

  public void resetExplanationOnVal(int val) {
  }


  /**
   * When a lower bound is restored, it deletes the explanation associated to the value removal.
   */

  public void resetExplanationOnInf() {
    boolean keep = true;
    for (ListIterator iterator = explanationOnInf.listIterator(); iterator.hasNext();) {
      IBoundExplanation expl = (IBoundExplanation) iterator.next();
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
   * When an upper bound is restored, it deletes the explanation associated to the value removal.
   */

  public void resetExplanationOnSup() {
    boolean keep = true;
    for (ListIterator iterator = explanationOnSup.listIterator(); iterator.hasNext();) {
      IBoundExplanation expl = (IBoundExplanation) iterator.next();
      if (expl.getPreviousValue() <= this.getSup()) {
        if (expl.getPreviousValue() == this.getOriginalSup() && keep) {
          keep = false;
        } else {
          iterator.remove();
        }
      }
    }
  }

  protected boolean updateSup(int x, Explanation e) {
    if (x < this.getSup()) {
      int oldValue = this.getSup();
      ((PalmIntVar) this.variable).self_explain(SUP, e);
      this.explanationOnSup.add(((PalmExplanation) e).makeDecSupExplanation(this.getSup(), (PalmIntVar) this.variable));
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
      ((PalmIntVar) this.variable).self_explain(INF, e);
      this.explanationOnInf.add(((PalmExplanation) e).makeIncInfExplanation(this.getInf(), (PalmIntVar) this.variable));
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
