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
package choco.cp.solver.variables.integer;

import choco.kernel.common.util.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.propagation.VarEvent;
import choco.kernel.solver.variables.integer.IntDomain;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * History:
 * 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 */
public abstract class AbstractIntDomain implements IntDomain {

  /**
   * The (optimization or decision) solver to which the entity belongs.
   */

  public Solver solver;

  /**
   * Reference to an object for logging trace statements related to domains of search variables (using the java.util.logging package)
   */
  protected static Logger logger = Logger.getLogger("choco.kernel.solver.propagation");

  /**
   * The involved variable.
   */
  protected IntDomainVarImpl variable;

  /**
   * for the delta domain: current value of the inf (domain lower bound) when the bound started beeing propagated
   * (just to check that it does not change during the propagation phase)
   */
  protected int currentInfPropagated;

  /**
   * for the delta domain: current value of the sup (domain upper bound) when the bound started beeing propagated
   * (just to check that it does not change during the propagation phase)
   */
  protected int currentSupPropagated;

  protected DisposableIntIterator lastIterator;

  /**
   * Returns an getIterator.
   */

  public DisposableIntIterator getIterator() {
    IntDomainIterator iter = (IntDomainIterator) lastIterator;
    if (iter != null && iter.reusable) {
      iter.init();
      return iter;
    }
    lastIterator = new IntDomainIterator(this);
    return lastIterator;
  }

  protected static class IntDomainIterator extends DisposableIntIterator {
    protected AbstractIntDomain domain;
    protected int nextValue;
    protected int supBound = -1;

    private IntDomainIterator(AbstractIntDomain dom) {
      domain = dom;
      init();
    }

   @Override
   public void init() {
       super.init();
      if (domain.getSize() >= 1) {
        nextValue = domain.getInf();
      } else {
        throw new UnsupportedOperationException();
      }
      supBound = domain.getSup();
      //currentValue = Integer.MIN_VALUE; // dom.getInf();
    }

    public boolean hasNext() {
      return /*(Integer.MIN_VALUE == currentValue) ||*/ (nextValue <= supBound);
      // if currentValue equals MIN_VALUE it will be less than the upper bound => only one test is needed ! Moreover
      // MIN_VALUE is a special case, should not be tested if useless !
    }

    public int next() {
      int v = nextValue;
      nextValue = domain.getNextValue(nextValue);
      return v;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
  }

  /**
   * Internal var: update on the variable upper bound caused by its i-th
   * constraint.
   * Returns a boolean indicating whether the call indeed added new information.
   *
   * @param x   The new upper bound
   * @param idx The index of the constraint (among all constraints linked to
   *            the variable) responsible for the update
   * @return a boolean indicating whether the call indeed added new information.
   * @throws ContradictionException contradiction exception
   */
  public boolean updateSup(int x, int idx) throws ContradictionException {
    if (_updateSup(x, idx)) {
      int cause = VarEvent.NOCAUSE;
      int val = getInf();
      if (getSup() == x) cause = idx;
      if (val == getSup()) {
          //instantiate(getSup(), cause);
          restrict(val);
          variable.updateNbVarInstanciated();
          solver.getPropagationEngine().postInstInt(variable, cause);
      }
      else
        solver.getPropagationEngine().postUpdateSup(variable, cause);
      return true;
    } else
      return false;
  }

  /**
   * Internal var: update on the variable lower bound caused by its i-th
   * constraint.
   * Returns a boolean indicating whether the call indeed added new information
   *
   * @param x   The new lower bound.
   * @param idx The index of the constraint (among all constraints linked to
   *            the variable) responsible for the update.
   * @return a boolean indicating whether the call indeed added new information
   * @throws ContradictionException contradiction exception
   */

  public boolean updateInf(int x, int idx) throws ContradictionException {
    if (_updateInf(x, idx)) {
      int cause = VarEvent.NOCAUSE;
      int val = getSup();      
      if (getInf() == x) cause = idx;
      if (val == getInf()) {
//        instantiate(getInf(), cause);
          restrict(val);
          variable.updateNbVarInstanciated();
          solver.getPropagationEngine().postInstInt(variable, cause);
      } else
        solver.getPropagationEngine().postUpdateInf(variable, cause);
      // TODO      solver.getChocEngine().postUpdateInf(variable, cause, oldinf);
      return true;
    } else
      return false;
  }

