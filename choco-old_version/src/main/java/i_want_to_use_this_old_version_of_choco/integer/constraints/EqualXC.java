// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.integer.constraints;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl;

import java.util.logging.Level;

/**
 * Implements a constraint X = C, with X a variable and C a constant.
 */
public class EqualXC extends AbstractUnIntConstraint {

  /**
   * The search constant of the constraint
   */
  protected final int cste;

  /**
   * Constructs the constraint with the specified variables and constant.
   *
   * @param x0 the search valued domain variable
   * @param c  the search constant used in the equality.
   */

  public EqualXC(IntDomainVar x0, int c) {
    this.v0 = (IntDomainVarImpl) x0;
    this.cste = c;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * Pretty print of the constraint.
   */

  public String pretty() {
    return this.v0 + " == " + cste;
  }

  /**
   * The one and only propagation method. <br>
   * Note that after the first propagation, the constraint could be set passive
   * (to prevent from further calls to propagation methods), but this management seems too heavy to be worthwhile
   */

  public void propagate() throws ContradictionException {
    if (logger.isLoggable(Level.FINEST))
      logger.finest("VAL(" + v0.toString() + ") = " + this.cste);
    v0.instantiate(this.cste, this.cIdx0);
  }

  public void awakeOnVar(int idx) throws ContradictionException {
    if (logger.isLoggable(Level.FINEST))
      logger.finest("VAL(" + v0.toString() + ") = " + this.cste);
    v0.instantiate(this.cste, this.cIdx0);
  }

  public void awakeOnInf(int idx) throws ContradictionException {
    if (logger.isLoggable(Level.FINEST))
      logger.finest("VAL(" + v0.toString() + ") = " + this.cste);
    v0.instantiate(this.cste, this.cIdx0);
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    if (logger.isLoggable(Level.FINEST))
      logger.finest("VAL(" + v0.toString() + ") = " + this.cste);
    v0.instantiate(this.cste, this.cIdx0);
  }

  public void awakeOnInst(int idx) throws ContradictionException {
    if (logger.isLoggable(Level.FINEST))
      logger.finest("VAL(" + v0.toString() + ") = " + this.cste);
    v0.instantiate(this.cste, this.cIdx0);
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    if (logger.isLoggable(Level.FINEST))
      logger.finest("VAL(" + v0.toString() + ") = " + this.cste);
    v0.instantiate(this.cste, this.cIdx0);
  }


  /**
   * Checks if the listeners must be checked or must fail.
   */

  public Boolean isEntailed() {
    if (!v0.canBeInstantiatedTo(cste)) {
      return Boolean.FALSE;
    } else if (v0.isInstantiatedTo(cste)) {
      return Boolean.TRUE;
    }
    else {
      return null;
    }
  }

  /**
   * Checks if the constraint is satisfied when the variables are instantiated.
   */

  public boolean isSatisfied(int[] tuple) {
    return (tuple[0] == this.cste);
  }

  /**
   * tests if the constraint is consistent with respect to the current state of domains
   *
   * @return true iff the constraint is bound consistent (same as arc consistent)
   */
  public boolean isConsistent() {
    return (v0.isInstantiatedTo(this.cste));
  }

  public AbstractConstraint opposite() {
    return (AbstractConstraint) getProblem().neq(v0, cste);
    // return NotEqualXC(v0, cste);
  }

  public final boolean isEquivalentTo(Constraint compareTo) {
    if (compareTo instanceof EqualXC) {
      EqualXC c = (EqualXC) compareTo;
      return ((this.v0 == c.v0) && (this.cste == c.cste));
    } else {
      return false;
    }
  }

}
