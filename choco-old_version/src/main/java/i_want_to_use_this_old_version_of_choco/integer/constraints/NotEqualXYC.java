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
import i_want_to_use_this_old_version_of_choco.util.Arithm;

import java.util.logging.Level;

/**
 * Implements a constraint X !== Y + C, with X and Y two variables and C a constant.
 */
public class NotEqualXYC extends AbstractBinIntConstraint {

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

  public NotEqualXYC(IntDomainVar x0, IntDomainVar x1, int c) {
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
    if (v0.isInstantiated()) {
      if (logger.isLoggable(Level.FINEST))
        logger.finest("VAL(" + v1.toString() + ") != VAL(" + v0.toString() + ") - " + this.cste);
      v1.removeVal(v0.getVal() - this.cste, this.cIdx1);
    } else if (v1.isInstantiated()) {
      if (logger.isLoggable(Level.FINEST))
        logger.finest("VAL(" + v0.toString() + ") != VAL(" + v1.toString() + ") + " + this.cste);
      v0.removeVal(v1.getVal() + this.cste, this.cIdx0);
    }
  }

  public void awakeOnInf(int idx) throws ContradictionException {
    if (v0.isInstantiated()) {
      if (logger.isLoggable(Level.FINEST))
        logger.finest("VAL(" + v1.toString() + ") != VAL(" + v0.toString() + ") - " + this.cste);
      v1.removeVal(v0.getVal() - this.cste, this.cIdx1);
    } else if (v1.isInstantiated()) {
      if (logger.isLoggable(Level.FINEST))
        logger.finest("VAL(" + v0.toString() + ") != VAL(" + v1.toString() + ") + " + this.cste);
      v0.removeVal(v1.getVal() + this.cste, this.cIdx0);
    }
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    if (v0.isInstantiated()) {
      if (logger.isLoggable(Level.FINEST))
        logger.finest("VAL(" + v1.toString() + ") != VAL(" + v0.toString() + ") - " + this.cste);
      v1.removeVal(v0.getVal() - this.cste, this.cIdx1);
    } else if (v1.isInstantiated()) {
      if (logger.isLoggable(Level.FINEST))
        logger.finest("VAL(" + v0.toString() + ") != VAL(" + v1.toString() + ") + " + this.cste);
      v0.removeVal(v1.getVal() + this.cste, this.cIdx0);
    }
  }

  public void awakeOnInst(int idx) throws ContradictionException {
    if (idx == 0) {
      if (logger.isLoggable(Level.FINEST))
        logger.finest("VAL(" + v1.toString() + ") != VAL(" + v0.toString() + ") - " + this.cste);
      v1.removeVal(v0.getVal() - this.cste, this.cIdx1);
    } else {
      assert(idx == 1);
      if (logger.isLoggable(Level.FINEST))
        logger.finest("VAL(" + v0.toString() + ") != VAL(" + v1.toString() + ") + " + this.cste);
      v0.removeVal(v1.getVal() + this.cste, this.cIdx0);
    }
  }

  public void awakeOnVar(int idx) throws ContradictionException {
    ;
  }

  /**
   * Nothing to do when only a hole in a domain is made
   */

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    ;
  }


  /**
   * Checks if the listeners must be checked or must fail.
   */

  public Boolean isEntailed() {
    if ((v0.getSup() < v1.getInf() + this.cste) ||
        (v1.getSup() < v0.getInf() - this.cste))
      return Boolean.TRUE;
    else if ((v0.getInf() == v0.getSup()) &&
        (v1.getInf() == v1.getSup()) &&
        (v0.getInf() == v1.getInf() + this.cste))
      return Boolean.FALSE;
    else
      return null;
  }

  /**
   * Checks if the constraint is satisfied when the variables are instantiated.
   */

  public boolean isSatisfied(int[] tuple) {
    return (tuple[0] != tuple[1] + this.cste);
  }

  /**
   * tests if the constraint is consistent with respect to the current state of domains
   *
   * @return true iff the constraint is bound consistent (weaker than arc consistent)
   */
  public boolean isConsistent() {
    return ((v0.isInstantiated()) ?
        ((v1.hasEnumeratedDomain()) ?
        (!v1.canBeInstantiatedTo(v0.getVal())) :
        ((v1.getInf() != v0.getVal()) && (v1.getSup() != v0.getVal()))) :
        ((v1.isInstantiated()) ?
        ((v0.hasEnumeratedDomain()) ?
        (!v0.canBeInstantiatedTo(v1.getVal())) :
        ((v0.getInf() != v1.getVal()) && (v0.getSup() != v1.getVal()))) :
        true));
  }

  public AbstractConstraint opposite() {
//    return new EqualXYC(v0, v1, cste);
    AbstractProblem pb = getProblem();
    return (AbstractConstraint) pb.eq(v0, pb.plus(v1, cste));
  }

  public final boolean isEquivalentTo(Constraint compareTo) {
    if (compareTo instanceof NotEqualXYC) {
      NotEqualXYC c = (NotEqualXYC) compareTo;
      return (((this.v0 == c.v0) && (this.v1 == c.v1) && (this.cste == c.cste)) ||
          ((this.v0 == c.v1) && (this.v1 == c.v0) && (this.cste == -(c.cste))));
    } else {
      return false;
    }
  }

  public String pretty() {
    StringBuffer sb = new StringBuffer();
    sb.append(v0.toString());
    sb.append(" != ");
    sb.append(v1.toString());
    sb.append(Arithm.pretty(this.cste));
    return sb.toString();
  }

}
