package choco.cp.solver.variables.integer;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomain;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * A variable implementation dedicated to boolean domains
 **/
public final class BooleanVarImpl extends IntDomainVarImpl {

    /**
     * A reference to the domain
     */
    protected BooleanDomain booldomain;

    public BooleanVarImpl(Solver solver, String name) {
        super(solver,name);
        this.booldomain = new BooleanDomain(this);
        this.event = new BoolVarEvent(this);
    }

    // ============================================
    // Methods of the interface
    // ============================================

    /**
     * Checks if the variable is instantiated to a specific value.
     */

    public final boolean isInstantiatedTo(final int x) {
      if (booldomain.isInstantiated()) {
        return x == booldomain.getValueIfInst();
      }
      return false;
    }


    /**
     * Checks if the variables is instantiated to any value.
     */

    public final boolean isInstantiated() {
      return booldomain.isInstantiated();
    }


    /**
     * Checks if a value is still in the domain.
     */
    public final boolean canBeInstantiatedTo(int x) {
        if (booldomain.isInstantiated()) {
            return x == booldomain.getValueIfInst();
        }
        return x >= 0 && x <= 1;
    }


    /**
     * Checks if a value is still in the domain assuming that x
     * is 0 or 1
     */
    public final boolean fastCanBeInstantiatedTo(int x) {
        if (booldomain.isInstantiated()) {
            return x == booldomain.getValueIfInst();
        }
        return true;
    }

    public boolean hasEnumeratedDomain() {
      return true;
    }

    public boolean hasBooleanDomain() {
      return true;
    }

    /**
     * Gets the domain size.
     */
    public final int getDomainSize() {
      return booldomain.getSize();
    }

    /**
     * Checks if it can be equals to another variable.
     */
    public final boolean canBeEqualTo(IntDomainVar x) {
      if (booldomain.isInstantiated()) {
          return x.canBeInstantiatedTo(booldomain.getValueIfInst());
      } else {
          return x.fastCanBeInstantiatedTo(0) || x.fastCanBeInstantiatedTo(1);
      }
    }

    public final IntDomain getDomain() {
        return booldomain;    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Checks if the variables can be instantiated to at least one value
     * in the array.
     * todo: this method seems completely deprecated
     * @param sortedValList The value array.
     * @param nVals         The number of interesting value in this array.
     */
    public final boolean canBeInstantiatedIn(final int[] sortedValList, int nVals) {
        if (getInf() <= sortedValList[nVals - 1]) {
            if (getSup() >= sortedValList[0]) {
                if (booldomain.isInstantiated()) {
                    int val = booldomain.getValueIfInst();
                    for (int i = 0; i < nVals; i++) {
                        if (sortedValList[i] == val)
                            return true;
                    }
                } else {
                    for (int i = 0; i < nVals; i++) {
                        if (sortedValList[i] == 0 || sortedValList[i] == 1)
                            return true;
                    }
                }
                return false;
            }
        }
        return false;
    }


    /**
     * Returns a randomly choosed value in the domain.
     * <p/>
     * Not implemented yet.
     */

    public int getRandomDomainValue() {
    	return booldomain.getRandomValue();
    }


    /**
     * Gets the next value in the domain.
     */

    public final int getNextDomainValue(int currentv) {
        return booldomain.getNextValue(currentv);
    }


    /**
     * Gets the previous value in the domain.
     */

    public final int getPrevDomainValue(int currentv) {
    	return booldomain.getPrevValue(currentv);
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
    	//logOnInf(x);
    	if(x > 0){
            return booldomain.instantiate(x, idx);
        }
        return false;
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
    	//logOnSup(x); 
    	if(x < 1){
            return booldomain.instantiate(x, idx);
        }
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
     */

    public boolean removeVal(int x, int idx) throws ContradictionException {
    	//logOnRem(x); 
    	if (x == 0)
            return booldomain.instantiate(1,idx);
        else if (x == 1)
            return booldomain.instantiate(0,idx);
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
     */

    public boolean removeInterval(int a, int b, int idx) throws ContradictionException {
    	//logOnRemInt(a, b);
    	return booldomain.removeInterval(a, b, idx);
    }

    /**
     * Internal var: instantiation of the variable caused by its i-th constraint
     * Returns a boolean indicating whether the call indeed added new information.
     *
     * @param x   the new upper bound
     * @param idx the index of the constraint (among all constraints linked to the
     *            variable) responsible for the update
     */

    public final boolean instantiate(int x, int idx) throws ContradictionException {
    	//logOnInst(x);
    	return booldomain.instantiate(x, idx);
    }


    /**
     * Gets the minimal value of the variable.
     */

    public final int getInf() {
        if (booldomain.isInstantiated())
            return booldomain.getValueIfInst();
        else return 0;
    }


    /**
     * Gets the maximal value of the variable.
     */

    public final int getSup() {
        if (booldomain.isInstantiated())
            return booldomain.getValueIfInst();
        else return 1;
    }


    /**
     * Gets the value of the variable if instantiated.
     */

    public final int getVal() {
      return booldomain.getValueIfInst();
    }

    /**
     * @deprecated replaced by getVal
     */
    public final int getValue() {
      return booldomain.getValueIfInst();
    }

    /**
     * pretty printing
     *
     * @return a String representation of the variable
     */
    public String toString() {
      return (name + ":" + (isInstantiated() ? getVal(): "?"));
    }

    public String pretty() {
        return this.toString() + "[" + this.booldomain.getSize() + "]" + this.booldomain.pretty();
    }
}
