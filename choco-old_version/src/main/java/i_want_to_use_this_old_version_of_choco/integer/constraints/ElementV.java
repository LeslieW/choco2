// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.integer.constraints;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.mem.IStateBool;
import i_want_to_use_this_old_version_of_choco.prop.VarEvent;
import i_want_to_use_this_old_version_of_choco.util.Arithm;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * A class implementing the constraint A[I] == X, with I and X being IntVars and A an array of IntVars
 */
public class ElementV extends AbstractLargeIntConstraint {
  protected IStateBool valueUpdateNeeded;
  protected IStateBool indexUpdateNeeded;

  public ElementV(IntDomainVar[] vars, int offset) {
    super(vars);
    this.cste = offset;
    initElementV();
  }

  private void initElementV() {
    valueUpdateNeeded = getProblem().getEnvironment().makeBool(true);
    indexUpdateNeeded = getProblem().getEnvironment().makeBool(true);
  }

  public Object clone() throws CloneNotSupportedException {
    Object res = super.clone();
    ((ElementV) res).initElementV();
    return res;
  }

  public String toString() {
    return "eltV";
  }

  public String pretty() {
    return (this.getValueVar().toString() + " = nth(" + this.getIndexVar().toString() + ", " + Arithm.pretty(this.vars, 0, vars.length - 3) + ")");
  }


  protected IntDomainVar getIndexVar() {
    return vars[vars.length - 2];
  }

  protected IntDomainVar getValueVar() {
    return vars[vars.length - 1];
  }

  public boolean isSatisfied(int[] tuple) {
    return tuple[tuple[vars.length - 2]] == tuple[vars.length - 1]; //getValueVar().getVal());
  }

  protected void updateValueFromIndex() throws ContradictionException {
    IntDomainVar idxVar = getIndexVar();
    IntDomainVar valVar = getValueVar();
    int minval = Integer.MAX_VALUE;
    int maxval = Integer.MIN_VALUE;
    for (IntIterator iter = idxVar.getDomain().getIterator(); iter.hasNext();) {
      int feasibleIndex = iter.next();
      minval = Arithm.min(minval, vars[feasibleIndex + cste].getInf());
      maxval = Arithm.max(maxval, vars[feasibleIndex + cste].getSup());
    }
    // further optimization:
    // I should consider for the min, the minimum value in domain(c.vars[feasibleIndex) that is >= to valVar.inf
    // (it can be greater than valVar.inf if there are holes in domain(c.vars[feasibleIndex]))
    valVar.updateInf(minval, cIndices[vars.length - 1]);
    valVar.updateSup(maxval, cIndices[vars.length - 1]);
    // v1.0: propagate on holes when valVar has an enumerated domain
    if (valVar.hasEnumeratedDomain()) {
      for (int v = valVar.getInf(); v < valVar.getSup(); v = valVar.getNextDomainValue(v)) {
        boolean possibleV = false;
        IntIterator it = idxVar.getDomain().getIterator();
        while ((it.hasNext()) && !(possibleV) ) {
        int tentativeIdx = it.next();
  //      for (int tentativeIdx = idxVar.getInf(); tentativeIdx <= idxVar.getSup(); tentativeIdx = idxVar.getNextDomainValue(tentativeIdx)) {
          if (vars[tentativeIdx + cste].canBeInstantiatedTo(v)) {
            possibleV = true;
            break;
          }
        }
        if (!possibleV) {
          valVar.removeVal(v, cIndices[vars.length - 1]);
        }
      }
    }
    valueUpdateNeeded.set(false);
  }

