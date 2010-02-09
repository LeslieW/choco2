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

import java.util.ArrayList;

/**
 * A stored list dedicated to positive integers and three operations :
 * - iteration
 * - removal of an element
 * - check if an element is or not within the list
 * It only requires a StoredInt to denote the first element of the list
 * and proceeds by swapping element with the first one to remove them and incrementing
 * the index of the first element.
 * IT DOES NOT PRESERVE THE ORDER OF THE LIST
 */
public class StoredIndexedBipartiteSet implements IStateIntVector {

    /**
     * The list of values
     */
    protected int[] list;

    /**
     * The position of each element within the list.
     * indexes[3] = k <=> list[k] = 3
     * we assume that elements ranges from 0 ... list.lenght
     * in other words the elements must be indexed.
     */
    protected int[] position;

    /**
     * If objects are added to the list, a mapping from their
     * indexes is needed.
     * idxToObjects[i] = o <=> o.getObjectIdx() == i
     */
    protected IndexedObject[] idxToObjects;

    /**
     * The first element of the list
     */
    protected IStateInt last;


    /**
     * An iterator to be reused
     */
    protected DisposableIntIterator _cachedIterator;

    /**
     * @param environment
     * @param values:     a set of DIFFERENT positive integer values !
     */
    public StoredIndexedBipartiteSet(IEnvironment environment, int[] values) {
        buildList(environment, values);
    }

    /**
     * @param environment
     * @param values:     a set of IndexObjects which have different indexes !
     */
    public StoredIndexedBipartiteSet(IEnvironment environment, IndexedObject[] values) {
        int[] intvalues = new int[values.length];
        for (int i = 0; i < intvalues.length; i++) {
            intvalues[i] = values[i].getObjectIdx();
        }
        buildList(environment, intvalues);
        idxToObjects = new IndexedObject[position.length];
        for (int i = 0; i < intvalues.length; i++) {
            idxToObjects[values[i].getObjectIdx()] = values[i];
        }
    }

    /**
     * @param environment
     * @param values:     a set of IndexObjects which have different indexes !
     */
    public StoredIndexedBipartiteSet(IEnvironment environment, ArrayList<IndexedObject> values) {
        int[] intvalues = new int[values.size()];
        for (int i = 0; i < intvalues.length; i++) {
            intvalues[i] = values.get(i).getObjectIdx();
        }
        buildList(environment, intvalues);
        idxToObjects = new IndexedObject[position.length];
        for (int i = 0; i < intvalues.length; i++) {
            idxToObjects[values.get(i).getObjectIdx()] = values.get(i);
        }
    }

