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
import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.IStateIntInterval;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IntervalListDomain extends AbstractIntDomain {

    protected final List<IStateIntInterval> intervalVector;

    private int size;
    //Records the last pair value/indice research
    private int[] lastsearch;

    /**
     * A random generator for random value from the domain
     */

    public static Random random = new Random(System.currentTimeMillis());

    public IntervalListDomain(IntDomainVarImpl v, int a, int b) {
        variable = v;
        problem = v.getProblem();
        IEnvironment env = problem.getEnvironment();
        intervalVector = new ArrayList();
        intervalVector.add(0, env.makeIntInterval(a, b));
        size = b - a + 1;
        lastsearch = new int[2];
    }

    private int searchSubDom(int val) {
        int idxVect = 0;
        int binf = val>=lastsearch[0]?lastsearch[1]:0;
        int bsup = val<=lastsearch[0]?lastsearch[1]:intervalVector.size()-1;
        int delta;
        boolean trouve = false;
        boolean trouvable = true;
        IStateIntInterval istmp;
        while (trouvable && !trouve) {
            delta = ((bsup + binf) / 2) - idxVect;
            idxVect += delta;
            istmp = intervalVector.get(idxVect);
            if (istmp.contains(val)) {
                lastsearch[0]=val;
                lastsearch[1]=idxVect;
                trouve = true;
            } else {
                if (binf == bsup || binf>bsup) {
                    trouvable = false;
                    continue;
                }
                if (val < istmp.getInf()) {
                    bsup = idxVect - 1;
                    continue;
                }
                if (val > istmp.getSup()) {
                    binf = idxVect + 1;
                }
            }
        }
        if (!trouvable) {
            idxVect = -1;
        }
        return idxVect;
    }

    /**
     * Check if it contains <code>x</code>
     *
     * @param x the value to check
     * @return true if it contains x, false else.
     */
    public boolean contains(int x) {
        if (x < intervalVector.get(0).getInf() || x > intervalVector.get(intervalVector.size() - 1).getSup()) {
            return false;
        }
        int i = searchSubDom(x);
        while (i > -1 && i < intervalVector.size()) {
            if (x < intervalVector.get(i).getInf()) {
                return false;
            }
            if (x <= intervalVector.get(i).getSup()) {
                return true;
            }
            if (x > intervalVector.get(i).getSup()) {
                i++;
            }
        }
        return false;
    }

    /**
     * Return the value of the following <code>x</code>
     *
     * @param x the previous value
     * @return the value that follows <code>x</code>
     */
    public int getNextValue(int x) {
        if (x < getInf()) {
            return getInf();
        }
        if (x < getSup()) {
            int i = searchSubDom(x);
            i = (i > -1 ? i : 0);
            while (i < intervalVector.size() ) {
                if (x < intervalVector.get(i).getInf()) {
                    return intervalVector.get(i).getInf();
                }
                if (x < intervalVector.get(i).getSup()) {
                    return x + 1;
                }
                if (x >= intervalVector.get(i).getSup()) {
                    i++;
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    /**
     * Return the value of the previous <code>x</code>
     *
     * @param x the following value
     * @return the value that previous <code>x</code>
     */
    public int getPrevValue(int x) {
        if (x > getSup()) {
            return getSup();
        }
        if (x > getInf()) {
            int i = searchSubDom(x);
            i = (i > -1 ? i : intervalVector.size()-1);
            while (i > -1) {
                if (x > intervalVector.get(i).getSup()) {
                    return intervalVector.get(i).getSup();
                }
                if (x > intervalVector.get(i).getInf()) {
                    return x - 1;
                }
                if (x <= intervalVector.get(i).getInf()) {
                    i--;
                }
            }
        }
        return Integer.MIN_VALUE;
    }


    /**
     * return a random value
     *
     * @return a randomize value
     */
    public int getRandomValue() {
        if (size == 1) return getInf();
        if (intervalVector.size() == 1)
            return intervalVector.get(0).getInf() + random.nextInt(intervalVector.get(0).getSup() - intervalVector.get(0).getInf());
        int vec = random.nextInt(intervalVector.size() - 1);
        if (intervalVector.get(vec).getSize() == 1) {
            return intervalVector.get(vec).getInf();
        }
        return intervalVector.get(vec).getInf() + random.nextInt(intervalVector.get(vec).getSup() - intervalVector.get(vec).getInf());
    }

    /**
     * Return the size of the domain
     *
     * @return the size of the domain
     */
    public int getSize() {
        return size;
    }


    /**
     * Return the size of the vector domain
     *
     * @return the size of the vector domain
     */
    public int getVectorSize() {
        return intervalVector.size();
    }

    /**
     * Check if there is a next value in the domain
     *
     * @param x the last value
     * @return wether there is a next value
     */
    public boolean hasNextValue(int x) {
        return (x < intervalVector.get(intervalVector.size() - 1).getSup());
    }

    /**
     * Check if there is a previous value in the domain
     *
     * @param x the last value
     * @return wether there is a previous value
     */
    public boolean hasPrevValue(int x) {
        return (x > intervalVector.get(0).getInf());
    }


    /**
     * Remove <code>x</code> from the domain
     *
     * @param x to remove
     * @return wether the value has been removed
     */
    public boolean remove(int x) {
        int i = searchSubDom(x);
        if (i > -1) {
            IStateIntInterval istmp = intervalVector.get(i);
            if (istmp.getSize() == 1) {
                size -= (istmp.getSize());
                intervalVector.remove(i);
                return true;
            }
            if (x == istmp.getInf()) {
                updateInf(x + 1, i);
                return true;
            }
            if (x == istmp.getSup()) {
                updateSup(x - 1, i);
                return true;
            }
            if (istmp.getSize() > 2) {
                intervalVector.add(i + 1, variable.getProblem().getEnvironment().makeIntInterval(x + 1, istmp.getSup()));
                updateSup(x - 1, i);
                size += intervalVector.get(i + 1).getSize();
                return true;
            }
        }
        return false;
    }


    /**
     * Removes from x to y inside the domain
     * @param x the sub lower bound
     * @param y the sub upper bound
     * @return weter the sub domain has been removed
     */
    public boolean remove(int x, int y){
        int i = searchSubDom(x);
        int j = searchSubDom(y);
        //TODO
        return false;
    }

    /**
     * Return the upper bound
     *
     * @return the upper bound
     */
    public int getSup() {
        return intervalVector.get(intervalVector.size() - 1).getSup();
    }

    /**
     * Return the lower bound
     *
     * @return the lower bound
     */
    public int getInf() {
        return intervalVector.get(0).getInf();
    }

    /**
     * Restict the domain to <code>x</code>.
     *
     * @param x the value to restrict the domain with
     */
    public void restrict(int x) {
        intervalVector.clear();
        intervalVector.add(0, variable.getProblem().getEnvironment().makeIntInterval(x, x));
        size = 1;
    }

    /**
     * Remove an specific element from the vector
     *
     * @param idx the index of the sub domain to remove
     */
    private void removeElement(int idx) {
        size -= intervalVector.get(idx).getSize();
        intervalVector.remove(idx);

    }

    /**
     * Update the lower boudn of a specific sub domain to x
     *
     * @param x  the new lower bound of the sub domain
     * @param id index of the sub interval
     */
    @Override
    public boolean updateInf(int x, int id) {
        int k = intervalVector.get(id).getInf();
        intervalVector.get(id).setInf(x);
        size -= intervalVector.get(id).getInf() - k;
        return true;
    }

    /**
     * Update the upepr bound of a specific sub domain to x
     *
     * @param x  the new lower bound of the sub domain
     * @param id index of the sub interval
     */
    @Override
    public boolean updateSup(int x, int id) {
        int k = intervalVector.get(id).getSup();
        intervalVector.get(id).setSup(x);
        size -= k - intervalVector.get(id).getSup();
        return true;
    }

    /**
     * Update the lower bound
     *
     * @param x the new lower bound
     * @return the new lower bound
     */
    public int updateInf(int x) {
        boolean uptodate = false;
        while (!uptodate) {
            if (x > intervalVector.get(0).getSup()) {
                removeElement(0);
                continue;
            }
            if (x > intervalVector.get(0).getInf()) {
                updateInf(x, 0);
                uptodate = true;
            }
        }
        return x;
    }


    /**
     * Update the upper bound
     *
     * @param x the new upper bound
     * @return the new upper bound
     */
    public int updateSup(int x) {
        boolean uptodate = false;
        while (!uptodate) {
            if (x < intervalVector.get(intervalVector.size() - 1).getInf()) {
                removeElement(intervalVector.size() - 1);
                continue;
            }
            if (x < intervalVector.get(intervalVector.size() - 1).getSup()) {
                updateSup(x, intervalVector.size() - 1);
                uptodate = true;
            }
        }
        return x;
    }

    public boolean isEnumerated() {
        return true;
    }

    public boolean isBoolean() {
        return false;
    }

    public IntIterator getDeltaIterator() {
        return null;
    }

    /**
     * Pretty print of the domain
     *
     * @return a pretty print of the domain
     */
    @Override
    public String pretty() {
        StringBuffer st = new StringBuffer("[");
        for (int i = 0; i < intervalVector.size(); i++) {
            IStateIntInterval aBoundsVector = intervalVector.get(i);
            st.append(aBoundsVector.toString());
        }
        st.append("]");
        return st.toString();
    }

}
