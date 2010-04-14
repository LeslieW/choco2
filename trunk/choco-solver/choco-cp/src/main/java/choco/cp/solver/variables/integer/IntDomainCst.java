/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |   (..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.variables.integer;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomain;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 31 mars 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public final class IntDomainCst extends IntDomainVarImpl {

    private final int value;
    private final boolean isBoolean;
    private final IntDomain domain;
    /**
     * Initializes a new variable.
     *
     * @param solver      The model this variable belongs to
     * @param name        The name of the variable
     * @param constraints constraints stored specific structure
     */
    public IntDomainCst(final Solver solver, final String name, final int theValue) {
        super(solver, name);
        value = theValue;
        isBoolean = (value == 0 || value == 1);
        domain = new OneValueIntDomain(theValue, solver.getPropagationEngine());    
    }

    /**
     * pretty printing of the object. This String is not constant and may depend on the context.
     *
     * @return a readable string representation of the object
     */
    @Override
    public String pretty() {
        return name;
    }

    /**
     * <b>Public user API:</b>
     * <i>Propagation events</i> removing a value from the domain of a variable.
     *
     * @param x the removed value
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */
    @Override
    public void remVal(final int x) throws ContradictionException {
        if(x == value){
            propagationEngine.raiseContradiction(this);
        }
    }

    /**
     * <b>Public user API:</b>
     * <i>Propagation events</i> updating the lower bound of a variable
     * (ie: removing all value strictly below the new lower bound from the domain).
     *
     * @param x the new lower bound
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */
    @Override
    public void setInf(final int x) throws ContradictionException {
        if(x > value){
            propagationEngine.raiseContradiction(this);
        }
    }

    /**
     * @param x the new inf value
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     * @deprecated replaced by setInf
     */
    @Override
    public void setMin(final int x) throws ContradictionException {
        if(x > value){
            propagationEngine.raiseContradiction(this);
        }
    }

    /**
     * <b>Public user API:</b>
     * <i>Propagation events</i> updating the upper bound of a variable
     * (ie: removing all value strictly above the new upper bound from the domain).
     *
     * @param x the new upper bound
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */
    @Override
    public void setSup(final int x) throws ContradictionException {
        if(x < value){
            propagationEngine.raiseContradiction(this);
        }
    }

    /**
     * @param x the new max value
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     * @deprecated replaced by setMax
     */
    @Override
    public void setMax(final int x) throws ContradictionException {
        if(x < value){
            propagationEngine.raiseContradiction(this);
        }
    }

    /**
     * <b>Public user API:</b>
     * <i>Propagation events</i> wiping out the domain of the variable (removing all values)
     * and throwing a contradiction
     *
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */
    @Override
    public void wipeOut() throws ContradictionException {
        propagationEngine.raiseContradiction(this);
    }

    /**
     * <b>Public user API:</b>
     * <i>Domains :</i> whether an enumeration of values (in addition to the enclosing interval) is stored
     *
     * @return wether an enumeration of values is stored
     */
    @Override
    public boolean hasEnumeratedDomain() {
        return true;
    }

    /**
     * <b>Public user API:</b>
     * <i>Domains :</i> whether the domain is a 0/1 domain
     *
     * @return wether the domain is a 0/1 domain
     */
    @Override
    public boolean hasBooleanDomain() {
        return isBoolean;
    }

    /**
     * <b>Public user API:</b>
     * <i>Domains :</i> returns the object responsible for storing the enumeration of values in the domain
     *
     * @return the objects responsible for storing the enumeration of values in the domain
     */
    @Override
    public IntDomain getDomain() {
        return domain;
    }

    /**
     * <b>Public user API:</b>
     * <i>Domains :</i> testing whether a value is in the domain.
     *
     * @param x the tested value
     * @return wether a value is in the domain
     */
    @Override
    public boolean canBeInstantiatedTo(final int x) {
        return x== value;
    }

    /**
     * Checks if a value is still in the domain assuming the value is
     * in the initial bound of the domain
     */
    @Override
    public boolean fastCanBeInstantiatedTo(final int x) {
        return x == value;
    }

    /**
     * <b>Public user API:</b>
     * <i>Domains :</i> testing whether two variables have intersecting domains.
     *
     * @param x the other variable
     * @return wether two variables have intersecting domains
     */
    @Override
    public boolean canBeEqualTo(final IntDomainVar x) {
        if(IntDomainCst.class.isInstance(x)){
            final IntDomainCst xc = (IntDomainCst)x;
            return xc.value == value;
        }
        return false;
    }

    /**
     * <b>Public user API:</b>
     * <i>Domains :</i> testing whether one value among a list is present in
     * the domain.
     *
     * @param sortedValList the list of values. Must be sorted in increasing order.
     * @param nVals         the size of the list of values
     * @return wether one value among a list is present in the domain
     */
    @Override
    public boolean canBeInstantiatedIn(final int[] sortedValList, final int nVals) {
        return nVals == 1 && sortedValList[0] == value;
    }

    /**
     * <b>Public user API:</b>
     * <i>Domains :</i> retrieves a value drawn at random (uniform distribution)
     * from the domain.
     *
     * @return a value drawn at random from the domain
     */
    @Override
    public int getRandomDomainValue() {
        return value;
    }

    /**
     * <b>Public user API:</b>
     * <i>Domains :</i> retrieves the value immediately (but strictly) after
     * <i>i</i> in the domain
     *
     * @param i the pivot value. May or may not be in the domain
     * @return the value immediatly after the domain
     */
    @Override
    public int getNextDomainValue(final int i) {
        return domain.getNextValue(i);
    }

    /**
     * <b>Public user API:</b>
     * <i>Domains :</i> retrieves the value immediately (but strictly) before
     * <i>i</i> in the domain.
     *
     * @param i the pivot value. May or may not be in the domain
     * @return the value immediatly before the domain
     */
    @Override
    public int getPrevDomainValue(final int i) {
        return domain.getPrevValue(i);
    }

    /**
     * <b>Public user API:</b>
     * <i>Domains :</i> retrieves the number of values in the domain.
     *
     * @return the number of values in the domain
     */
    @Override
    public int getDomainSize() {
        return 1;
    }

    /**
     * Returns the lower bound of the variable domain (e.g. the smallest value that the variable can be assigned).
     *
     * @return the domain lower bound
     */
    @Override
    public int getInf() {
        return value;
    }

    /**
     * Returns the upper bound of the variable domain (e.g. the greatest value that the variable can be assigned).
     *
     * @return the domain upper bound
     */
    @Override
    public int getSup() {
        return value;
    }

    /**
     * @return the value of the variable if known
     * @deprecated replaced by getVal
     */
    @Override
    public int getValue() {
        return value;
    }

    /**
     * <i>Propagation events</i> updating the lower bound of a variable
     * (ie: removing all value strictly below the new lower bound from the domain).
     *
     * @param x          a lower bound of the domain (the new one, if better than the one currently stored)
     * @param cause      constraint that modified the {@code x}
     * @param forceAwake
     * @return a boolean indicating whether this method call added new information or not
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */
    @Override
    public boolean updateInf(final int x, final SConstraint cause, final boolean forceAwake) throws ContradictionException {
        if(x > value){
            propagationEngine.raiseContradiction(this);
        }
        return false;
    }

    @Override
    public boolean updateInf(final int x, final int idx) throws ContradictionException {
        if(x > value){
            propagationEngine.raiseContradiction(this);
        }
        return false;
    }

    /**
     * <i>Propagation events</i> updating the upper bound of a variable
     * (ie: removing all value strictly above the new upper bound from the domain).
     *
     * @param x          an upper bound of the domain (the new one, if better than the one currently stored)
     * @param cause
     * @param forceAwake
     * @return a boolean indicating whether this method call added new information or not
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */
    @Override
    public boolean updateSup(final int x, final SConstraint cause, final boolean forceAwake) throws ContradictionException {
        if(x < value){
            propagationEngine.raiseContradiction(this);
        }
        return false;
    }

    @Override
    public boolean updateSup(final int x, final int idx) throws ContradictionException {
        if(x < value){
            propagationEngine.raiseContradiction(this);
        }
        return false;
    }

    /**
     * <i>Propagation events</i> updating the domain of a variable (by removing a value)
     *
     * @param x          the value that is not in the domain
     * @param cause
     * @param forceAwake
     * @return a boolean indicating whether this method call added new information or not
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */
    @Override
    public boolean removeVal(final int x, final SConstraint cause, final boolean forceAwake) throws ContradictionException {
        if(x == value){
            propagationEngine.raiseContradiction(this);
        }
        return false;
    }

    @Override
    public boolean removeVal(final int x, final int idx) throws ContradictionException {
        if(x == value){
            propagationEngine.raiseContradiction(this);
        }
        return false;
    }

    /**
     * <i>Propagation events</i> updating the domain of a variable
     * (by removing an interval, ie, a sequence of consecutive values)
     *
     * @param a          the lower bound of the forbidden interval
     * @param b          the upper bound of the forbidden interval
     * @param cause
     * @param forceAwake
     * @return a boolean indicating whether this method call added new information or not
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */
    @Override
    public boolean removeInterval(final int a, final int b, final SConstraint cause, final boolean forceAwake) throws ContradictionException {
        if(a <= value && value <= b){
            propagationEngine.raiseContradiction(this);
        }
        return false;
    }

    @Override
    public boolean removeInterval(final int a, final int b, final int idx) throws ContradictionException {
        if(a <= value && value <= b){
            propagationEngine.raiseContradiction(this);
        }
        return false;
    }

    /**
     * <i>Propagation events</i> instantiating a variable
     * (ie: removing all other values from the domain)
     *
     * @param x          the value of the variable
     * @param cause
     * @param forceAwake
     * @return a boolean indicating whether this method call added new information or not
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */
    @Override
    public boolean instantiate(final int x, final SConstraint cause, final boolean forceAwake) throws ContradictionException {
        if(x != value){
            propagationEngine.raiseContradiction(this);
        }
        return false;
    }

    @Override
    public boolean instantiate(final int x, final int idx) throws ContradictionException {
        if(x != value){
            propagationEngine.raiseContradiction(this);
        }
        return false;
    }

    /**
     * <b>Public user API:</b>
     * <i>Propagation events</i> assigning a value to a variable
     * (ie: removing all other values from its domain).
     *
     * @param x the value that is assigned to the variable
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */
    @Override
    public void setVal(final int x) throws ContradictionException {
        if(x != value){
            propagationEngine.raiseContradiction(this);
        }
    }

    /**
     * Returns the value of the variable if instantiated.
     *
     * @return the value of the variable
     */
    @Override
    public int getVal() {
        return value;
    }

    /**
     * <b>Public user API:</b>
     * <i>Domains :</i> testing whether the value of an instantiated variable
     * is equal to a specific value.
     *
     * @param x the tested value
     * @return wether the value of an instantiated variables is equal to a x.
     */
    @Override
    public boolean isInstantiatedTo(final int x) {
        return x == value;
    }

    /**
     * <b>Public user API:</b>
     * <i>Domains :</i> testing whether a variable is instantiated or not.
     *
     * @return a boolean giving if a variable is instanciated or not
     */
    @Override
    public boolean isInstantiated() {
        return true;
    }
}
