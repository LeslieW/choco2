// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.var;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.AbstractEntity;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.prop.VarEvent;
import i_want_to_use_this_old_version_of_choco.util.DisposableIntIterator;

import java.util.logging.Level;
import java.util.logging.Logger;
/** History:
 * 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 * */
public abstract class AbstractIntDomain extends AbstractEntity implements IntDomain {
    /**
     * Reference to an object for logging trace statements related to domains of search variables (using the java.util.logging package)
     */
    protected static Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop");

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

    protected IntDomainIterator lastIterator;

    /**
     * Returns an getIterator.
     */

    public DisposableIntIterator getIterator() {
        if (lastIterator != null && lastIterator.reusable) {
            lastIterator.reusable = false;
            lastIterator.currentValue = Integer.MIN_VALUE;
            return lastIterator;
        }
        lastIterator = new IntDomainIterator(this);
        return lastIterator;
    }

    protected class IntDomainIterator implements DisposableIntIterator {
        protected AbstractIntDomain domain;
        protected int currentValue = Integer.MIN_VALUE;
        boolean reusable;

        private IntDomainIterator(AbstractIntDomain dom) {
            reusable = false;
            domain = dom;
            currentValue = Integer.MIN_VALUE; // dom.getInf();
        }

        public boolean hasNext() {
            return (Integer.MIN_VALUE == currentValue) ? true : (currentValue < domain.getSup());
        }

        public int next() {
            currentValue = (Integer.MIN_VALUE == currentValue) ? domain.getInf() : domain.getNextValue(currentValue);
            return currentValue;
        }

        public void remove() {
            if (currentValue == Integer.MIN_VALUE) {
                throw new IllegalStateException();
            } else {
                throw new UnsupportedOperationException();
            }
        }

        public void dispose() {
            reusable = true;
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
            if (getSup() == x) cause = idx;
            if (getInf() == getSup())
                instantiate(getSup(), cause);
            else
                problem.getPropagationEngine().postUpdateSup(variable, cause);
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
        if (_updateInf(x,idx)) {
            int cause = VarEvent.NOCAUSE;
            if (getInf() == x) cause = idx;
            if (getSup() == getInf())
                instantiate(getInf(), cause);
            else
                problem.getPropagationEngine().postUpdateInf(variable, cause);
            // TODO      problem.getChocEngine().postUpdateInf(variable, cause, oldinf);
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
            if (getInf() == getSup())
                problem.getPropagationEngine().postInstInt(variable, VarEvent.NOCAUSE);
            else if (x < getInf())
                problem.getPropagationEngine().postUpdateInf(variable, VarEvent.NOCAUSE);
            else if (x > getSup())
                problem.getPropagationEngine().postUpdateSup(variable, VarEvent.NOCAUSE);
            else
                problem.getPropagationEngine().postRemoveVal(variable, x, idx);
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
        if (_instantiate(x,idx)) {
            if (logger.isLoggable(Level.FINEST))
                logger.finest("INST(" + this.toString() + "): " + x);
            problem.getPropagationEngine().postInstInt(variable, idx);
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
     * @param x the new instantiate value
     * @return wether it is a real modification or not
     * @throws ContradictionException contradiction exception
     */

    protected boolean _instantiate(int x, int idx) throws ContradictionException {
        if (variable.isInstantiated()) {
            if (variable.getVal() != x) {
	            if (idx == -1)
		            throw new ContradictionException(this.variable);
		        else throw new ContradictionException(this.variable, (AbstractConstraint) variable.getConstraintVector().get(idx));                      	            
            } else return false;
        } else {
            if (x < getInf() || x > getSup() || !contains(x)) { // GRT : we need to check bounds
                // since contains suppose trivial bounds
                // are containing tested value !!
	            if (idx == -1)
		            throw new ContradictionException(this.variable);
		        else throw new ContradictionException(this.variable, (AbstractConstraint) variable.getConstraintVector().get(idx));      
            } else {
                restrict(x);
            }
            variable.value.set(x);
            //FR_1873619 CPRU: DomOverDeg+DomOverWDeg
            variable.updateNbVarInstanciated();
            return true;
        }
    }


    /**
     * Improving the lower bound.
     * @param x the new lower bound
     * @return a boolean indicating wether the update has been done
     * @throws ContradictionException contradiction exception
     */

    // note: one could have thrown an OutOfDomainException in case (x > IStateInt.MAXINT)
    protected boolean _updateInf(int x, int idx) throws ContradictionException {
        if (x > getInf()) {
            if (x > getSup()) {
                if (idx == -1)
	                throw new ContradictionException(this.variable);
	            else throw new ContradictionException(this.variable, (AbstractConstraint) variable.getConstraintVector().get(idx)); 
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
     * @param x the new upper bound
     * @return wether the update has been done
     * @throws ContradictionException contradiction exception
     */
    protected boolean _updateSup(int x, int idx) throws ContradictionException {
        if (x < getSup()) {
            if (x < getInf()) {
	            if (idx == -1)
		            throw new ContradictionException(this.variable);
		        else throw new ContradictionException(this.variable, (AbstractConstraint) variable.getConstraintVector().get(idx));
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
     * @param x the value to remove
     * @return wether the removal has been done
     * @throws ContradictionException contradiction excpetion
     */
    protected boolean _removeVal(int x, int idx) throws ContradictionException {
        int infv = getInf(), supv = getSup();
        if (infv <= x && x <= supv) {
            if (x == infv) {
                _updateInf(x + 1, idx);
                if (getInf() == supv) _instantiate(supv, idx);
                return true;
            } else if (x == supv) {
                _updateSup(x - 1, idx);
                if (getSup() == infv) _instantiate(infv, idx);
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
     *
     * @return a boolean
     */
    public boolean getReleasedDeltaDomain() {
        return true;
    }
}
