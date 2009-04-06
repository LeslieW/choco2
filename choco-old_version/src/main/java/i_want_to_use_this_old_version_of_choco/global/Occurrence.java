// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.global;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.constraints.AbstractLargeIntConstraint;
import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.IStateBitSet;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;

import java.util.logging.Logger;

public class Occurrence extends AbstractLargeIntConstraint {
  private Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop.const");


  /**
   * isPossible[i] is true if variable i can take the occurence value
   */
  public IStateBitSet isPossible;

  /**
   * isSure[i] is true if variable i is instantiated to the occurence value.
   */
  public IStateBitSet isSure;

  /**
   * Store the number of variables which can still take the occurence value
   */
  public IStateInt nbPossible;

  /**
   * Store the number of variables which are instantiated to the occurence value
   */
  public IStateInt nbSure;

  public boolean constrainOnInfNumber = false;    // >=
  public boolean constrainOnSupNumber = false;    // <=


  /**
   * Constructor,
   * API: should be used through the Problem.createOccurrence API
   * Define an occurence constraint setting size{forall v in lvars | v = occval} <= or >= or = occVar
   * assumes the occVar variable to be the last of the variables of the constraint:
   * vars = [lvars | occVar]
   * with  lvars = list of variables for which the occurence of occval in their domain is constrained
   *
   * @param occval checking value
   * @param onInf  if true, constraint insures size{forall v in lvars | v = occval} <= occVar
   * @param onSup  if true, constraint insure size{forall v in lvars | v = occval} >= occVar
   */
  public Occurrence(IntDomainVar[] vars, int occval, boolean onInf, boolean onSup) {
    super(vars);
    init(occval, onInf, onSup);
  }

  public Object clone() throws CloneNotSupportedException {
    Occurrence newc = (Occurrence) super.clone();
    newc.init(this.cste, this.constrainOnInfNumber, this.constrainOnSupNumber);
    return newc;
  }

  public void init(int occval, boolean onInf, boolean onSup) {
    this.cste = occval;
    this.constrainOnInfNumber = onInf;
    this.constrainOnSupNumber = onSup;

    IEnvironment envi = vars[0].getProblem().getEnvironment();
    this.problem = vars[0].getProblem();
    isPossible = envi.makeBitSet(vars.length-1);
    isSure = envi.makeBitSet(vars.length -1);
    nbPossible = envi.makeInt(0);
    nbSure = envi.makeInt(0);
  }

  public void awakeOnInf(int idx) throws ContradictionException {
    int nbVars = vars.length - 1;
    if (idx < nbVars) {
      if (isPossible.get(idx)) {
        if (vars[idx].getInf() > cste) {
          isPossible.clear(idx);
          nbPossible.add(-1);
          checkNbPossible();
        } else if (vars[idx].getInf() == cste &&
            !(isSure.get(idx)) &&
            constrainOnSupNumber &&
            nbSure.get() == vars[nbVars].getSup()) {
          isPossible.clear(idx);
          nbPossible.add(-1);
          vars[idx].updateInf(cste + 1, cIndices[idx]);
        }
      }
    } else
      checkNbPossible();
  }


  public void awakeOnSup(int idx) throws ContradictionException {
    int nbVars = vars.length - 1;
    if (idx < nbVars) {
      if (isPossible.get(idx)) {
        if (vars[idx].getSup() < cste) {
          isPossible.clear(idx);
          nbPossible.add(-1);
          checkNbPossible();
        } else if (vars[idx].getSup() == cste &&
            !(isSure.get(idx)) &&
            constrainOnInfNumber &&
            nbSure.get() == vars[nbVars].getSup()) {
          isPossible.clear(idx);
          nbPossible.add(-1);
          vars[idx].updateSup(cste - 1, cIndices[idx]);
        }
      }
    } else
      checkNbSure();
  }


