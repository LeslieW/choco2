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

import choco.cp.solver.variables.delta.BipartiteDeltaDomain;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.iterators.OneValueIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.propagation.PropagationEngine;

import java.util.Arrays;
import java.util.Random;

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
     * @param environment
     * @param propagationEngine
     */
    public BipartiteIntDomain(IntDomainVarImpl v, int[] sortedValues, IEnvironment environment, PropagationEngine propagationEngine) {
        super(propagationEngine);
        assert(v!=null);
        assert(sortedValues!=null);
        init(v, sortedValues, environment);
    }

    /**
     * Constructs a new domain for the specified variable and bounds.
     *
     * @param v   The involved variable.
     * @param low Minimal value.
     * @param up  Maximal value.
     * @param environment
     * @param propagationEngine
     */
    public BipartiteIntDomain(IntDomainVarImpl v, int low, int up, IEnvironment environment, PropagationEngine propagationEngine) {
        super(propagationEngine);
        // Pre-condition
        assert(v!=null);
        assert(low <= up);
        int[] sortedValues = new int[up - low + 1];
        for (int i = 0; i < sortedValues.length; i++) {
            sortedValues[i] = low + i;
        }
        init(v, sortedValues, environment);
    }

    public void init(IntDomainVarImpl v, int[] sortedValues, IEnvironment environment) {
        // Constructor
        variable = v;
        int low = sortedValues[0];
        int up = sortedValues[sortedValues.length - 1];
        int size = sortedValues.length;
        // Initial lower bound is recorded as an offset 
        offset = low;
        // Initialisation of values
        values = new int[size];
        System.arraycopy(sortedValues, 0, values, 0, sortedValues.length);
        // By default all values are in...
        valuesInDomainNumber = environment.makeInt(size - 1);
        // Indices are first simply offset...
        indices = new int[up - low + 1];
        Arrays.fill(indices, Integer.MAX_VALUE);
        for (int i = 0; i < values.length; i++) {
            indices[values[i] - offset] = i;
        }
        // At the beginning nothing has been propageted !
        deltaDom = new BipartiteDeltaDomain(size, values, valuesInDomainNumber);
        inf = environment.makeInt(low);
        sup = environment.makeInt(up);
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
                    propagationEngine.raiseContradiction(idx, variable);
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
                    propagationEngine.raiseContradiction(idx, variable);
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
            deltaDom.remove(mark);
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
        deltaDom.remove(valuesInDomainNumber.get());
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
        if(getSize() == 1) return OneValueIterator.getOneValueIterator(getInf());
        BipartiteIntDomainIterator iter = (BipartiteIntDomainIterator) _cachedIterator;
        if (iter != null && iter.reusable) {
            iter.init();
            return iter;
        }
        _cachedIterator = new BipartiteIntDomainIterator(this);
        return _cachedIterator;
    }

    protected static class BipartiteIntDomainIterator extends DisposableIntIterator {
        BipartiteIntDomain domain;
        protected int nextIdx;

        private BipartiteIntDomainIterator(BipartiteIntDomain dom) { //AbstractIntDomain dom) {
            this.domain = dom;
            init();
        }

        @Override
        public void init() {
            super.init();
            nextIdx = domain.valuesInDomainNumber.get();
        }

        public boolean hasNext() {
            return nextIdx >= 0;
        }

        public int next() {
            int v = nextIdx;
            nextIdx--;
            return domain.values[v];
        }
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
        int count = 1;

        int current = this.getInf();
        buf.append(current);
        while(count < maxDisplay && this.hasNextValue(current)){
            current = this.getNextValue(current);
            count++;
            if (count > 0) buf.append(", ");
            buf.append(current);
        }
        if (this.getSize() > maxDisplay) {
            buf.append(", ..., ");
            buf.append(this.getSup());
        }
        buf.append("}");
        return buf.toString();
    }
}