  /**
   * Internal var: update (value removal) on the domain of a variable caused by
   * its i-th constraint.
   * <i>Note:</i> Whenever the hole results in a stronger var (such as a bound update or
   * an instantiation, then we forget about the index of the var generating constraint.
   * Indeed the propagated var is stronger than the initial one that
   * was generated; thus the generating constraint should be informed
   * about such a new var.
   * Returns a boolean indicating whether the call indeed added new information.
   *
   * @param x   The removed value
   * @param idx The index of the constraint (among all constraints linked to the variable) responsible for the update
   * @return a boolean indicating whether the call indeed added new information.
   * @throws ContradictionException contradiction exception
   */

  public boolean removeVal(int x, int idx) throws ContradictionException {
    if (_removeVal(x, idx)) {
      if (logger.isLoggable(Level.FINEST))
        logger.finest("REM(" + this.toString() + "): " + x);
      // TODO : to test !!
      //int promoteCause = variable.getEvent().getCause() == VarEvent.NOEVENT ? idx : VarEvent.NOCAUSE;
      //int promoteCause = idx;
      int promoteCause = VarEvent.NOCAUSE;
      if (getInf() == getSup())
        solver.getPropagationEngine().postInstInt(variable, promoteCause);
      else if (x < getInf())
        solver.getPropagationEngine().postUpdateInf(variable, promoteCause);
      else if (x > getSup())
        solver.getPropagationEngine().postUpdateSup(variable, promoteCause);
      else
        solver.getPropagationEngine().postRemoveVal(variable, x, idx);
      return true;
    } else
      return false;
  }


  /**
   * Internal var: remove an interval (a sequence of consecutive values) from
   * the domain of a variable caused by its i-th constraint.
   * Returns a boolean indicating whether the call indeed added new information.
   *
   * @param a   the first removed value
   * @param b   the last removed value
   * @param idx the index of the constraint (among all constraints linked to the variable)
   *            responsible for the update
   * @return a boolean indicating whether the call indeed added new information.
   * @throws ContradictionException contradiction exception
   */

  public boolean removeInterval(int a, int b, int idx) throws ContradictionException {
    if (a <= getInf())
      return updateInf(b + 1, idx);
    else if (getSup() <= b)
      return updateSup(a - 1, idx);
    else if (variable.hasEnumeratedDomain()) {     // TODO: really ugly .........
      boolean anyChange = false;
      for (int v = getNextValue(a - 1); v <= b; v = getNextValue(v)) {
        //for (int v = a; v <= b; v++) {
        anyChange |= removeVal(v, idx);
      }
      return anyChange;
    } else
      return false;
  }

  /**
   * Internal var: instantiation of the variable caused by its i-th constraint
   * Returns a boolean indicating whether the call indeed added new information.
   *
   * @param x   the new upper bound
   * @param idx the index of the constraint (among all constraints linked to the
   *            variable) responsible for the update
   * @return a boolean indicating whether the call indeed added new information.
   * @throws ContradictionException contradiction exception
   */

  public boolean instantiate(int x, int idx) throws ContradictionException {
    if (_instantiate(x, idx)) {
      if (logger.isLoggable(Level.FINEST))
        logger.finest("INST(" + this.toString() + "): " + x);
      solver.getPropagationEngine().postInstInt(variable, idx);
      return true;
    } else
      return false;
  }

// ============================================
  // Private methods for maintaining the
  // domain.
  // ============================================

  /**
   * Instantiating a variable to an search value. Returns true if this was
   * a real modification or not
   *
   * @param x the new instantiate value
   * @return wether it is a real modification or not
   * @throws ContradictionException contradiction exception
   */

