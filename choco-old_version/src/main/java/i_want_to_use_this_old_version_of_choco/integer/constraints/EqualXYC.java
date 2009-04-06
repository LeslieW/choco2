// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.integer.constraints;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomain;
import i_want_to_use_this_old_version_of_choco.util.Arithm;

import java.util.logging.Level;

/**
 * Implements a constraint X == Y + C, with X and Y two variables and C a constant.
 */
public class EqualXYC extends AbstractBinIntConstraint {

  /**
   * The search constant of the constraint
   */
  protected final int cste;

  /**
   * Constructs the constraint with the specified variables and constant.
   *
   * @param x0 first IntDomainVar
   * @param x1 second IntDomainVar
   * @param c  The search constant used in the disequality.
   */

  public EqualXYC(IntDomainVar x0, IntDomainVar x1, int c) {
    super(x0, x1);
    this.cste = c;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * The one and only propagation method, using foward checking
   */

  public void propagate() throws ContradictionException {
    if (logger.isLoggable(Level.FINEST))
      logger.finest("INF(" + v0.toString() + ") >= INF(" + v1.toString() + ") + " + cste + " = " + (v1.getInf() + cste));
    v0.updateInf(v1.getInf() + cste, cIdx0);
    if (logger.isLoggable(Level.FINEST))
      logger.finest("SUP(" + v0.toString() + ") <= SUP(" + v1.toString() + ") + " + cste + " = " + (v1.getSup() + cste));
    v0.updateSup(v1.getSup() + cste, cIdx0);
    if (logger.isLoggable(Level.FINEST))
      logger.finest("INF(" + v1.toString() + ") >= INF(" + v0.toString() + ") - " + cste + " = " + (v0.getInf() - cste));
    v1.updateInf(v0.getInf() - cste, cIdx1);
    if (logger.isLoggable(Level.FINEST))
      logger.finest("SUP(" + v1.toString() + ") <= SUP(" + v0.toString() + ") - " + cste + " = " + (v0.getSup() - cste));
    v1.updateSup(v0.getSup() - cste, cIdx1);

    // ensure that, in case of enumerated domains, holes are also propagated
    if (v1.hasEnumeratedDomain() && v0.hasEnumeratedDomain()) {
      IntDomain dom0 = v0.getDomain();
      int val0;
      for (val0 = dom0.getInf(); dom0.hasNextValue(val0); val0 = dom0.getNextValue(val0)) {
        if (!(v1.canBeInstantiatedTo(val0 - cste))) {
          if (logger.isLoggable(Level.FINEST))
            logger.finest(v0.toString() + " = " + v1.toString() + ") + " + cste + " != " + val0);
          v0.removeVal(val0, cIdx0);
        }
      }

      IntDomain dom1 = v1.getDomain();
      int val1;
      for (val1 = dom1.getInf(); dom1.hasNextValue(val1); val1 = dom1.getNextValue(val1)) {
        if (!(v0.canBeInstantiatedTo(val1 + cste))) {
          if (logger.isLoggable(Level.FINEST))
            logger.finest(v1.toString() + " = " + v0.toString() + ") - " + cste + " != " + val1);
          v1.removeVal(val1, cIdx1);
        }
      }
    }
  }


  public void awakeOnInf(int idx) throws ContradictionException {
    if (idx == 0) {
      if (logger.isLoggable(Level.FINEST))
        logger.finest("INF(" + v1.toString() + ") >= INF(" + v0.toString() + ") - " + cste + " = " + (v0.getInf() - cste));
      v1.updateInf(v0.getInf() - cste, cIdx1);
    } else {
      if (logger.isLoggable(Level.FINEST))
        logger.finest("INF(" + v0.toString() + ") >= INF(" + v1.toString() + ") + " + cste + " = " + (v1.getInf() + cste));
      v0.updateInf(v1.getInf() + cste, cIdx0);
    }
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    if (idx == 0) {
      if (logger.isLoggable(Level.FINEST))
        logger.finest("SUP(" + v1.toString() + ") <= SUP(" + v0.toString() + ") - " + cste + " = " + (v0.getSup() - cste));
      v1.updateSup(v0.getSup() - cste, cIdx1);
    } else {
      if (logger.isLoggable(Level.FINEST))
        logger.finest("SUP(" + v0.toString() + ") <= SUP(" + v1.toString() + ") + " + cste + " = " + (v1.getSup() + cste));
      v0.updateSup(v1.getSup() + cste, cIdx0);
    }
  }

  public void awakeOnInst(int idx) throws ContradictionException {
    if (idx == 0) {
      if (logger.isLoggable(Level.FINEST))
        logger.finest(v1.toString() + " = " + v0.toString() + ") - " + cste + " = " + (v0.getVal() - cste));
      v1.instantiate(v0.getVal() - cste, cIdx1);
    } else {
      if (logger.isLoggable(Level.FINEST))
        logger.finest(v0.toString() + " = " + v1.toString() + ") + " + cste + " = " + (v1.getVal() + cste));
      v0.instantiate(v1.getVal() + cste, cIdx0);
    }
  }

  public void awakeOnVar(int idx) throws ContradictionException {
    if (idx == 0) {
      if (logger.isLoggable(Level.FINEST))
        logger.finest(v1.toString() + " = " + v0.toString() + ") - " + cste + " within [" + (v0.getInf() - cste) + ", " + (v0.getSup() - cste) + "]");
      v1.updateInf(v0.getInf() - cste, cIdx1);
      v1.updateSup(v0.getSup() - cste, cIdx1);
      if (v1.hasEnumeratedDomain() && v0.hasEnumeratedDomain()) {
        IntDomain dom1 = v1.getDomain();
        int val1;
        for (val1 = dom1.getInf(); dom1.hasNextValue(val1); val1 = dom1.getNextValue(val1)) {
          if (!(v0.canBeInstantiatedTo(val1 + cste))) {
            if (logger.isLoggable(Level.FINEST))
              logger.finest(v1.toString() + " = " + v0.toString() + ") - " + cste + " != " + val1);
            v1.removeVal(val1, cIdx1);
          }
        }
      }
    } else {
      assert(idx == 1);
      v0.updateInf(v1.getInf() + cste, cIdx0);
      v0.updateSup(v1.getSup() + cste, cIdx0);
      if (v1.hasEnumeratedDomain() && v0.hasEnumeratedDomain()) {
        IntDomain dom0 = v0.getDomain();
        int val0;
        for (val0 = dom0.getInf(); dom0.hasNextValue(val0); val0 = dom0.getNextValue(val0)) {
          if (!(v1.canBeInstantiatedTo(val0 - cste))) {
            if (logger.isLoggable(Level.FINEST))
              logger.finest(v0.toString() + " = " + v1.toString() + ") + " + cste + " != " + val0);
            v0.removeVal(val0, cIdx0);
          }
        }
      }
    }
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    if (idx == 0) {
      if (logger.isLoggable(Level.FINEST))
        logger.finest(v1.toString() + " = " + v0.toString() + ") - " + cste + " != " + (x - cste));
      v1.removeVal(x - cste, cIdx1);
    } else {
      assert(idx == 1);
      if (logger.isLoggable(Level.FINEST))
        logger.finest(v0.toString() + " = " + v1.toString() + ") + " + cste + " != " + (x + cste));
      v0.removeVal(x + cste, cIdx0);
    }
  }

  /**
   * Checks if the listeners must be checked or must fail.
   */

  public Boolean isEntailed() {
    if ((v0.getSup() < v1.getInf() + cste) ||
        (v0.getInf() > v1.getSup() + cste))
      return Boolean.FALSE;
    else if (v0.isInstantiated() &&
        v1.isInstantiated() &&
        (v0.getVal() == v1.getVal()))
      return Boolean.TRUE;
    else
      return null;
  }

  /**
   * Checks if the constraint is satisfied when the variables are instantiated.
   */

  public boolean isSatisfied(int[] tuple) {
    return (tuple[0] == tuple[1] + this.cste);
  }

  /**
   * tests if the constraint is consistent with respect to the current state of domains
   *
   * @return true iff the constraint is bound consistent (weaker than arc consistent)
   */
  public boolean isConsistent() {
    return ((v0.getInf() == v1.getInf() + cste) && (v0.getSup() == v1.getSup() + cste));
  }

  public AbstractConstraint opposite() {
    AbstractProblem pb = getProblem();
    return (AbstractConstraint) pb.neq(v0, pb.plus(v1, cste));
    // return NotEqualXYC(v0, v1, cste);
  }

  public final boolean isEquivalentTo(Constraint compareTo) {
    if (compareTo instanceof EqualXYC) {
      EqualXYC c = (EqualXYC) compareTo;
      return (((this.v0 == c.v0) && (this.v1 == c.v1) && (this.cste == c.cste)) ||
          ((this.v0 == c.v1) && (this.v1 == c.v0) && (this.cste == -(c.cste))));
    } else {
      return false;
    }
  }

  public String pretty() {
    StringBuffer sb = new StringBuffer();
    sb.append(v0.toString());
    sb.append(" = ");
    sb.append(v1.toString());
    sb.append(Arithm.pretty(this.cste));
    return sb.toString();
  }

}
