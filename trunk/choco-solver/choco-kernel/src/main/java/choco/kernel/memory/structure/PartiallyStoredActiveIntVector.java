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

import java.util.Arrays;


/**
 * A class implementing a vector with two kind of storage:
 * standard static storage in an array, and backtrackable storage.
 * By convention, integers with small indices (0 .. 999999) are statically managed
 * as if they were in a standard array.
 * And integers with large indices (1000000 ... ) are "stored" in a backtrackable
 * manner, as if they were in a StoredIntVector
 *
 * This structure also deals with a kind of state. It only records active integer.
 *
 */
public class PartiallyStoredActiveIntVector extends PartiallyStoredIntVector {

    private int[] staticIndices;
    private int[] storedIndices;

    private final int offsetStatic;

    private final IStateInt offsetStored;

    private final IStateInt lastWorld;

    private IEnvironment env;

    public PartiallyStoredActiveIntVector(IEnvironment env) {
        super(env);
        this.env = env;
        staticIndices = new int[2];
        storedIndices = new int[INITIAL_STORED_CAPACITY];
        Arrays.fill(staticIndices, -1);
        staticIndices[0] = 0;
        staticIndices[1] = 0;

        Arrays.fill(storedIndices, -1);
        storedIndices[0] = 0;
        storedIndices[1] = 0;

        offsetStatic = 1;
        offsetStored = env.makeInt(1);

        lastWorld = env.makeInt(-1);
    }

    public void staticAdd(int ob, boolean isActive) {
        if(isActive){
            super.staticAdd(ob);
            staticIndices[offsetStatic]++;
        }//else
        // Nothing to do

    }

    public void add(int ob, boolean isActive) {
       if(isActive){
            int w = this.env.getWorldIndex();
            if(lastWorld.get() != w){
                lastWorld.set(w);
                // copy the previsous array
                storedCopy();
            }
            super.add(ob);
            storedIndices[offsetStored.get()]++;
        }//else
        // Nothing to do
    }

    private void ensureStoredIndicesCapacity(int n) {
        if (n > storedIndices.length) {
          int newSize = storedIndices.length;
          while (n >= newSize) {
              newSize = (3 * newSize) / 2;
          }
          int[] newStoredObjects = new int[newSize];
          System.arraycopy(storedIndices, 0, newStoredObjects, 0, storedIndices.length);
          this.storedIndices = newStoredObjects;
      }
    }

    @Override
    public void remove(int o) {
        this.set(o, false);
    }

    public void set(int idx, boolean active){
        if(idx >= STORED_OFFSET){
            int w = this.env.getWorldIndex();
            if(active){
                add(idx, true);
            }else{
                int id = storedIndices[offsetStored.get()]-1;
                //TODO: improve the search
                while(id >= storedIndices[offsetStored.get()-1] && storedInts[id]!=idx){
                   id--;
                }
                if(id >= storedIndices[offsetStored.get()-1]){
                    if(lastWorld.get() != w){
                        lastWorld.set(w);
                        storedCopy();
                        id += (storedIndices[offsetStored.get()]-storedIndices[offsetStored.get()-1]);
                    }
                    System.arraycopy(storedInts, id+1, storedInts, id, storedIndices[offsetStored.get()]-id-1);
//                    storedInts[storedIndices[offsetStored.get()]-1] = 0;
                    nStoredInts.add(-1);
                    storedIndices[offsetStored.get()]--;
                }
            }
        }else{
            if(active){
                staticAdd(idx, true);
            }else{
                int id = staticIndices[offsetStatic]-1;
                //TODO: improve the search
                while(id >= staticIndices[offsetStatic-1] && staticInts[id]!=idx){
                   id--;
                }
                if(id >= staticIndices[offsetStatic-1]){
                    System.arraycopy(staticInts, id+1, staticInts, id, staticIndices[offsetStatic]-id-1);
//                    staticInts[staticIndices[offsetStatic]-1] = 0;
                    nStaticInts--;
                    staticIndices[offsetStatic]--;
                }
            }
        }
    }

    private void storedCopy() {
        int diff = (nStoredInts.get() - storedIndices[offsetStored.get()-1]);
        super.ensureStoredCapacity(nStoredInts.get()+diff);
        System.arraycopy(storedInts, nStoredInts.get() - diff, storedInts, nStoredInts.get(), diff);
        nStoredInts.add(diff);
        offsetStored.add(1);
        ensureStoredIndicesCapacity(offsetStored.get());
        storedIndices[offsetStored.get()] = storedIndices[offsetStored.get()-1]+diff;
    }

    protected DisposableIntIterator _cachedIterator = null;

    @Override
    public DisposableIntIterator getIndexIterator() {
        ActiveIterator iter = (ActiveIterator) _cachedIterator;
        if (iter != null && iter.reusable) {
            iter.init();
            return iter;
        }
        _cachedIterator = new ActiveIterator(this);
        return _cachedIterator;
    }

    private static class ActiveIterator extends DisposableIntIterator {
        final PartiallyStoredActiveIntVector vector;
        int[] values;
        int i;

        ActiveIterator(PartiallyStoredActiveIntVector vector) {
            this.vector = vector;
            init();
        }

        public void init() {
            super.init();
            int staSize = vector.staticIndices[vector.offsetStatic] - vector.staticIndices[vector.offsetStatic-1];
            int offsetSto = vector.offsetStored.get();
            int stoSize = vector.storedIndices[offsetSto] - vector.storedIndices[offsetSto-1];
            values = new int[staSize+stoSize];
            System.arraycopy(vector.staticInts, vector.staticIndices[vector.offsetStatic-1], values, 0, staSize);
            System.arraycopy(vector.storedInts, vector.storedIndices[offsetSto-1], values, staSize, stoSize);
            i = 0;
        }

        public boolean hasNext() {
            return (i < values.length);
        }

        public int next() {
            return values[i++];
        }
    }

}