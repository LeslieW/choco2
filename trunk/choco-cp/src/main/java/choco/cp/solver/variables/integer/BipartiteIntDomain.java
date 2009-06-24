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

import choco.Choco;
import choco.kernel.common.util.DisposableIntIterator;
import choco.kernel.common.util.IntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;

/**
 * @author Guillaume Rochart
 *         This class is an integer domain implementation based on a bipartite set : values in the domain, and values outside.
 *         For instance :
 *         <p/>
 *         World 1 :      1234
 *         World 2 :      124 3
 *         World 3 :      14 23
 *         World 2 :      142 3
 *         World 3 :      42 13
 *         World 2 :      421 3
 *         World 1 :      4213
 *         <p/>
 *         On this implementation, we only need an integer list and a stored integer to know how many values are in the domain,
 *         and a list pointing all value indices in order to be able to remove values easily (without iterating the domain).
 */
public class BipartiteIntDomain extends AbstractIntDomain {

    /**
     * A random generator for random value from the domain
     */

    protected final static Random random = new Random();


    /**
     * The values (not ordered) contained in the domain.
     */
    protected int[] values;

    /**
     * The number of values currently in the domain.
     */
    protected IStateInt valuesInDomainNumber;

    /**
     * lower bound of the domain.
     */
    protected IStateInt inf;

    /**
     * lower bound of the domain.
     */
    protected IStateInt sup;

    /**
     * The indices for each values (+ the minimalValue).
     */
    protected int[] indices;

    /**
     * The minimum from all values.
     */
    protected int offset;

    /**
     * Constructs a new domain for the specified variable and bounds.
     *
     * @param v   The involved variable.
     * @param sortedValues arry of sorted values.
     */
    public BipartiteIntDomain(IntDomainVarImpl v, int[] sortedValues) {
        if (Choco.DEBUG) {
            if (v == null) {
                LOGGER.severe("Cannot create a domain without a not null variable !");
                System.exit(-1);
            }
            if (sortedValues == null) {
                LOGGER.log(Level.SEVERE, "Variable {0} cannot have an empty domain at the beginning !", v);
                System.exit(-1);
            }
        }
        init(v, sortedValues);
    }

    /**
     * Constructs a new domain for the specified variable and bounds.
     *
     * @param v   The involved variable.
     * @param low Minimal value.
     * @param up  Maximal value.
     */
    public BipartiteIntDomain(IntDomainVarImpl v, int low, int up) {
        // Pre-condition
        if (Choco.DEBUG) {
            if (v == null) {
            	 LOGGER.severe("Cannot create a domain without a not null variable !");
                System.exit(-1);
            }
            if (low > up) {
            	 LOGGER.log(Level.SEVERE, "Variable {0} cannot have an empty domain at the beginning !", v);
                System.exit(-1);
            }
        }
        int[] sortedValues = new int[up - low + 1];
        for (int i = 0; i < sortedValues.length; i++) {
            sortedValues[i] = low + i;
        }
        init(v, sortedValues);
    }

    public void init(IntDomainVarImpl v, int[] sortedValues) {
        // Constructor
        variable = v;
        solver = v.getSolver();
        IEnvironment env = solver.getEnvironment();
        int low = sortedValues[0];
        int up = sortedValues[sortedValues.length - 1];
        int size = sortedValues.length;
        // Initial lower bound is recorded as an offset 
        offset = low;
        // Initialisation of values
        values = new int[size];
        System.arraycopy(sortedValues, 0, values, 0, sortedValues.length);
        // By default all values are in...
        valuesInDomainNumber = env.makeInt(size - 1);
        // Indices are first simply offset...
        indices = new int[up - low + 1];
        Arrays.fill(indices, Integer.MAX_VALUE);
        for (int i = 0; i < values.length; i++) {
            indices[values[i] - offset] = i;
        }
        // At the beginning nothing has been propageted !
        endOfDeltaDomain = size;
        beginningOfDeltaDomain = size;
        inf = env.makeInt(low);
        sup = env.makeInt(up);
    }

    /**
     * @return the lower bound
     */
    public int getInf() {
        return inf.get();
    }

    /**
     * @return the upper bound
     */
    public int getSup() {
        return sup.get();
    }

    /**
     * This method is volontarely inefficient! It allows to be efficient for backtracking and so on.
     *
     * @return true if the value is still in the domain
     */
    public boolean contains(int x) {
        return indices[x - offset] <= valuesInDomainNumber.get();
    }

    /**
     * @return the number of values in the domain
     */
    public int getSize() {
        return valuesInDomainNumber.get() + 1;
    }

