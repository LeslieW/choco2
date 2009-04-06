package i_want_to_use_this_old_version_of_choco.integer.var;
/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|       Choco-Solver.net    *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco-solver.net        *
 *     + support : support@chocosolver.net        *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                    N. Jussien   1999-2008      *
 **************************************************/
import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.AbstractVar;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;
import i_want_to_use_this_old_version_of_choco.prop.VarEvent;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.logging.Level;
import java.util.logging.Logger;
/** History:
 * 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 * */
/**
 * Implements search valued domain variables.
 */
public class IntDomainVarImpl extends AbstractVar implements IntDomainVar {

  /**
   * The backtrackable value of the variable, if instantiated.
   */

  public final IStateInt value; // voir si il faut le storer...


  /**
   * The backtrackable domain of the variable.
   */

  protected AbstractIntDomain domain;

  /**
   * Reference to an object for logging trace statements related to IntDomainVar (using the java.util.logging package)
   */

  protected static Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop");

  /**
   * Constructs a new variable for the specified problem and with the
   * specified name and bounds.
   *
   * @param pb         The problem of the variable.
   * @param name       Its name.
   * @param domainType the type of encoding for the domain (BOUNDS, BITSET, ...)
   * @param a          Its minimal value.
   * @param b          Its maximal value.
   */

  public IntDomainVarImpl(AbstractProblem pb, String name, int domainType, int a, int b) {
    super(pb, name);
    IEnvironment env = pb.getEnvironment();

    if (domainType == IntDomainVar.BITSET) {
      domain = new BitSetIntDomain(this, a, b);
    } else if (domainType == IntDomainVar.LINKEDLIST) {
        domain = new LinkedIntDomain(this, a, b);
    } else if (domainType == IntDomainVar.INTERVALLIST) {
        domain = new IntervalListDomain(this, a, b);
    } else {
      domain = new IntervalIntDomain(this, a, b);
    }
    this.value = env.makeInt();
    if (a == b) this.value.set(a);
    this.event = new IntVarEvent(this);
  }

  public IntDomainVarImpl(AbstractProblem pb, String name, int[] sortedValues) {
    super(pb, name);
    IEnvironment env = pb.getEnvironment();

    domain = new BitSetIntDomain(this, sortedValues);
    this.value = env.makeInt();
    if (sortedValues.length == 1) this.value.set(sortedValues[0]);
    this.event = new IntVarEvent(this);
  }


  // ============================================
  // Methods of the interface
  // ============================================

  /**
   * Checks if the variable is instantiated to a specific value.
   */

  public boolean isInstantiatedTo(int x) {
    return (getVal() == x);
  }


  /**
   * Checks if the variables is instantiated to any value.
   */

  public boolean isInstantiated() {
    return (value.isKnown());
  }


  /**
   * Checks if a value is still in the domain.
   */

  public boolean canBeInstantiatedTo(int x) {
    //return domain.contains(x);
    return (getInf() <= x && x <= getSup() && (domain == null || domain.contains(x)));
  }


  /**
   * Sets the minimum value.
   */

  public void setInf(int x) throws ContradictionException {
    updateInf(x, VarEvent.NOCAUSE);
  }

  /**
   * @deprecated replaced by setInf
   */
  public void setMin(int x) throws ContradictionException {
    updateInf(x, VarEvent.NOCAUSE);
  }

  /**
   * Sets the maximal value.
   */

  public void setSup(int x) throws ContradictionException {
    updateSup(x, VarEvent.NOCAUSE);
  }

  /**
   * @deprecated replaced by setSup
   */
  public void setMax(int x) throws ContradictionException {
    updateSup(x, VarEvent.NOCAUSE);
  }

  /**
   * Instantiates the variable.
   *
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
   */

  public void setVal(int x) throws ContradictionException {
    instantiate(x, VarEvent.NOCAUSE);
  }


  /**
   * Removes a value.
   *
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
   */

  public void remVal(int x) throws ContradictionException {
    removeVal(x, VarEvent.NOCAUSE);
  }

  public void wipeOut() throws ContradictionException {
    throw new ContradictionException(this);
  }

  public boolean hasEnumeratedDomain() {
    return domain.isEnumerated();
  }

  public boolean hasBooleanDomain() {
    return domain.isBoolean();
  }

  public IntDomain getDomain() {
    return domain;
  }

  /**
   * Gets the domain size.
   */

  public int getDomainSize() {
    return domain.getSize();
  }

  /**
   * Checks if it can be equals to another variable.
   */