  protected void updateIndexFromValue() throws ContradictionException {
    IntDomainVar idxVar = getIndexVar();
    IntDomainVar valVar = getValueVar();
    int minFeasibleIndex = Arithm.max(0 - cste, idxVar.getInf());
    int maxFeasibleIndex = Arithm.min(idxVar.getSup(), vars.length - 3 - cste);
    int cause = cIndices[vars.length - 2];
    if (valVar.hasEnumeratedDomain()) {
      cause = VarEvent.NOCAUSE;
    }
    while (idxVar.canBeInstantiatedTo(minFeasibleIndex) &&
        !(valVar.canBeEqualTo(vars[minFeasibleIndex + cste]))) {
      minFeasibleIndex++;
    }
    idxVar.updateInf(minFeasibleIndex, cause);


    while (idxVar.canBeInstantiatedTo(maxFeasibleIndex) &&
        !(valVar.canBeEqualTo(vars[maxFeasibleIndex + cste]))) {
      maxFeasibleIndex--;
    }
    idxVar.updateSup(maxFeasibleIndex, cause);

    if (idxVar.hasEnumeratedDomain()) { //those remVal would be ignored for variables using an interval approximation for domain
      for (int i = minFeasibleIndex + 1; i < maxFeasibleIndex - 1; i++) {
        if (idxVar.canBeInstantiatedTo(i) && !valVar.canBeEqualTo(vars[i + cste])) {
          idxVar.removeVal(i, cause);
        }
      }
    }
    // if the domain of idxVar has been reduced to one element, then it behaves like an equality
    if (idxVar.isInstantiated()) {
      equalityBehaviour();
    }
    indexUpdateNeeded.set(false);
  }

// Once the index is known, the constraints behaves like an equality : valVar == c.vars[idxVar.value]
// This method must only be called when the value of idxVar is known.
  protected void equalityBehaviour() throws ContradictionException {
    assert(getIndexVar().isInstantiated());
    int indexVal = getIndexVar().getVal();
    IntDomainVar valVar = getValueVar();
    IntDomainVar targetVar = vars[indexVal + cste];
    // code similar to awake@Equalxyc
    valVar.updateInf(targetVar.getInf(), cIndices[vars.length - 1]);
    valVar.updateSup(targetVar.getSup(), cIndices[vars.length - 1]);
    targetVar.updateInf(valVar.getInf(), cIndices[indexVal + cste]);
    targetVar.updateSup(valVar.getSup(), cIndices[indexVal + cste]);
    if (targetVar.hasEnumeratedDomain()) {
      for (int val = valVar.getInf(); val < valVar.getSup(); val = valVar.getNextDomainValue(val)) {
        if (!targetVar.canBeInstantiatedTo(val)) {
          valVar.removeVal(val, cIndices[vars.length - 1]);
        }
      }
    }
    if (valVar.hasEnumeratedDomain()) {
      for (int val = targetVar.getInf(); val < targetVar.getSup(); val = targetVar.getNextDomainValue(val)) {
        if (!valVar.canBeInstantiatedTo(val)) {
          targetVar.removeVal(val, cIndices[indexVal]);
        }
      }
    }
  }

  public void awake() throws ContradictionException {
    int n = vars.length;
    IntDomainVar idxVar = getIndexVar();
    idxVar.updateInf(0 - cste, cIndices[n - 2]);
    idxVar.updateSup(n - 3 - cste, cIndices[n - 2]);
    propagate();
  }

  public void propagate() throws ContradictionException {
    if (indexUpdateNeeded.get()) {
      updateIndexFromValue();
    }
    if (getIndexVar().isInstantiated()) {
      equalityBehaviour();
    } else if (valueUpdateNeeded.get()) {
      updateValueFromIndex();
    }
  }