    /**
     * Be careful, there is no order in the domain values in this implementation !
     *
     * @param x the previous value in the list
     * @return the next value after x
     */
    public int getNextValue(int x) {
        if (x >= getSup()) return Integer.MAX_VALUE;
        int nextval = Integer.MAX_VALUE;//x + 1;
        for (int i = valuesInDomainNumber.get(); i >= 0; i--) {
            if (values[i] > x && values[i] < nextval)
                nextval = values[i];
        }
        return nextval;
    }

    /**
     * Be careful, there is no order in the domain values in this implementation !
     *
     * @param x a value in the list
     * @return the previous value before x
     */
    public int getPrevValue(int x) {
        if (x <= getInf()) return Integer.MAX_VALUE;
        int prevval = Integer.MIN_VALUE;
        for (int i = valuesInDomainNumber.get(); i >= 0; i--) {
            if (values[i] < x && values[i] > prevval)
                prevval = values[i];
        }
        return prevval;
    }

    //warning : this is not the usual semantic of hasNextValue...
    public boolean hasNextValue(int x) {
        return x < getSup();
    }

    //warning : this is not the usual semantic of hasPrevValue...
    public boolean hasPrevValue(int x) {
        return x > getInf();
    }

    public boolean removeInterval(int a, int b, int idx) throws ContradictionException {
        if (a <= getInf())
            return updateInf(b + 1, idx);
        else if (getSup() <= b)
            return updateSup(a - 1, idx);
        else {
            boolean anyChange = false;
            for (int i = valuesInDomainNumber.get(); i >= 0; i--) {
                int v = values[i];
                if (v >= a && v <= b)
                    anyChange |= removeVal(v, idx);
            }
            return anyChange;
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
            boolean b = remove(x);
            if (x == infv) {
                int possibleninf = x + 1;
                if (possibleninf > supv) {
                    if (idx == -1)
                        this.getSolver().getPropagationEngine().raiseContradiction(this.variable, ContradictionException.VARIABLE);
                    else
                        this.getSolver().getPropagationEngine().raiseContradiction(variable.getConstraintVector().get(idx), ContradictionException.CONSTRAINT);
                }
                int min = Integer.MAX_VALUE;
                for (int i = valuesInDomainNumber.get(); i >= 0; i--) {
                    if (values[i] < min) {
                        min = values[i];
                        if (min == possibleninf) break;
                    }
                }
                inf.set(min);
                return b;
            } else if (x == supv) {
                int possiblesup = x - 1;
                if (possiblesup < infv) {
                    if (idx == -1)
                        this.getSolver().getPropagationEngine().raiseContradiction(this.variable, ContradictionException.VARIABLE);
                    else
                        this.getSolver().getPropagationEngine().raiseContradiction(variable.getConstraintVector().get(idx), ContradictionException.CONSTRAINT);
                }
                int max = Integer.MIN_VALUE;
                for (int i = valuesInDomainNumber.get(); i >= 0; i--) {
                    if (values[i] > max) {
                        max = values[i];
                        if (max == possiblesup) break;
                    }
                }
                sup.set(max);
                return b;
            } else {
                return b;//remove(x);
            }
        } else {
            return false;
        }
    }

    public boolean remove(int x) {
        int i = x - offset;
        int mark = valuesInDomainNumber.get();
        if (indices[i] <= mark) {
            if (indices[i] < mark) {
                int tempval = values[mark];
                values[mark] = x;
                values[indices[i]] = tempval;
                indices[tempval - offset] = indices[i];
                indices[i] = mark;
            }
            valuesInDomainNumber.add(-1);
            if (endOfDeltaDomain <= mark) {
                endOfDeltaDomain = mark + 1;
            }
            return true;
        }
        return false;
    }


    public void restrict(int x) {
        int i = x - offset;
        int tempval = values[0];
        values[0] = x;
        values[indices[i]] = tempval;
        indices[tempval - offset] = indices[i];
        indices[i] = 0;
        inf.set(x);
        sup.set(x);
        if (endOfDeltaDomain <= valuesInDomainNumber.get()) {
            endOfDeltaDomain = valuesInDomainNumber.get() + 1;
        }
        valuesInDomainNumber.set(0);
    }


    public int updateInf(int x) {
        int min = Integer.MAX_VALUE;
        for (int i = valuesInDomainNumber.get(); i >= 0; i--) {
            if (values[i] < x) {
                remove(values[i]);
            } else if (values[i] < min) {
                min = values[i];
            }
        }
        inf.set(min);
        return min;
    }

