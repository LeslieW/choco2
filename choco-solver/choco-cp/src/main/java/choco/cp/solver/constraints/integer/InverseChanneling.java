/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */


package choco.cp.solver.constraints.integer;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class InverseChanneling extends AbstractLargeIntSConstraint {

  protected int n;

    private int min;

    /**
   * link x and y so that x[i] = j <=> y[j] = i
   * It is used to maintain both model on permutation problems
   */
  public InverseChanneling(IntDomainVar[] allVars, int n) {
    super(allVars);
    this.min = allVars[0].getInf();
    this.n = n;
  }


    @Override
    public int getFilteredEventMask(int idx) {
        if (vars[idx].hasEnumeratedDomain()) {
            return IntVarEvent.INSTINTbitvector + IntVarEvent.REMVALbitvector;
        } else {
            return IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
        }
    }

  public void propagate() throws ContradictionException {
    for (int idx = 0; idx < vars.length; idx++) {
      for (int i = 0; i < n; i++) {
        if (idx < n && !vars[idx].canBeInstantiatedTo(i + min)) {
          vars[i + n].removeVal(idx + min, cIndices[i + n]);
        } else if (!vars[idx].canBeInstantiatedTo(i + min)) {
          vars[i].removeVal(idx - n + min, cIndices[i]);
        }
      }
    }
  }


  public void awakeOnInf(int idx) throws ContradictionException {
    int val = vars[idx].getInf() - min;
    if (idx < n) {
      for (int i = 0; i < val; i++) {
        vars[i + n].removeVal(idx + min, cIndices[i + n]);
      }
    } else {
      for (int i = 0; i < val; i++) {
        vars[i].removeVal(idx - n + min, cIndices[i]);
      }
    }
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    int val = vars[idx].getSup() + 1 - min;
    if (idx < n) {
      for (int i = val; i < n; i++) {
        vars[i + n].removeVal(idx + min, cIndices[i + n]);
      }
    } else {
      for (int i = val; i < n; i++) {
        vars[i].removeVal(idx - n + min, cIndices[i]);
      }
    }    //To change body of overridden methods use File | Settings | File Templates.
  }

  public void awakeOnInst(int idx) throws ContradictionException {
    int val = vars[idx].getVal() - min;
    if (idx < n) {
      vars[val + n].instantiate(idx + min, cIndices[val + n]);
      for (int i = 0; i < n; i++) {
        if (i != idx) {
          vars[i].removeVal(val + min, cIndices[i]);
        }
      }
    } else {
      vars[val].instantiate(idx - n + min, cIndices[val]);
      for (int i = n; i < 2 * n; i++) {
        if (i != idx) {
          vars[i].removeVal(val + min, cIndices[i]);
        }
      }
    }
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    if (idx < n) {
      vars[x - min + n].removeVal(idx + min, cIndices[x - min + n]);
    } else {
      vars[x - min].removeVal(idx - n + min, cIndices[x - min]);
    }
  }


  public boolean isSatisfied(int[] tuple) {
    for (int i = 0; i < n; i++) {
      int x = tuple[i];
      if (tuple[x - min + n] != i+ min) return false;
    }
    return true;
  }

  public String pretty() {
    StringBuilder sb = new StringBuilder();
    sb.append("InverseChanneling({");
    for (int i = 0; i <n; i++) {
      if (i > 0) sb.append(", ");
      IntDomainVar var = vars[i];
      sb.append(var.pretty());
    }
    sb.append("}, {");
    for (int i = 0; i < n; i++) {
      if (i > 0) sb.append(", ");
      IntDomainVar var = vars[n + i];
      sb.append(var.pretty());
    }
    sb.append("})");
    return sb.toString();
  }
}
