// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.var;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;
import i_want_to_use_this_old_version_of_choco.util.DisposableIntIterator;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.Random;

public class IntervalIntDomain extends AbstractIntDomain {
    /**
     * A random generator for random value from the domain
     */

    protected static Random random = new Random(System.currentTimeMillis());

    /**
     * The backtrackable minimal value of the variable.
     */

    protected final IStateInt inf;

    /**
     * The backtrackable maximal value of the variable.
     */

    protected final IStateInt sup;

    public IntervalIntDomain(IntDomainVarImpl v, int a, int b) {
        variable = v;
        problem = v.getProblem();
        IEnvironment env = problem.getEnvironment();
        inf = env.makeInt(a);
        sup = env.makeInt(b);
    }

    public boolean contains(int x) {
        return ((x >= getInf()) && (x <= getSup()));
    }

    public int getNextValue(int x) {
        if (x < getInf()) {
            return getInf();
        } else if (x < getSup()) {
            return x + 1;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    public int getPrevValue(int x) {
        if (x > getSup()) {
            return getSup();
        } else if (x > getInf()) {
            return x - 1;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    public int getRandomValue() {
        int inf = getInf();
        int rand = random.nextInt(this.getSize());
        return inf + rand;
    }

    public int getSize() {
        return getSup() - getInf() + 1;
    }

    public boolean hasNextValue(int x) {
        return (x < getSup());
    }

    public boolean hasPrevValue(int x) {
        return (x > getInf());
    }

    public DisposableIntIterator getIterator() {
        return new DisposableIntIterator() {
            int x = getInf() - 1;

            public boolean hasNext() {
                return x < getSup();
            }

            public int next() {
                return ++x;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            public void dispose() {}
        };
    }

    public boolean remove(int x) {
        return false;
    }

    public int getSup() {
        return sup.get();
    }

    public int getInf() {
        return inf.get();
    }

    public void restrict(int x) {
        inf.set(x);
        sup.set(x);
    }

    public int updateInf(int x) {
        inf.set(x);
        return x;
    }

    public int updateSup(int x) {
        sup.set(x);
        return x;
    }

    protected boolean _removeVal(int x, int idx) throws ContradictionException {
        int infv = getInf(), supv = getSup();
        if (x == infv) {
            _updateInf(x + 1, idx);
            if (getInf() == supv) _instantiate(supv, idx);
            return true;
        } else if (x == supv) {
            _updateSup(x - 1, idx);
            if (getSup() == infv) _instantiate(infv, idx);
            return true;
        } else {
            return false;
        }
    }

    public boolean isEnumerated() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public IntIterator getDeltaIterator() {
        return null;
    }

    public String pretty() {
        return ("[" + this.getInf() + " .. " + this.getSup() + "]");
    }

}
