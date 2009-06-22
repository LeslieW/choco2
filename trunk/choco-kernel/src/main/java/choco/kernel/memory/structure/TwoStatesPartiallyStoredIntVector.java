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

import choco.kernel.common.util.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;

import java.util.Arrays;


/**
 * A class implementing a vector with two kind of storage:
 * standard static storage in an array, and backtrackable storage.
 * By convention, integers with small indices (0 .. 999999) are statically managed
 * as if they were in a standard array.
 * And integers with large indices (1000000 ... ) are "stored" in a backtrackable
 * manner, as if they were in a StoredIntVector
 */
public class TwoStatesPartiallyStoredIntVector extends PartiallyStoredIntVector {

    protected int[] staticIndices;
    protected int[] storedIndices;

    protected int offsetStatic;
    protected IStateInt offsetStored;

    public TwoStatesPartiallyStoredIntVector(IEnvironment env) {
        super(env);
        staticIndices = new int[INITIAL_STATIC_CAPACITY];
        storedIndices = new int[INITIAL_STORED_CAPACITY];
        Arrays.fill(staticIndices, -1);
        Arrays.fill(storedIndices, -1);
        offsetStatic = 0;
        offsetStored = env.makeInt(0);
    }

    public void staticAdd(int o, boolean isActive) {
        super.ensureStaticCapacity(nStaticInts+1);
        ensureStaticCapacity2(o);
        // If active => left to offset
        if(isActive){
            if(offsetStatic >= nStaticInts){
                staticInts[nStaticInts] = o;
                staticIndices[o] = nStaticInts++;
                offsetStatic++;
            }else{
                int to = staticInts[offsetStatic];
                staticInts[offsetStatic] = o;
                staticIndices[o] = offsetStatic++;
                staticInts[nStaticInts] = to;
                staticIndices[to] = nStaticInts++;
            }
        }
        // if inactive => right to offset
        else{
            staticInts[nStaticInts] = o;
            staticIndices[o] = nStaticInts++;
        }
    }

    public void ensureStaticCapacity2(int ind) {
        if (ind >= staticIndices.length) {
            int newSize = staticIndices.length;
            while (ind >= newSize) {
                newSize = (3 * newSize) / 2;
            }
            int[] newStaticObjects = new int[newSize];
            Arrays.fill(newStaticObjects, -1);
            System.arraycopy(staticIndices, 0, newStaticObjects, 0, staticIndices.length);
            staticIndices = newStaticObjects;
        }
    }

    public void add(int ob, boolean isActive) {
        int o = ob - STORED_OFFSET;
        super.ensureStoredCapacity(nStoredInts.get()+1);
        ensureStoredCapacity2(o);
        int offset = offsetStored.get();
        // If active => left to offset
        if(isActive){
            if(offset >= nStoredInts.get()){
                storedInts[nStoredInts.get()] = o;
                storedIndices[o] = nStoredInts.get();
                nStoredInts.add(1);
                offsetStored.add(1);
            }else{
                int to = storedInts[offsetStatic];
                storedInts[offsetStored.get()] = o;
                storedIndices[o] = offset;
                offsetStored.add(1);
                storedInts[nStoredInts.get()] = to;
                storedIndices[to] = nStoredInts.get();
                nStoredInts.add(1);
            }
        }
        // if inactive => right to offset
        else{
            storedInts[nStoredInts.get()] = o;
            storedIndices[o] = nStoredInts.get();
            nStoredInts.add(1);
        }
    }

    public void ensureStoredCapacity2(int ind) {
        if (ind >= storedIndices.length) {
            int newSize = storedIndices.length;
            while (ind >= newSize) {
                newSize = (3 * newSize) / 2;
            }
            int[] newStaticObjects = new int[newSize];
            Arrays.fill(newStaticObjects, -1);
            System.arraycopy(storedIndices, 0, newStaticObjects, 0, storedIndices.length);
            storedIndices = newStaticObjects;
        }
    }

