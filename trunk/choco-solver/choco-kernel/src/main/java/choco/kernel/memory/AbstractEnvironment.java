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
package choco.kernel.memory;

import choco.kernel.memory.structure.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Super class of all environments !
 */
public abstract class AbstractEnvironment implements IEnvironment {
    /**
     * The current world index.
     */
    protected int currentWorld = 0;

    private static final int SIZE = 128;

    /**
     * Shared BitSet
     */
    public IStateIntVector currentBitSet;
    /**
     * Nex free bit in the shared BitSet
     */
    protected int nextOffset;

    public final int getWorldIndex() {
        return currentWorld;
    }

    public final void createSharedBipartiteSet(int size){
        currentBitSet = makeBipartiteSet(size);
        nextOffset = -1;
    }

    /**
     * Factory pattern : shared StoredBitSetVector objects is return by the environment
     *
     * @return
     */
    @Override
    public final IStateIntVector getSharedBipartiteSetForBooleanVars() {
        if(currentBitSet == null){
            createSharedBipartiteSet(SIZE);
        }
        nextOffset++;
        if(nextOffset > currentBitSet.size()-1){
            increaseSizeOfSharedBipartiteSet(SIZE-(nextOffset % SIZE));
        }
        return currentBitSet;
    }

    /**
     * Return the next free bit in the shared StoredBitSetVector object
     *
     * @return
     */
    @Override
    public final int getNextOffset() {
        return nextOffset;
    }

    @SuppressWarnings({"unchecked"})
    public <E> StoredBipartiteSet makeStoredBipartiteList(Collection<E> coll){
        return new StoredBipartiteSet(this, coll);
    }

    @SuppressWarnings({"unchecked"})
    public <E> StoredBipartiteSet makeStoredBipartiteList2(E[] elm){
        return new StoredBipartiteSet(this, elm);
    }

    public IStateIntVector makeBipartiteIntList(int[] entries) {
		return new StoredIntBipartiteList(this,entries);
	}

    public IStateIntVector makeBipartiteSet(int[] entries) {
		return new StoredIndexedBipartiteSet(this,entries);
	}

	public IStateIntVector makeBipartiteSet(int nbEntries) {
		return new StoredIndexedBipartiteSet(this,nbEntries);
	}

	public IStateIntVector makeBipartiteSet(IndexedObject[] entries) {
		return new StoredIndexedBipartiteSet(this,entries);
	}

	public IStateIntVector makeBipartiteSet(ArrayList<IndexedObject> entries) {
		return new StoredIndexedBipartiteSet(this,entries);
	}

    public <T> PartiallyStoredVector<T> makePartiallyStoredVector() {
        return new PartiallyStoredVector<T>(this);
    }

    public PartiallyStoredIntVector makePartiallyStoredIntVector() {
        return new PartiallyStoredIntVector(this);
    }

    public PartiallyStoredBitSet makePartiallyStoredBitSet(){
        return new PartiallyStoredBitSet(this);
    }

    public TwoStatesIntVector makeTwoStateIntVector(int initialSize, int initialValue){
        return new TwoStatesIntVector(this, initialSize, initialValue);
    }

    public TwoStatesPartiallyStoredIntVector makeTwoStatesPartiallyStoredIntVector(){
        return new TwoStatesPartiallyStoredIntVector(this);
    }

    public IntInterval makeIntInterval(int inf, int sup){
        return new IntInterval(this, inf, sup);
    }

    /**
     * Factory pattern: new IStateBitSet objects are created by the environment
     *
     * @param size initail size of the IStateBitSet
     * @return IStateBitSet
     */
    @Override
    public IStateBitSet makeBitSet(int size) {
        return new SBitSet(this, size);
    }

    /**
	 * Increase the size of the shared bi partite set,
	 * it HAS to be called before the end of the environment creation
	 * BEWARE: be sure you are correctly calling this method
	 *
	 * @param gap the gap the reach the expected size
	 */
	@Override
	public void increaseSizeOfSharedBipartiteSet(int gap) {
		((StoredIndexedBipartiteSet)currentBitSet).increaseSize(gap);
	}

}
