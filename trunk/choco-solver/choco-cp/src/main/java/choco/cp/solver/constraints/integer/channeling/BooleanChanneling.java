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


package choco.cp.solver.constraints.integer.channeling;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractBinIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * a simple channeling constraint :
 * y_ij = 1 si x_i = j
 * y_ij = 0 sinon
 */
public class BooleanChanneling extends AbstractBinIntSConstraint {

  protected int cste;

  public BooleanChanneling(IntDomainVar yij, IntDomainVar xi, int j) {
    super(yij, xi);
    this.cste = j;
  }

    @Override
    public int getFilteredEventMask(int idx) {
        if(idx == 0){
            if(v0.hasEnumeratedDomain()){
                return IntVarEvent.REMVALbitvector;
            }else{
                return IntVarEvent.BOUNDSbitvector;
            }
        }else{
            if(v1.hasEnumeratedDomain()){
                return IntVarEvent.REMVALbitvector;
            }else{
                return IntVarEvent.BOUNDSbitvector;
            }
        }
    }

    public void filterFromBtoX() throws ContradictionException {
    if (v0.isInstantiated()) {
      if (v0.isInstantiatedTo(0)) { // on retire la valeur j de x;
        v1.removeVal(cste, cIdx1);
      } else { // on instancie x � j
        v1.instantiate(cste, cIdx1);
      }
    }
  }

  public void filterFromXtoB() throws ContradictionException {
    if (v1.canBeInstantiatedTo(cste)) {
      if (v1.isInstantiatedTo(cste)) { // on instancie y_ij � 1
        v0.instantiate(1, cIdx0);
      }
    } else {  // on instancie y_ij � 0;
      v0.instantiate(0, cIdx0);
    }
  }

  public void propagate() throws ContradictionException {
    filterFromXtoB();
    filterFromBtoX();
  }

  public void awakeOnInf(int idx) throws ContradictionException {
    if (idx == 0) {
      filterFromBtoX();
    } else {
      filterFromXtoB();
    }
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    if (idx == 0) {
      filterFromBtoX();
    } else {
      filterFromXtoB();
    }
  }

  public void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
    if (idx == 0) {
      filterFromBtoX();
    } else {
      filterFromXtoB();
    }
  }

  public Boolean isEntailed() {
    if (!v1.canBeInstantiatedTo(cste)) {
      return Boolean.TRUE;
    } else
      return null;
  }

  public boolean isSatisfied(int[] tuple) {
    int val = tuple[1];
    return (val == cste && tuple[0] == 1) || (val != cste && tuple[0] == 0);
  }

  public String pretty() {
    return "(" + v0.pretty() + " = 1)  <=> (" + v1.pretty() + " = " + cste + ")";
  }
}