  protected boolean _instantiate(int x, int idx) throws ContradictionException {
    if (variable.isInstantiated()) {
      if (variable.getVal() != x) {
        if (idx == -1)
          this.getSolver().getPropagationEngine().raiseContradiction(this.variable, ContradictionException.VARIABLE);
        else
          this.getSolver().getPropagationEngine().raiseContradiction(variable.getConstraintVector().get(idx), ContradictionException.CONSTRAINT);
        return true; // Just for compilation !
      } else return false;
    } else {
      if (x < getInf() || x > getSup() || !contains(x)) { // GRT : we need to check bounds
        // since contains suppose trivial bounds
        // are containing tested value !!
        if (idx == -1)
          this.getSolver().getPropagationEngine().raiseContradiction(this.variable, ContradictionException.VARIABLE);
        else
          this.getSolver().getPropagationEngine().raiseContradiction(variable.getConstraintVector().get(idx), ContradictionException.CONSTRAINT);
        return true; // Just for compilation !
      } else {
        restrict(x);
      }
      //FR_1873619 CPRU: DomOverDeg+DomOverWDeg
      variable.updateNbVarInstanciated();
      return true;
    }
  }


  /**
   * Improving the lower bound.
   *
   * @param x the new lower bound
   * @return a boolean indicating wether the update has been done
   * @throws ContradictionException contradiction exception
   */

  // note: one could have thrown an OutOfDomainException in case (x > IStateInt.MAXINT)
  protected boolean _updateInf(int x, int idx) throws ContradictionException {
    if (x > getInf()) {
      if (x > getSup()) {
        if (idx == -1)
          this.getSolver().getPropagationEngine().raiseContradiction(this.variable, ContradictionException.VARIABLE);
        else
          this.getSolver().getPropagationEngine().raiseContradiction(variable.getConstraintVector().get(idx), ContradictionException.CONSTRAINT);
        return true; // Just for compilation !
      } else {
        updateInf(x);
        return true;
      }
    } else {
      return false;
    }
  }


  /**
   * Improving the upper bound.
   *
   * @param x the new upper bound
   * @return wether the update has been done
   * @throws ContradictionException contradiction exception
   */
  protected boolean _updateSup(int x, int idx) throws ContradictionException {
    if (x < getSup()) {
      if (x < getInf()) {
        if (idx == -1)
          this.getSolver().getPropagationEngine().raiseContradiction(this.variable, ContradictionException.VARIABLE);
        else
          this.getSolver().getPropagationEngine().raiseContradiction(variable.getConstraintVector().get(idx), ContradictionException.CONSTRAINT);
        return true; // Just for compilation !
      } else {
        updateSup(x);
        return true;
      }
    } else {
      return false;
    }
  }

  /**
   * Removing a value from the domain of a variable. Returns true if this
   * was a real modification on the domain.
   *
   * @param x the value to remove
   * @return wether the removal has been done
   * @throws ContradictionException contradiction excpetion
   */
  protected boolean _removeVal(int x, int idx) throws ContradictionException {
    int infv = getInf(), supv = getSup();
    if (infv <= x && x <= supv) {
      if (x == infv) {
        _updateInf(x + 1, idx);
        if (getInf() == supv) {
            restrict(supv);
            variable.updateNbVarInstanciated();
            //_instantiate(supv, idx);
        }
        return true;
      } else if (x == supv) {
        _updateSup(x - 1, idx);
        if (getSup() == infv) {
            restrict(infv);
            variable.updateNbVarInstanciated();            
            //_instantiate(infv, idx);
        }
        return true;
      } else {
        return remove(x);
      }
    } else {
      return false;
    }
  }

  public void freezeDeltaDomain() {
    currentInfPropagated = getInf();
    currentSupPropagated = getSup();
  }

  /**
   * release the delta domain
   *
   * @return wether it was a new update
   */
  public boolean releaseDeltaDomain() {
    boolean noNewUpdate = ((getInf() == currentInfPropagated) && (getSup() == currentSupPropagated));
    currentInfPropagated = Integer.MIN_VALUE;
    currentSupPropagated = Integer.MAX_VALUE;
    return noNewUpdate;
  }

  public void clearDeltaDomain() {
    currentInfPropagated = Integer.MIN_VALUE;
    currentSupPropagated = Integer.MAX_VALUE;
  }

  /**
   * @return a boolean
   */
  public boolean getReleasedDeltaDomain() {
    return true;
  }

  /**
   * Retrieves the solver of the entity
   */

  public Solver getSolver() {
    return solver;
  }

  public void setSolver(Solver solver) {
    this.solver = solver;
  }
}