  public void awakeOnInst(int idx) throws ContradictionException {
    int nbVars = vars.length - 1;
    if (idx < nbVars && isPossible.get(idx) && !(isSure.get(idx))) {
      if (vars[idx].getVal() == cste) {
        isSure.set(idx);
        nbSure.add(1);
        checkNbSure();
      } else {
        isPossible.clear(idx);
        nbPossible.add(-1);
        checkNbPossible();
      }
    } else {
      checkNbPossible();
      checkNbSure();
    }
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    int nbVars = vars.length - 1;
    if (idx < nbVars && x == cste && isPossible.get(idx) && vars[idx].hasEnumeratedDomain()) {
      isPossible.clear(idx);
      nbPossible.add(-1);
      checkNbPossible();
    }
  }

  public boolean isSatisfied(int[] tuple) {
    int nbVars = vars.length - 1;
    int cptVal = 0;
    for (int i = 0; i < nbVars; i++) {
      if (vars[i].getVal() == cste) cptVal++;
    }
    if (constrainOnInfNumber & constrainOnSupNumber)
      return cptVal == vars[nbVars].getVal();
    else if (constrainOnInfNumber)
      return cptVal >= vars[nbVars].getVal();
    else
      return cptVal <= vars[nbVars].getVal();
  }


  public void checkNbPossible() throws ContradictionException {
    int nbVars = vars.length - 1;
    if (constrainOnInfNumber) {
      vars[nbVars].updateSup(nbPossible.get(), cIndices[nbVars]);
      if (vars[nbVars].getInf() == nbPossible.get()) {
        for (int i = 0; i < nbVars; i++) {
          if (isPossible.get(i) && !vars[i].isInstantiated())
          {
            isSure.set(i);
            nbSure.add(1);
            vars[i].instantiate(cste, cIndices[i]);
          }
        }
      }
    }
  }

  public void checkNbSure() throws ContradictionException {
    int nbVars = vars.length - 1;
    if (constrainOnSupNumber) {
      vars[nbVars].updateInf(nbSure.get(), cIndices[nbVars]);
      if (vars[nbVars].getSup() == nbSure.get()) {
        for (int i = 0; i < nbVars; i++) {
          if (isPossible.get(i) && !vars[i].isInstantiated())
          {
            isPossible.clear(i);
            nbPossible.add(-1);
            vars[i].removeVal(cste, cIndices[i]);
          }
        }
      }
    }
  }

  public void filter() throws ContradictionException {
    checkNbPossible();
    checkNbSure();
  }

  public void propagate() throws ContradictionException {
    int nbVars = vars.length - 1;
    for (int j = 0; j < nbVars; j++) {
      if (isPossible.get(j)) {
        if (!isSure.get(j) && vars[j].isInstantiatedTo(cste)) {
          isSure.set(j);
          nbSure.add(1);
        } else if (!vars[j].canBeInstantiatedTo(cste)) {
          isPossible.clear(j);
          nbPossible.add(-1);
        }
      }
    }
    filter();
  }

  public void awake() throws ContradictionException {
    int nbVars = vars.length;
    for (int i = 0; i < (nbVars - 1); i++) {
      if (vars[i].canBeInstantiatedTo(cste)) {
        isPossible.set(i);
        nbPossible.add(1);
      }
      if (vars[i].isInstantiatedTo(cste)) {
        isSure.set(i);
        nbSure.add(+1);
      }
    }
    if (constrainOnInfNumber) vars[nbVars - 1].updateSup(nbVars - 1, cIndices[nbVars - 1]);
    if (constrainOnSupNumber) vars[nbVars - 1].updateInf(0, cIndices[nbVars - 1]);
    propagate();
  }

  public String pretty() {
    String s = "occur([";
    for (int i = 0; i < vars.length - 2; i++) {
      s += vars[i] + ",";
    }
    s += vars[vars.length - 2] + "], " + cste + ")";
    if (constrainOnInfNumber && constrainOnSupNumber)
      s += " = ";
    else if (constrainOnInfNumber)
      s += " >= ";
    else
      s += " <= ";
    s += vars[vars.length - 1];
    return s;
  }
}
