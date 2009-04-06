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
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * a simple channeling constraint :
 * y_ij = 1 si x_i = j
 * y_ij = 0 sinon
 */
public class BooleanChanneling extends AbstractBinIntConstraint {

  protected int cste;

  public BooleanChanneling(IntDomainVar yij, IntDomainVar xi, int j) {
    super(yij, xi);
    this.cste = j;
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

  public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
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
