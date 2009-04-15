/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
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
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2008      *
 **************************************************/
package choco.cp.solver.variables.integer;

import choco.kernel.common.util.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.trailing.StoredIndexedBipartiteSet;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.SolverException;

import java.util.Random;
import java.util.logging.Level;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 18 déc. 2008
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class BooleanDomain extends AbstractIntDomain {

    /**
     * A random generator for random value from the domain
     */

    protected static Random random = new Random(System.currentTimeMillis());

    /**
     * The offset, that is the minimal value of the domain (stored at index 0).
     * Thus the entry at index i corresponds to x=i+offset).
     */

    protected final int offset;


    /**
     * indicate the value of the domain : false = 0, true = 1
     */
    protected int value;

    /**
     * A bi partite set indicating for each value whether it is present or not.
     * If the set contains the domain, the variable is not instanciated.
     */

    protected StoredIndexedBipartiteSet notInstanciated;


    /**
     * Constructs a new domain for the specified variable and bounds.
     *
     * @param v The involved variable.
     */

    public BooleanDomain(IntDomainVarImpl v) {
        variable = v;
        solver = v.getSolver();
        IEnvironment env = solver.getEnvironment();
        notInstanciated = (StoredIndexedBipartiteSet)env.getSharedBipartiteSetForBooleanVars();
        this.offset = env.getNextOffset();
        value = 0;
    }


    /**
     * This method is not relevant if the variable is not instantiated.
     * For performance issue, this test is not
     *
     * @return the value IF the variable is instantiated
     */
    public int getValueIfInst() {
        return value;
    }


    /**
     * @return true if the boolean is instantiated
     */
    public boolean isInstantiated() {
        return !notInstanciated.contain(offset);
    }

    /**
     * Returns the minimal present value.
     */
    public int getInf() {
        if (!notInstanciated.contain(offset)) {
            return getValueIfInst();
        }
        return 0;
    }


    /**
     * Returns the maximal present value.
     */
    public int getSup() {
        if (!notInstanciated.contain(offset)) {
            return getValueIfInst();
        }
        return 1;
    }


    /**
     * Sets a new minimal value.
     *
     * @param x New bound value.
     */

    public int updateInf(int x) {
        throw new SolverException("Unexpected call of updateInf");
    }

    /**
     * Sets a new maximal value.
     *
     * @param x New bound value.
     */

    public int updateSup(int x) {
        throw new SolverException("Unexpected call of updateSup");
    }

    /**
     * Checks if the value is present.
     *
     * @param x The value to check.
     */

    public final boolean contains(final int x) {
        if(!notInstanciated.contain(offset)){
            return getValueIfInst()==x;
        }
        return x==0||x==1;
    }

    /**
     * Removes a value.
     */
    public boolean remove(int x) {
        throw new SolverException("Unexpected call of remove");
    }

    /**
     * Removes all the value but the specified one.
     */

    public void restrict(int x) {
        notInstanciated.remove(offset);
        value = x;
    }

    /**
     * Returns the current size of the domain.
     */

    public int getSize() {
        return (notInstanciated.contain(offset)?2:1);
    }

    public DisposableIntIterator getIterator() {
        if(getSize() == 1) return DisposableIntIterator.getOneValueIterator(getInf());
        BitSetIntDomainIterator iter = (BitSetIntDomainIterator) lastIterator;
        if (iter != null && iter.reusable) {
            iter.init();
            return iter;
        }
        lastIterator = new BitSetIntDomainIterator();
        return lastIterator;
    }

    protected class BitSetIntDomainIterator extends DisposableIntIterator {
        protected int nextValue;

        private BitSetIntDomainIterator() { //AbstractIntDomain dom) {
            init();
        }

        @Override
        public void init() {
            super.init();
            nextValue = getInf();
        }

        public boolean hasNext() {
            return nextValue <= 1;
        }

        public int next() {
            int v = nextValue;
            if (v == 0 && notInstanciated.contain(offset))
                nextValue = 1;
            else nextValue = 2;
            return v;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Returns the value following <code>x</code>
     */

    public final int getNextValue(final int x) {
        if (!notInstanciated.contain(offset)) {
            int val = getValueIfInst();
            return (val > x) ? val : Integer.MAX_VALUE;
        } else {
            if (x < 0) return 0;
            if (x == 0) return 1;
            return Integer.MAX_VALUE;            
        }
    }


    /**
     * Returns the value preceding <code>x</code>
     */

    public int getPrevValue(int x) {
        if(x > getSup())return getSup();
        if(x > getInf())return getInf();
        return Integer.MIN_VALUE;
    }


    /**
     * Checks if the value has a following value.
     */

    public boolean hasNextValue(int x) {
        return (x < getSup());
    }


    /**
     * Checks if the value has a preceding value.
     */

    public boolean hasPrevValue(int x) {
        return (x > getInf());
    }


    /**
     * Returns a value randomly choosed in the domain.
     */

    public int getRandomValue() {
        if (!notInstanciated.contain(offset)) {
            return getValueIfInst();
        } else {
            return random.nextInt(2);
        }
    }
 
    public boolean isEnumerated() {
        return true;
    }

    public boolean isBoolean() {
        return true;
    }

    protected DisposableIntIterator _cachedDeltaIntDomainIterator = null;

    public DisposableIntIterator getDeltaIterator() {
        DeltaBoolDomainIterator iter = (DeltaBoolDomainIterator) _cachedDeltaIntDomainIterator;
        if (iter != null && iter.disposed) {
            iter.init();
            return iter;
        }
        _cachedDeltaIntDomainIterator = new DeltaBoolDomainIterator(this);
        return _cachedDeltaIntDomainIterator;
    }

    protected static class DeltaBoolDomainIterator extends DisposableIntIterator {
        protected BooleanDomain domain;
        protected int val = -1;
        protected boolean disposed = true;

        private DeltaBoolDomainIterator(BooleanDomain dom) {
            domain = dom;
            init();
        }

        public void init() {
            val = domain.getValueIfInst();
            if (val == 1) val = 0;
            else if (val == 0) val = 1;
            disposed = false;
        }

        public void dispose() {
            disposed = true;
        }

        public boolean hasNext() {
            return val < 2;
        }

        public int next() {
            int temp = val;
            val = 2;
//            if (isInstantiated())
//                val = 2;
//            else val++;
            return temp;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public String toString() {
        return "[" + getInf() + "," + getSup() + "]";
    }

    public String pretty() {
        return toString();
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
            solver.getPropagationEngine().postInstInt(variable, idx);

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
            solver.getPropagationEngine().postInstInt(variable, idx);
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
            solver.getPropagationEngine().postInstInt(variable, idx);
            return true;
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
            solver.getPropagationEngine().postInstInt(variable, idx);
            return true;
        } else
            return false;
    }

    public void failOnIndex(int idx) throws ContradictionException {
        if (idx == -1)
            this.getSolver().getPropagationEngine().raiseContradiction(this.variable, ContradictionException.VARIABLE);
        else
            this.getSolver().getPropagationEngine().raiseContradiction(variable.getConstraintVector().get(idx), ContradictionException.CONSTRAINT);
    }
    

    /**
     * Instantiating a variable to an search value. Returns true if this was
     * a real modification or not
     *
     * @param x the new instantiate value
     * @return wether it is a real modification or not
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */
    @Override
       protected boolean _instantiate(int x, int idx) throws ContradictionException {
        if (!notInstanciated.contain(offset)) {
            if (value != x) {
                failOnIndex(idx);
            }
            return false;
        } else {
            if (x == 0 || x == 1) {
                restrict(x);
                variable.updateNbVarInstanciated();
                return true;
            } else {
                failOnIndex(idx);
                return false;
            }
        }
       }


    /**
     * Improving the lower bound.
     *
     * @param x the new lower bound
     * @return a boolean indicating wether the update has been done
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */
    @Override
    protected boolean _updateInf(int x, int idx) throws ContradictionException {
        if (isInstantiated()) {
           if (getValueIfInst() < x) {
            failOnIndex(idx);
           }
           return false;
        } else {
           if (x > 1) {
            failOnIndex(idx);
           } else if (x == 1) {
               restrict(1);
               variable.updateNbVarInstanciated();
               //variable.value.set(1);
               return true;
           }
        }
        return false;
    }


    /**
      * Improving the upper bound.
      *
      * @param x the new upper bound
      * @return wether the update has been done
      * @throws choco.kernel.solver.ContradictionException
      *          contradiction exception
      */
     @Override
     protected boolean _updateSup(int x, int idx) throws ContradictionException {
         if (isInstantiated()) {
            if (getValueIfInst() > x) {
             failOnIndex(idx);
            }
            return false;
         } else {
            if (x < 0) {
             failOnIndex(idx);
            } else if (x == 0) {
                restrict(0);
                variable.updateNbVarInstanciated();
                //variable.value.set(0);
                return true;
            }
         }
         return false;
     }


    /**
      * Removing a value from the domain of a variable. Returns true if this
      * was a real modification on the domain.
      *
      * @param x the value to remove
      * @return wether the removal has been done
      * @throws choco.kernel.solver.ContradictionException
      *          contradiction excpetion
      */
     @Override
     protected boolean _removeVal(int x, int idx) throws ContradictionException {
        if (isInstantiated()) {
            if (getValueIfInst() == x) {
             failOnIndex(idx);
            }
            return false;
         } else {
            if (x == 0) {
                restrict(1);
                variable.updateNbVarInstanciated();
                //variable.value.set(1);
                return true;
            } else if (x == 1) {
                restrict(0);
                variable.updateNbVarInstanciated();
                //variable.value.set(0);
                return true;
            }
         }
         return false;
     }

     public StoredIndexedBipartiteSet getStoredList() {
         return notInstanciated;
     }

     public int getOffset() {
         return offset;
     }
}