  public void awakeOnInf(int idx) throws ContradictionException {
    IntDomainVar idxVar = getIndexVar();
    IntDomainVar valVar = getValueVar();
    if (idx == vars.length - 2) {        // the event concerns idxVar
      if (idxVar.isInstantiated()) {
        equalityBehaviour();
      } else {
        updateValueFromIndex();
      }
    } else if (idx == vars.length - 1) { // the event concerns valVar
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        vars[idxVal + cste].updateInf(valVar.getInf(), cIndices[idxVal + cste]);
      } else {
        updateIndexFromValue();
      }
    } else {                            // the event concerns a variable from the array
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        if (idx == idxVal + cste) {
          valVar.updateInf(vars[idx].getInf(), cIndices[vars.length - 1]);
        }
      } else if (idxVar.canBeInstantiatedTo(idx - cste)) {  //otherwise the variable is not in scope
        if (!valVar.canBeEqualTo(vars[idx])) {
          idxVar.removeVal(idx - cste, VarEvent.NOCAUSE);
          // NOCAUSE because if it changes the domain of IndexVar (what is not sure if idxVar
          // uses an interval approximated domain) then it must cause updateValueFromIndex(c)
        } else if (vars[idx].getInf() > valVar.getInf()) {
          // only the inf can change if the index is not removed
          int minval = Integer.MAX_VALUE;
          for (IntIterator it = idxVar.getDomain().getIterator(); it.hasNext();) {
            int feasibleIndex = it.next() + this.cste;
            minval = Arithm.min(minval, vars[feasibleIndex].getInf());
          }
          valVar.updateInf(minval, VarEvent.NOCAUSE);
          // NOCAUSE because if valVar takes a new min, then it can have consequence
          // on the constraint itself (ie remove indices such that l[i].sup < value.inf)
        }
      }
    }
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    IntDomainVar idxVar = getIndexVar();
    IntDomainVar valVar = getValueVar();
    if (idx == vars.length - 2) {        // the event concerns idxVar
      if (idxVar.isInstantiated()) {
        equalityBehaviour();
      } else {
        updateValueFromIndex();
      }
    } else if (idx == vars.length - 1) {  // the event concerns valVar
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        vars[idxVal + cste].updateSup(valVar.getSup(), cIndices[idxVal + cste]);
      } else {
        updateIndexFromValue();
      }
    } else {                            // the event concerns a variable from the array
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        if (idx == idxVal + cste) {
          valVar.updateSup(vars[idx].getSup(), cIndices[vars.length - 1]);
        }
      } else if (idxVar.canBeInstantiatedTo(idx - cste)) {  //otherwise the variable is not in scope
        if (!valVar.canBeEqualTo(vars[idx])) {
          idxVar.removeVal(idx - cste, VarEvent.NOCAUSE);
          // NOCAUSE because if it changes the domain of IndexVar (what is not sure if idxVar
          // uses an interval approximated domain) then it must cause updateValueFromIndex(c)
        } else if (vars[idx].getSup() < valVar.getSup()) {
          // only the sup can change if the index is not removed
          int maxval = Integer.MIN_VALUE;
          for (IntIterator it = idxVar.getDomain().getIterator(); it.hasNext();) {
            int feasibleIndex = it.next() + this.cste;
            maxval = Arithm.max(maxval, vars[feasibleIndex].getSup());
          }
          valVar.updateSup(maxval, VarEvent.NOCAUSE);
          // NOCAUSE because if valVar takes a new min, then it can have consequence
          // on the constraint itself (ie remove indices such that l[i].sup < value.inf)
        }
      }
    }
  }

  public void awakeOnInst(int idx) throws ContradictionException {
    IntDomainVar idxVar = getIndexVar();
    IntDomainVar valVar = getValueVar();
    if (idx == vars.length - 2) {        // the event concerns idxVar
      equalityBehaviour();
    } else if (idx == vars.length - 1) {  // the event concerns valVar
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        vars[idxVal + cste].instantiate(valVar.getVal(), cIndices[idxVal + cste]);
      } else {
        updateIndexFromValue();
      }
    } else {                            // the event concerns a variable from the array
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        if (idx == idxVal + cste) {
          valVar.instantiate(vars[idx].getVal(), cIndices[vars.length - 1]);
        }
      } else if (idxVar.canBeInstantiatedTo(idx - cste)) {  //otherwise the variable is not in scope
        if (!valVar.canBeEqualTo(vars[idx])) {
          idxVar.removeVal(idx - cste, VarEvent.NOCAUSE);
          // NOCAUSE because if it changes the domain of IndexVar (what is not sure if idxVar
          // uses an interval approximated domain) then it must cause updateValueFromIndex(c)
        } else {
          updateValueFromIndex(); // both the min and max may have changed
        }
      }
    }
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    IntDomainVar idxVar = getIndexVar();
    IntDomainVar valVar = getValueVar();
    if (idx == vars.length - 2) {        // the event concerns idxVar
      updateValueFromIndex();
    } else if (idx == vars.length - 1) {  // the event concerns valVar
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        vars[idxVal + cste].removeVal(x, cIndices[idxVal + cste]);
      } else {
        updateIndexFromValue();
      }
    } else {                            // the event concerns a variable from the array
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        if (idx == idxVal + cste) {
          valVar.removeVal(x, cIndices[vars.length - 1]);
        }
      } else if ((idxVar.canBeInstantiatedTo(idx - cste)) && (valVar.hasEnumeratedDomain())) {
        boolean existsSupport = false;
        for (IntIterator it = idxVar.getDomain().getIterator(); it.hasNext();) {
          int feasibleIndex = it.next() + this.cste;
          if (vars[feasibleIndex].canBeInstantiatedTo(x)) {
            existsSupport = true;
          }
        }
        if (!existsSupport) {
          valVar.removeVal(x, VarEvent.NOCAUSE);
        }
      }
    }
  }

  public void awakeOnVar(int idx) throws ContradictionException {
    IntDomainVar idxVar = getIndexVar();
    IntDomainVar valVar = getValueVar();
    if (idx == vars.length - 2) {
      if (idxVar.isInstantiated()) {
        equalityBehaviour();
      } else {
        updateValueFromIndex();
      }
    } else if (idx == vars.length - 1) {
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        vars[idxVal + cste].updateSup(valVar.getSup(), cIndices[idxVal + cste]);
        vars[idxVal + cste].updateInf(valVar.getInf(), cIndices[idxVal + cste]);
        for (IntIterator it = vars[idxVal + cste].getDomain().getIterator(); it.hasNext();) {
          int v = it.next();
          if (!(valVar.canBeInstantiatedTo(v))) {
            vars[idxVal + cste].removeVal(v, cIndices[idxVal + cste]);
          }
        }
      } else {
        updateIndexFromValue();
      }
    } else {
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        if (idx == idxVal + cste) {
          valVar.updateSup(vars[idx].getSup(), cIndices[vars.length - 1]);
          valVar.updateInf(vars[idx].getInf(), cIndices[vars.length - 1]);
          for (IntIterator it = valVar.getDomain().getIterator(); it.hasNext();) {
            int v = it.next();
            if (!(vars[idx].canBeInstantiatedTo(v))) {
              valVar.removeVal(v, cIndices[vars.length - 1]);
            }
          }
        }
      } else if (idxVar.canBeInstantiatedTo(idx - cste)) {  // otherwise the variable is not in scope
        // TODO : there is probably something missing here ....
      }
    }
  }

  public Boolean isEntailed() {
    Boolean isEntailed = null;
    IntDomainVar idxVar = getIndexVar();
    IntDomainVar valVar = getValueVar();
    if ((valVar.isInstantiated()) &&
        (idxVar.getInf() + this.cste >= 0) &&
        (idxVar.getSup() + this.cste < vars.length - 2)) {
      boolean allEqualToValVar = true;
      for (IntIterator it = idxVar.getDomain().getIterator(); it.hasNext();) {
        int feasibleIndex = it.next() + this.cste;
        if (!vars[feasibleIndex].isInstantiatedTo(valVar.getVal())) {
          allEqualToValVar = false;
        }
      }
      if (allEqualToValVar) {
        isEntailed = Boolean.TRUE;
      }
    }
    if (isEntailed != Boolean.TRUE) {
      boolean existsSupport = false;
      for (IntIterator it = idxVar.getDomain().getIterator(); it.hasNext();) {
        int feasibleIndex = it.next() + this.cste;
        if ((feasibleIndex >= 0) && (feasibleIndex < vars.length - 2) && (valVar.canBeEqualTo(vars[feasibleIndex]))) {
          existsSupport = true;
        }
      }
      if (!existsSupport) isEntailed = Boolean.FALSE;
    }
    return isEntailed;
  }


}
