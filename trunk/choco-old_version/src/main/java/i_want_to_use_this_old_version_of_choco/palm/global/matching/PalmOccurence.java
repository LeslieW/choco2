package i_want_to_use_this_old_version_of_choco.palm.global.matching;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.global.Occurrence;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.mem.IStateBitSet;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedProblem;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.PalmConstraint;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntDomain;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntVar;
import i_want_to_use_this_old_version_of_choco.palm.integer.PalmIntVarListener;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.Set;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class PalmOccurence extends Occurrence implements PalmConstraint, PalmIntVarListener {
  private boolean checkPossible = false, checkSure = false;
  private IStateBitSet checkInf;
  private IStateBitSet checkSup;

  public PalmOccurence(IntDomainVar[] lvars, int occval, boolean onInf, boolean onSup) {
    super(lvars, occval, onInf, onSup);
    checkInf = lvars[0].getProblem().getEnvironment().makeBitSet(lvars.length - 1);
    checkSup = lvars[0].getProblem().getEnvironment().makeBitSet(lvars.length - 1);
    this.hook = ((ExplainedProblem) this.getProblem()).makeConstraintPlugin(this);
    //this.problem = pb;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public void checkNbPossible() throws ContradictionException {
    if (this.constrainOnInfNumber) {
      Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
      ((ExplainedConstraintPlugin) this.hook).self_explain(expl);

      for (int i = 0; i < this.vars.length - 1; i++) {
        if (!this.isPossible.get(i)) {
          ((ExplainedIntVar) this.vars[i]).self_explain(PalmIntDomain.VAL, this.cste, expl);
        }
      }

      ((ExplainedIntVar) this.vars[this.vars.length - 1]).updateSup(this.nbPossible.get(),
          cIndices[this.vars.length - 1], (Explanation) expl.copy());

      if (this.vars[this.vars.length - 1].getInf() == this.nbPossible.get()) {
        ((ExplainedIntVar) this.vars[this.vars.length - 1]).self_explain(PalmIntDomain.INF, expl);
        for (int i = 0; i < this.vars.length - 1; i++) {
          if (this.isPossible.get(i)) {
            ((ExplainedIntVar) this.vars[i]).instantiate(this.cste, cIndices[i], (Explanation) expl.copy());
          }
        }
      }

    }
    this.checkPossible = false;
  }

  public void checkNbSure() throws ContradictionException {
    if (this.constrainOnSupNumber) {
      Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
      ((ExplainedConstraintPlugin) this.hook).self_explain(expl);

      for (int i = 0; i < this.vars.length - 1; i++) {
        if (this.isSure.get(i)) {
          ((ExplainedIntVar) this.vars[i]).self_explain(PalmIntDomain.DOM, expl);
        }
      }


      ((ExplainedIntVar) this.vars[this.vars.length - 1]).updateInf(this.nbSure.get(),
          this.getConstraintIdx(this.vars.length - 1), (Explanation) expl.copy());


      if (this.vars[this.vars.length - 1].getSup() == this.nbSure.get()) {
        ((ExplainedIntVar) this.vars[this.vars.length - 1]).self_explain(PalmIntDomain.SUP, expl);
        for (int i = 0; i < this.vars.length - 1; i++) {
          if (this.isPossible.get(i) && !this.vars[i].isInstantiated()) {
            ((ExplainedIntVar) this.vars[i]).removeVal(this.cste, this.getConstraintIdx(i), (Explanation) expl.copy());
          }
        }
      }

    }
    this.checkSure = false;
  }

  public void updateDataStructuresOnConstraint(int idx, int select, int newValue, int oldValue) {
    switch (select) {
      case PalmIntDomain.VAL:
        if (idx < this.getNbVars() - 1) {
          if ((newValue == this.cste) && (this.isPossible.get(idx))) {
            this.isPossible.clear(idx);
            this.nbPossible.add(-1);
            this.checkPossible = true;
            if (this.isSure.get(idx)) {
              this.isSure.clear(idx);
              this.nbSure.add(-1);
              this.checkSure = true;
            }
          }

          if (this.vars[idx].isInstantiated()) {
            int val = this.vars[idx].getVal();
            if ((val == this.cste) && (!this.isSure.get(idx))) {
              this.isSure.set(idx);
              this.nbSure.add(1);
              this.checkSure = true;
            }
          }
        } else {
          this.checkSure = true;
          this.checkPossible = true;
        }
        if (newValue < this.vars[idx].getInf()) {
          this.checkInf.set(idx);
        }
        if (newValue > this.vars[idx].getSup()) {
          this.checkSup.set(idx);
        }
        break;
      case PalmIntDomain.INF:
        if (idx < this.getNbVars() - 1) {
          if ((newValue > this.cste) && (this.isPossible.get(idx))) {
            this.isPossible.clear(idx);
            this.nbPossible.add(-1);
            this.checkPossible = true;
            if (this.isSure.get(idx)) {
              this.isSure.clear(idx);
              this.nbSure.add(-1);
              this.checkSure = true;
            }
          }

          if (this.vars[idx].isInstantiated()) {
            int val = this.vars[idx].getVal();
            if ((val == this.cste) && (!this.isSure.get(idx))) {
              this.isSure.set(idx);
              this.nbSure.add(1);
              this.checkSure = true;
            }
          }
        } else {
          this.checkSure = true;
          this.checkPossible = true;
        }
        break;
      case PalmIntDomain.SUP:
        if (idx < this.getNbVars() - 1) {
          if ((newValue < this.cste) && (this.isPossible.get(idx))) {
            this.isPossible.clear(idx);
            this.nbPossible.add(-1);
            this.checkPossible = true;
            if (this.isSure.get(idx)) {
              this.isSure.clear(idx);
              this.nbSure.add(-1);
              this.checkSure = true;
            }
          }

          if (this.vars[idx].isInstantiated()) {
            int val = this.vars[idx].getVal();
            if ((val == this.cste) && (!this.isSure.get(idx))) {
              this.isSure.set(idx);
              this.nbSure.add(1);
              this.checkSure = true;
            }
          }
        } else {
          this.checkSure = true;
          this.checkPossible = true;
        }
        break;
    }
  }

  public void updateDataStructuresOnRestoreConstraint(int idx, int select, int newValue, int oldValue) {
    switch (select) {
      case PalmIntDomain.VAL:
        if (idx < this.getNbVars() - 1) {
          if (newValue == this.cste) {
            if (!this.isPossible.get(idx)) {
              this.isPossible.set(idx);
              this.nbPossible.add(1);
              this.checkPossible = true;
            }
            if (this.vars[idx].isInstantiated()) {
              if (!this.isSure.get(idx)) {
                this.isSure.set(idx);
                this.nbSure.add(1);
                this.checkSure = true;
              }
            }
          } else if (this.isSure.get(idx)) {
            this.isSure.clear(idx);
            this.nbSure.add(-1);
            this.checkSure = true;
          }
        } else {
          this.checkPossible = true;
          this.checkSure = true;
        }
        this.checkInf.set(idx);
        this.checkSup.set(idx);
        break;
      case PalmIntDomain.INF:
        if (idx < this.getNbVars() - 1) {
          if ((newValue <= this.cste) && (oldValue > this.cste) && (this.vars[idx].getSup() >= newValue)) {
            this.isPossible.set(idx);
            this.nbPossible.add(1);
            this.checkPossible = true;
            if (this.vars[idx].isInstantiated()) {
              int val = this.vars[idx].getVal();
              if ((val == this.cste) && (!this.isSure.get(idx))) {
                this.isSure.set(idx);
                this.nbSure.add(1);
                this.checkSure = true;
              } else if ((val != this.cste) && (this.isSure.get(idx))) { // A priori, c'est inutile !!
                this.isSure.clear(idx);
                this.nbSure.add(-1);
                this.checkSure = true;
              }
            }
          }
          if ((this.isSure.get(idx)) && (this.vars[idx].getInf() != this.cste)) {
            this.isSure.clear(idx);
            this.nbSure.add(-1);
            this.checkSure = true;
          }
        } else {
          this.checkPossible = true;
          this.checkSure = true;
        }
        break;
      case PalmIntDomain.SUP:
        if (idx < this.getNbVars() - 1) {
          if ((newValue >= this.cste) && (oldValue < this.cste) && (this.vars[idx].getInf() <= newValue)) {
            this.isPossible.set(idx);
            this.nbPossible.add(1);
            this.checkPossible = true;
            if (this.vars[idx].isInstantiated()) {
              int val = this.vars[idx].getVal();
              if ((val == this.cste) && (!this.isSure.get(idx))) {
                this.isSure.set(idx);
                this.nbSure.add(1);
                this.checkSure = true;
              } else if ((val != this.cste) && (this.isSure.get(idx))) {
                this.isSure.clear(idx);
                this.nbSure.add(-1);
                this.checkSure = true;
              }
            }
          }
          if ((this.isSure.get(idx)) && (this.vars[idx].getSup() != this.cste)) {
            this.isSure.clear(idx);
            this.nbSure.add(-1);
            this.checkSure = true;
          }
        } else {
          this.checkPossible = true;
          this.checkSure = true;
        }
        break;
    }
  }

  public void awakeOnInf(int idx) throws ContradictionException {
    if (idx < this.getNbVars() - 1) {
      if (this.checkPossible)
        this.checkNbPossible();
      if (this.checkSure)
        this.checkNbSure();
      if ((this.isPossible.get(idx)) && (this.vars[idx].getInf() == this.cste))
        if ((!this.isSure.get(idx)) && (this.constrainOnSupNumber) &&
            (this.nbSure.get() == this.vars[this.vars.length - 1].getSup())) {
          Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
          ((ExplainedConstraintPlugin) this.hook).self_explain(expl);
          ((ExplainedIntVar) this.vars[idx]).self_explain(PalmIntDomain.INF, expl);
          ((ExplainedIntVar) this.vars[this.vars.length - 1]).self_explain(PalmIntDomain.SUP, expl);
          for (int i = 0; i < this.getNbVars() - 1; i++) {
            if (this.isSure.get(i))
              ((ExplainedIntVar) this.vars[i]).self_explain(PalmIntDomain.DOM, expl);
          }
          ((ExplainedIntVar) this.vars[idx]).updateInf(this.cste + 1, this.getConstraintIdx(idx), expl);

        }
    } else {
      this.checkNbPossible();
    }
    this.checkInf.clear(idx);
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    if (idx < this.getNbVars() - 1) {
      if (this.checkPossible)
        this.checkNbPossible();
      if (this.checkSure)
        this.checkNbSure();
      if ((this.isPossible.get(idx)) && (this.vars[idx].getSup() == this.cste))
        if ((!this.isSure.get(idx)) && (this.constrainOnInfNumber) &&
            (this.nbSure.get() == this.vars[this.vars.length - 1].getSup())) {
          Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
          ((ExplainedConstraintPlugin) this.hook).self_explain(expl);
          ((ExplainedIntVar) this.vars[idx]).self_explain(PalmIntDomain.SUP, expl);
          ((ExplainedIntVar) this.vars[this.vars.length - 1]).self_explain(PalmIntDomain.SUP, expl);
          for (int i = 0; i < this.getNbVars() - 1; i++) {
            if (this.isSure.get(i))
              ((ExplainedIntVar) this.vars[i]).self_explain(PalmIntDomain.DOM, expl);
          }
          ((ExplainedIntVar) this.vars[idx]).updateSup(this.cste - 1, this.getConstraintIdx(idx), expl);
        }
    } else {
      this.checkNbPossible();
    }
    this.checkSup.clear(idx);
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    if (this.checkPossible) {
      this.checkNbPossible();
    }
    if (this.checkSure) {
      this.checkNbSure();
    }
    if (this.checkInf.get(idx)) {
      this.awakeOnInf(idx);
    }
    if (this.checkSup.get(idx)) {
      this.awakeOnSup(idx);
    }
  }

  public void awakeOnRestoreInf(int idx) throws ContradictionException {
    this.filter();
  }

  public void awakeOnRestoreSup(int idx) throws ContradictionException {
    this.filter();
  }

  public void awakeOnRestoreVal(int idx, int val) throws ContradictionException {
    this.filter();
  }

  public void awakeOnRestoreVal(int idx, IntIterator it) throws ContradictionException {
    for (; it.hasNext();) {
      awakeOnRestoreVal(idx, it.next());
    }
  }

  public void awake() throws ContradictionException {
    Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
    int n = this.vars.length;
    for (int i = 0; i < (n - 1); i++) {
      if (vars[i].canBeInstantiatedTo(cste)) {
        isPossible.set(i);
        nbPossible.add(1);
      }
      if (vars[i].isInstantiatedTo(cste)) {
        isSure.set(i);
        nbSure.add(+1);
      }
    }

    ((ExplainedConstraintPlugin) this.hook).self_explain(expl);

    if (this.constrainOnInfNumber) ((ExplainedIntVar) this.vars[n - 1]).updateSup(n, this.getConstraintIdx(n - 1), expl);
    if (this.constrainOnSupNumber) ((ExplainedIntVar) this.vars[n - 1]).updateInf(0, this.getConstraintIdx(n - 1), expl);

    propagate();
  }

  public Set whyIsTrue() {
    return null;
  }

  public Set whyIsFalse() {
    return null;
  }

  public void takeIntoAccountStatusChange(int index) {
  }
}
