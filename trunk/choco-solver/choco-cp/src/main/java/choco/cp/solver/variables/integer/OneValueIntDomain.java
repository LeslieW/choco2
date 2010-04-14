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

import choco.kernel.solver.propagation.PropagationEngine;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 31 mars 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public final class OneValueIntDomain extends AbstractIntDomain{

    private final int value;

    private final boolean isBoolean;
    
    protected OneValueIntDomain(final int theValue, final PropagationEngine propagationEngine) {
        super(propagationEngine);
        value  = theValue;
        isBoolean = (value == 0 || value == 1);
    }


    /**
     * Access the minimal value stored in the domain.
     */
    @Override
    public int getInf() {
        return value;
    }

    /**
     * Access the maximal value stored in the domain/
     */
    @Override
    public int getSup() {
        return value;
    }

    /**
     * Augment the minimal value stored in the domain.
     * returns the new lower bound (<i>x</i> or more, in case <i>x</i> was
     * not in the domain)
     */
    @Override
    public int updateInf(final int x) {
        throw new UnsupportedOperationException();
    }

    /**
     * Diminish the maximal value stored in the domain.
     * returns the new upper bound (<i>x</i> or more, in case <i>x</i> was
     * not in the domain).
     */
    @Override
    public int updateSup(final int x) {
        throw new UnsupportedOperationException();
    }

    /**
     * Testing whether an search value is contained within the domain.
     */
    @Override
    public boolean contains(final int x) {
        return x == value;
    }

    /**
     * Removing a single value from the domain.
     */
    @Override
    public boolean remove(final int x) {
        throw new UnsupportedOperationException();
    }

    /**
     * Restricting the domain to a singleton
     */
    @Override
    public void restrict(final int x) {
        throw new UnsupportedOperationException();
    }

    /**
     * Access the total number of values stored in the domain.
     */
    @Override
    public int getSize() {
        return 1;
    }

    /**
     * Accessing the smallest value stored in the domain and strictly greater
     * than <i>x</i>.
     * Does not require <i>x</i> to be in the domain.
     */
    @Override
    public int getNextValue(final int x) {
        if(x < value){
            return value;
        }else{
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Accessing the largest value stored in the domain and strictly smaller
     * than <i>x</i>.
     * Does not require <i>x</i> to be in the domain.
     */
    @Override
    public int getPrevValue(final int x) {
        if(x > value){
            return value;
        }else{
            return Integer.MIN_VALUE;
        }
    }

    /**
     * Testing whether there are values in the domain that are strictly greater
     * than <i>x</i>.
     * Does not require <i>x</i> to be in the domain.
     */
    @Override
    public boolean hasNextValue(final int x) {
        return x < value;
    }

    /**
     * Testing whether there are values in the domain that are strictly smaller
     * than <i>x</i>.
     * Does not require <i>x</i> to be in the domain.
     */
    @Override
    public boolean hasPrevValue(final int x) {
        return x > value;
    }

    /**
     * Draws a value at random from the domain.
     */
    @Override
    public int getRandomValue() {
        return value;
    }

    @Override
    public boolean isEnumerated() {
        return true;
    }

    /**
     * Is it a 0/1 domain ?
     */
    @Override
    public boolean isBoolean() {
        return isBoolean;
    }

    /**
     * pretty printing of the object. This String is not constant and may depend on the context.
     *
     * @return a readable string representation of the object
     */
    @Override
    public String pretty() {
        return String.valueOf('['+value+']');
    }
}