  public boolean canBeEqualTo(IntDomainVar x) {
    if (x.getInf() <= this.getSup()) {
      if (this.getInf() <= x.getSup()) {
        if (!this.hasEnumeratedDomain() || !x.hasEnumeratedDomain())
          return true;
        else {
          for (IntIterator it = this.getDomain().getIterator(); it.hasNext();) {
            int v = it.next();
            if (x.canBeInstantiatedTo(v))
              return true;
          }
          return false;
        }
      } else
        return false;
    } else
      return false;
  }


  /**
   * Checks if the variables can be instantiated to at least one value
   * in the array.
   *
   * @param sortedValList The value array.
   * @param nVals         The number of interesting value in this array.
   */

  public boolean canBeInstantiatedIn(int[] sortedValList, int nVals) {
    if (getInf() <= sortedValList[nVals - 1]) {
      if (getSup() >= sortedValList[0]) {
        if (domain == null)
          return true;
        else {
          for (int i = 0; i < nVals; i++) {
            if (canBeInstantiatedTo(sortedValList[i]))
              return true;
          }
          return false;
        }
      } else
        return false;
    } else
      return false;
  }


  /**
   * Returns a randomly choosed value in the domain.
   * <p/>
   * Not implemented yet.
   */

  public int getRandomDomainValue() {
    if (domain == null)
      return getInf();
// TODO     return inf.get() + random(sup.get() - inf.get() + 1);
    else
      return domain.getRandomValue();
  }


  /**
   * Gets the next value in the domain.
   */

  public int getNextDomainValue(int currentv) {
    if (currentv < getInf())
      return getInf();
    else if (domain == null)
      return currentv + 1;
    else
      return domain.getNextValue(currentv);
  }


  /**
   * Gets the previous value in the domain.
   */

  public int getPrevDomainValue(int currentv) {
    if (currentv > getSup())
      return getSup();
    else if (domain == null)
      return currentv - 1;
    else
      return domain.getPrevValue(currentv);
  }


  /**
   * Internal var: update on the variable lower bound caused by its i-th
   * constraint.
   * Returns a boolean indicating whether the call indeed added new information
   *
   * @param x   The new lower bound.
   * @param idx The index of the constraint (among all constraints linked to
   *            the variable) responsible for the update.
   */

  public boolean updateInf(int x, int idx) throws ContradictionException {
          if (logger.isLoggable(Level.FINEST))
            logger.finest("INF(" + this.toString() + "): " + this.getInf() + " -> " + x);
          return domain.updateInf(x, idx);
  }

  /**
   * Internal var: update on the variable upper bound caused by its i-th
   * constraint.
   * Returns a boolean indicating whether the call indeed added new information.
   *
   * @param x   The new upper bound
   * @param idx The index of the constraint (among all constraints linked to
   *            the variable) responsible for the update
   */

  public boolean updateSup(int x, int idx) throws ContradictionException {
          if (logger.isLoggable(Level.FINEST))
            logger.finest("SUP(" + this.toString() + "): " + this.getSup() + " -> " + x);
          return domain.updateSup(x, idx);
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
   */

  public boolean removeVal(int x, int idx) throws ContradictionException {
        return domain.removeVal(x, idx);
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
   */

  public boolean removeInterval(int a, int b, int idx) throws ContradictionException {
      return domain.removeInterval(a, b, idx);
  }

  /**
   * Internal var: instantiation of the variable caused by its i-th constraint
   * Returns a boolean indicating whether the call indeed added new information.
   *
   * @param x   the new upper bound
   * @param idx the index of the constraint (among all constraints linked to the
   *            variable) responsible for the update
   */

  public boolean instantiate(int x, int idx) throws ContradictionException {
      return domain.instantiate(x, idx);
  }


  public void fail() throws ContradictionException {
        problem.getPropagationEngine().raiseContradiction(this);
  }


  /**
   * Gets the minimal value of the variable.
   */

  public int getInf() {
    return domain.getInf();
  }


  /**
   * Gets the maximal value of the variable.
   */

  public int getSup() {
    return domain.getSup();
  }


  /**
   * Gets the value of the variable if instantiated.
   */

  public int getVal() {
    return value.get();
  }

  /**
   * @deprecated replaced by getVal
   */
  public int getValue() {
    return value.get();
  }

  /**
   * pretty printing
   *
   * @return a String representation of the variable
   */
  public String toString() {
    return (super.toString() + ":" + value.toString());
  }

  /**
   * pretty printing
   *
   * @return a String representation of the variable
   */
  public String pretty() {
    return (this.toString() + "[" + this.domain.getSize() + "]" + this.domain.pretty());
  }
}