    public int updateSup(int x) {
        int max = Integer.MIN_VALUE;
        for (int i = valuesInDomainNumber.get(); i >= 0; i--) {
            if (values[i] > x) {
                remove(values[i]);
            } else if (values[i] > max) {
                max = values[i];
            }
        }
        sup.set(max);
        return max;
    }


    public int getRandomValue() {
        if (getSize() == 1) return values[0];
        else return values[random.nextInt(valuesInDomainNumber.get() + 1)];
    }

    public DisposableIntIterator getIterator() {
        if(getSize() == 1) return DisposableIntIterator.getOneValueIterator(getInf());
        BipartiteIntDomainIterator iter = (BipartiteIntDomainIterator) lastIterator;
        if (iter != null && iter.reusable) {
            iter.init();
            return iter;
        }
        lastIterator = new BipartiteIntDomainIterator();
        return lastIterator;
    }

    protected class BipartiteIntDomainIterator extends DisposableIntIterator {
        protected int nextIdx;

        private BipartiteIntDomainIterator() { //AbstractIntDomain dom) {
            init();
        }

        @Override
        public void init() {
            super.init();
            nextIdx = valuesInDomainNumber.get();
        }

        public boolean hasNext() {
            return nextIdx >= 0;
        }

        public int next() {
            int v = nextIdx;
            nextIdx--;
            return values[v];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    protected DisposableIntIterator _cachedDeltaIntDomainIterator = null;

    public DisposableIntIterator getDeltaIterator() {
        DeltaBipartiteIterator iter = (DeltaBipartiteIterator) _cachedDeltaIntDomainIterator;
        if (iter != null && iter.disposed) {
            iter.init();
            return iter;
        }
        _cachedDeltaIntDomainIterator = new DeltaBipartiteIterator(this);
        return _cachedDeltaIntDomainIterator;
    }

    protected class DeltaBipartiteIterator extends DisposableIntIterator {
        protected BipartiteIntDomain domain;
        protected int currentIndex = -1;
        protected boolean disposed = true;

        private DeltaBipartiteIterator(BipartiteIntDomain dom) {
            domain = dom;
            init();
        }

        public void init() {
            currentIndex = beginningOfDeltaDomain;
            disposed = false;
        }

        public void dispose() {
            disposed = true;
        }

        public boolean hasNext() {
            return currentIndex < endOfDeltaDomain;
        }

        public int next() {
            return values[currentIndex++];
        }

        public void remove() {
            if (currentIndex == -1) {
                throw new IllegalStateException();
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    /**
     * A pointer to the first removed value to be propagated.
     * its position in the list
     */
    protected int beginningOfDeltaDomain;

    /**
     * A pointer on the last removed value propagated.
     * its position in the list
     */
    protected int endOfDeltaDomain;

    /**
     * The delta domain container is "frozen" (it can no longer accept new value removals)
     * so that this set of values can be iterated as such
     */
    public void freezeDeltaDomain() {
        // freeze all data associated to bounds for the the event
        super.freezeDeltaDomain();
        beginningOfDeltaDomain = valuesInDomainNumber.get() + 1;
    }

    /**
     * after an iteration over the delta domain, the delta domain is reopened again.
     *
     * @return true iff the delta domain is reopened empty (no updates have been made to the domain
     *         while it was frozen, false iff the delta domain is reopened with pending value removals (updates
     *         were made to the domain, while the delta domain was frozen).
     */
    public boolean releaseDeltaDomain() {
        // release all data associated to bounds for the the event
        super.releaseDeltaDomain();
        endOfDeltaDomain = beginningOfDeltaDomain;
        beginningOfDeltaDomain = valuesInDomainNumber.get() + 1;
        return beginningOfDeltaDomain == endOfDeltaDomain;
    }

    public boolean getReleasedDeltaDomain() {
        return beginningOfDeltaDomain == endOfDeltaDomain;
    }

    /**
     * cleans the data structure implementing the delta domain
     */
    public void clearDeltaDomain() {
        beginningOfDeltaDomain = valuesInDomainNumber.get() + 1;
        endOfDeltaDomain = beginningOfDeltaDomain;
    }

    public boolean isEnumerated() {
        return true;
    }

    public boolean isBoolean() {
        return false;
    }

    public String pretty() {
        StringBuffer buf = new StringBuffer("{");
        int maxDisplay = 15;
        int count = 0;
        for (IntIterator it = this.getIterator(); (it.hasNext() && count < maxDisplay);) {
            int val = it.next();
            count++;
            if (count > 1) buf.append(", ");
            buf.append(val);
        }
        if (this.getSize() > maxDisplay) {
            buf.append("..., ");
            buf.append(this.getSup());
        }
        buf.append("}");
        return buf.toString();
    }

}
