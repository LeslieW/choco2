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
package choco.kernel.memory.structure;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.IStateIntVector;
import choco.kernel.solver.SolverException;

/**
 * A stored list dedicated to two operations :
 * - iteration
 * - removal of an element during iteration
 * It only requires a StoredInt to denote the first element of the list
 * and proceeds by swapping element with the first one to remove them and incrementing
 * the index of the first element.
 * IT DOES NOT PRESERVE THE ORDER OF THE LIST
 */
public class StoredIntBipartiteList implements IStateIntVector {

    /**
     * The list of values
     */
    protected int[] list;

    /**
     * The first element of the list
     */
    protected IStateInt last;


    /**
     * An iterator to be reused
     */
    protected DisposableIntIterator _cachedIterator;

    public StoredIntBipartiteList(IEnvironment environment, int[] values) {
        this.list = values;
        this.last = environment.makeInt(values.length - 1);
    }


    public int size() {
        return last.get() + 1;
    }

    public boolean isEmpty() {
        return last.get() == -1;
    }

    public void add(final int i) {
        throw new UnsupportedOperationException("adding element is not permitted in this structure (the list is only meant to decrease during search)");
    }

    public void remove(int i) {
        throw new UnsupportedOperationException("removing element is not permitted in this structure (the list is only meant to decrease during search)");
    }

    public void removeLast() {
        last.add(-1);
    }

    public int get(final int index) {
        return list[index];
    }

    @Override
    public int unsafeGet(int index) {
        return list[index];
    }

    public int set(final int index, final int val) {
        throw new SolverException("setting an element is not permitted on this structure");
    }

    @Override
    public int unsafeSet(int index, int val) {
        return set(index,val);
    }

    public DisposableIntIterator getIterator() {
      BipartiteListIterator iter = (BipartiteListIterator) _cachedIterator;
      if (iter != null && iter.reusable) {
        iter.init();
        return iter;
      }
      _cachedIterator = new BipartiteListIterator(this);
      return _cachedIterator;
    }

    public String pretty() {
        StringBuilder s = new StringBuilder("[");
        for (int i = 0; i <= last.get(); i++) {
            s.append(list[i]).append(i == last.get() ? "" : ",");
        }
        return s.append("]").toString();
    }


    protected static class BipartiteListIterator extends DisposableIntIterator {
        StoredIntBipartiteList siblist;

        int idx;

        public BipartiteListIterator(StoredIntBipartiteList list) {
            this.siblist = list;
           init();
        }

        public void init() {
            super.init();
           idx = 0;
        }

        public boolean hasNext() {
            return idx <= siblist.last.get();
        }

        public int next() {
            return siblist.list[idx++];
        }

        public void remove() {
            idx--;
            int temp = siblist.list[siblist.last.get()];
            siblist.list[siblist.last.get()] = siblist.list[idx];
            siblist.list[idx] = temp;
            siblist.last.add(-1);
        }
    }
}
