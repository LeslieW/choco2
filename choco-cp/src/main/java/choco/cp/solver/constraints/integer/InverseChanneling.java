/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package choco.cp.solver.constraints.integer;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.propagation.event.ConstraintEvent;
import choco.kernel.solver.variables.integer.IntDomainVar;

public final class InverseChanneling extends AbstractLargeIntSConstraint {

  protected int n;

    private int min;

    /**
   * link x and y so that x[i] = j <=> y[j] = i
   * It is used to maintain both model on permutation problems
   */
  public InverseChanneling(IntDomainVar[] allVars, int n) {
    super(ConstraintEvent.CUBIC, allVars);
    this.min = allVars[0].getInf();
    this.n = n;
  }


    @Override
    public int getFilteredEventMask(int idx) {
        if (vars[idx].hasEnumeratedDomain()) {
            return IntVarEvent.INSTINT_MASK + IntVarEvent.REMVAL_MASK;
        } else {
            return IntVarEvent.INSTINT_MASK + IntVarEvent.BOUNDS_MASK;
        }
    }

  public void propagate() throws ContradictionException {
    for (int idx = 0; idx < vars.length; idx++) {
      for (int i = 0; i < n; i++) {
        if (idx < n && !vars[idx].canBeInstantiatedTo(i + min)) {
          vars[i + n].removeVal(idx + min, this, false);
        } else if (!vars[idx].canBeInstantiatedTo(i + min)) {
          vars[i].removeVal(idx - n + min, this, false);
        }
      }
    }
  }


  public void awakeOnInf(int idx) throws ContradictionException {
    int val = vars[idx].getInf() - min;
    if (idx < n) {
      for (int i = 0; i < val; i++) {
        vars[i + n].removeVal(idx + min, this, false);
      }
    } else {
      for (int i = 0; i < val; i++) {
        vars[i].removeVal(idx - n + min, this, false);
      }
    }
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    int val = vars[idx].getSup() + 1 - min;
    if (idx < n) {
      for (int i = val; i < n; i++) {
        vars[i + n].removeVal(idx + min, this, false);
      }
    } else {
      for (int i = val; i < n; i++) {
        vars[i].removeVal(idx - n + min, this, false);
      }
    }    //To change body of overridden methods use File | Settings | File Templates.
  }

  public void awakeOnInst(int idx) throws ContradictionException {
    int val = vars[idx].getVal() - min;
    if (idx < n) {
      vars[val + n].instantiate(idx + min, this, false);
      for (int i = 0; i < n; i++) {
        if (i != idx) {
          vars[i].removeVal(val + min, this, false);
        }
      }
    } else {
      vars[val].instantiate(idx - n + min, this, false);
      for (int i = n; i < 2 * n; i++) {
        if (i != idx) {
          vars[i].removeVal(val + min, this, false);
        }
      }
    }
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    if (idx < n) {
      vars[x - min + n].removeVal(idx + min, this, false);
    } else {
      vars[x - min].removeVal(idx - n + min, this, false);
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
