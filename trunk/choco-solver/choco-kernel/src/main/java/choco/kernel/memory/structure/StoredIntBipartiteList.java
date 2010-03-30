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
import choco.kernel.memory.structure.iterators.BipartiteListIterator;
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
public final class StoredIntBipartiteList implements IStateIntVector {

    /**
     * The list of values
     */
    private final int[] list;

    /**
     * The first element of the list
     */
    private final IStateInt last;


    public StoredIntBipartiteList(final IEnvironment environment, final int[] values) {
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

    public void remove(final int i) {
        throw new UnsupportedOperationException("removing element is not permitted in this structure (the list is only meant to decrease during search)");
    }

    public void removeLast() {
        last.add(-1);
    }

    public int get(final int index) {
        return list[index];
    }

    @Override
    public int quickGet(final int index) {
        return list[index];
    }

    @Override
    public boolean contain(final int val) {
        final int llast = last.get();
        for (int i = 0; i< llast; i++){
            if(val == list[i]){
                return true;
            }
        }
        return false;
    }

    public int set(final int index, final int val) {
        throw new SolverException("setting an element is not permitted on this structure");
    }

    @Override
    public int quickSet(final int index, final int val) {
        return set(index,val);
    }

    public DisposableIntIterator getIterator() {
        return BipartiteListIterator.getIterator(list, last); 
    }

    public String pretty() {
        final StringBuilder s = new StringBuilder("[");
        for (int i = 0; i <= last.get(); i++) {
            s.append(list[i]).append(i == last.get() ? "" : ",");
        }
        return s.append(']').toString();
    }
}