    @Override
    public void remove(int o) {
        System.err.println("Remove from TwoStatesPartiallyStoredIntVector is not implemented !!");
//        staticInts[o] = staticInts[nStaticInts];
//        staticInts[nStaticInts] = 0;
//        nStaticInts--;
//        if (o < offsetStatic) offsetStatic--;
    }

    public void set(int idx, boolean active){
        // static object
        if(idx < STORED_OFFSET){
            if(active && staticIndices[idx] >= offsetStatic){
                if(staticIndices[idx] != offsetStatic){
                    // swap with the first passive
                    int to = staticInts[staticIndices[idx]];

                    staticInts[staticIndices[idx]] = staticInts[offsetStatic];
                    staticIndices[staticInts[staticIndices[idx]]] = staticIndices[idx];

                    staticInts[offsetStatic] = to;
                    staticIndices[to] = offsetStatic;
                }
                offsetStatic++;
            }else if(!active && staticIndices[idx] < offsetStatic){
                if(staticIndices[idx]!= offsetStatic-1){
                    // swap last active
                    int to = staticInts[staticIndices[idx]];

                    staticInts[staticIndices[idx]] = staticInts[offsetStatic-1];
                    staticIndices[staticInts[staticIndices[idx]]] = staticIndices[idx];

                    staticInts[offsetStatic-1] = to;
                    staticIndices[to] = offsetStatic-1;
                }
                offsetStatic--;
            }
        }
        // dynamic object
        else{
            int newIdx = idx - STORED_OFFSET;
            int offset = offsetStored.get();
            if(active && storedIndices[newIdx] >= offset){
                if(storedIndices[newIdx] != offset){
                    // swap with the first passive
                    int to = storedInts[storedIndices[newIdx]];

                    storedInts[storedIndices[newIdx]] = storedInts[offset];
                    storedIndices[storedInts[storedIndices[newIdx]]] = storedIndices[newIdx];

                    storedInts[offset] = to;
                    storedIndices[to] = offset;
                }
                offsetStored.add(1);

            }else if(!active && storedIndices[newIdx] < offset){
                if(storedIndices[newIdx]!= offset-1){
                    // swap last active
                    int to = storedInts[storedIndices[newIdx]];

                    storedInts[storedIndices[newIdx]] = storedInts[offset-1];
                    storedIndices[storedInts[storedIndices[newIdx]]] = storedIndices[newIdx];

                    storedInts[offset-1] = to;
                    storedIndices[to] = offset-1;
                }
                offsetStored.add(-1);
            }
        }
    }

    protected DisposableIntIterator _cachedIndexIterator = null;

    @Override
    public DisposableIntIterator getIndexIterator() {
        ActiveIterator iter = (ActiveIterator) _cachedIndexIterator;
        if (iter != null && iter.reusable) {
            iter.init();
            return iter;
        }
        _cachedIndexIterator = new ActiveIterator();
        return _cachedIndexIterator;
    }

    class ActiveIterator extends DisposableIntIterator {
        int staticI = -1;
        int storedI = -1;

        boolean stats = (offsetStatic > 0);
        boolean storeds = (offsetStored.get() > 0);

        ActiveIterator() {
            init();
        }

        public void init() {
            super.init();
            staticI = -1;
            storedI = -1;
            stats = (offsetStatic > 0);
            storeds = (offsetStored.get() > 0);
        }

        public boolean hasNext() {
            if (staticI == -1 && storedI == -1) {
                return stats || storeds;
            } else {
                return ((stats && staticI < offsetStatic - 1)
                        || (staticI == offsetStatic && storeds)
                        || (storeds && storedI < offsetStored.get() - 1));
            }
        }

        public int next() {
            if (staticI < offsetStatic - 1) {
                staticI++;
                return staticInts[staticI];
            } else {
                storedI++;
                return storedInts[storedI] + STORED_OFFSET;
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}