    public void buildList(IEnvironment environment, int[] values) {
        this.list = values;
        int maxElt = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > maxElt) maxElt = values[i];
        }
        this.position = new int[maxElt + 1];
        for (int i = 0; i < values.length; i++) {
            position[values[i]] = i;
        }
        this.last = environment.makeInt(list.length - 1);
    }

    /**
     * Create a stored bipartite set with a size.
     * Thus the value stored will go from 0 to nbValues.
     * @param environment
     * @param nbValues
     */
    public StoredIndexedBipartiteSet(IEnvironment environment, int nbValues) {
        int[]values = new int[nbValues];
        for(int i = 0; i < nbValues; i++){
            values[i]=i;
        }
        buildList(environment, values);
    }

    /**
     * Increase the number of value watched.
     * BEWARE: be sure your are correctly calling this method.
     * It deletes everything already declared
     * @param gap the gap the reach the expected size
     */
    public void increaseSize(int gap){
        int l = list.length;
        int[]newList = new int[l+gap];
        for(int i = 0; i < l+gap; i++){
            newList[i] = i;
        }
        int maxElt = 0;
        for (int i = 0; i < newList.length; i++) {
            if (newList[i] > maxElt) maxElt = newList[i];
        }
        int[]newPosition = new int[maxElt + 1];
        for (int i = 0; i < newList.length; i++) {
            newPosition[newList[i]] = i;
        }
        // record already removed values
        int end = last.get()+1;
        int[] removed = new int[list.length - end];
        System.arraycopy(list, end, removed, 0, list.length-end);

        this.list = newList;
        this.position = newPosition;
        IEnvironment env = last.getEnvironment();
        this.last = null;
        this.last = env.makeInt(list.length - 1);
        for (int i = 0; i < removed.length; i++) {
            remove(removed[i]);
        }
    }

    public int size() {
        return last.get() + 1;
    }

    public boolean isEmpty() {
        return last.get() == -1;
    }

    public void add(int i) {
        throw new UnsupportedOperationException("adding element is not permitted in this structure (the list is only meant to decrease during search)");
    }

    public void clear() {
        last.set(-1);
    }

    public void removeLast() {
        remove(list[last.get()]);
    }

    public void remove(int object) {
        if (contain(object)) {
            int idxToRem = position[object];
            if (idxToRem == last.get()) {
                last.add(-1);
            } else {
                int temp = list[last.get()];
                list[last.get()] = object;
                list[idxToRem] = temp;
                position[object] = last.get();
                position[temp] = idxToRem;
                last.add(-1);
            }
        }
    }

    //we assume that the object belongs to the list
    public void remove(IndexedObject object) {
        remove(object.getObjectIdx());
    }

    public boolean contain(int object) {
        return position[object] <= last.get();
    }

    public boolean contain(IndexedObject object) {
        return contain(object.getObjectIdx());
    }

    public final int get(int index) {
        return list[index];
    }

    @Override
    public final int quickGet(int index) {
        return get(index);
    }

    public IndexedObject getObject(int index) {
        return idxToObjects[list[index]];
    }

    public int set(int index, int val) {
        throw new SolverException("setting an element is not permitted on this structure");
    }

    @Override
    public int quickSet(int index, int val) {
        return set(index,val);
    }

    public DisposableIntIterator getIterator() {
        BipartiteSetIterator iter = (BipartiteSetIterator) _cachedIterator;
        if (iter != null && iter.reusable) {
            iter.init();
            return iter;
        }
        _cachedIterator = new BipartiteSetIterator(this);
        return _cachedIterator;
    }

    public BipartiteSetIterator getObjectIterator() {
        BipartiteSetIterator iter = (BipartiteSetIterator) _cachedIterator;
        if (iter != null && iter.reusable) {
            iter.init();
            return iter;
        }
        _cachedIterator = new BipartiteSetIterator(this);
        return (BipartiteSetIterator) _cachedIterator;
    }

    public String pretty() {
        StringBuilder s = new StringBuilder("[");
        for (int i = 0; i <= last.get(); i++) {
            s.append(list[i]).append(i == (last.get()) ? "" : ",");
        }
        return s.append("]").toString();
    }


    public static class BipartiteSetIterator extends DisposableIntIterator {
        StoredIndexedBipartiteSet sibset;

        int idx;

        public BipartiteSetIterator(StoredIndexedBipartiteSet sibset) {
            this.sibset = sibset;
            init();
        }

        public void init() {
            super.init();
            idx = 0;
        }

        public boolean hasNext() {
            return idx <= sibset.last.get();
        }

        public int next() {
            return sibset.list[idx++];
        }

        public IndexedObject nextObject() {
            return sibset.idxToObjects[sibset.list[idx++]];
        }

        public void remove() {
            idx--;
            int idxToRem = idx;
            if (idxToRem == sibset.last.get()) {
                sibset.last.add(-1);
            } else {
                int temp = sibset.list[sibset.last.get()];
                sibset.list[sibset.last.get()] = sibset.list[idxToRem];
                sibset.list[idxToRem] = temp;
                sibset.position[sibset.list[sibset.last.get()]] = sibset.last.get();
                sibset.position[temp] = idxToRem;
                sibset.last.add(-1);
            }
        }
    }

    //a is not in the list, returns its index k in the table from
    //the end of the list.
    //It basically means that a was the k element to be removed
    public int findIndexOfInt(int a) {
        return list.length - position[a];
    }
}
