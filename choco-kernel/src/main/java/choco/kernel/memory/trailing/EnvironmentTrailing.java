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
package choco.kernel.memory.trailing;


import java.util.ArrayList;

import choco.kernel.memory.AbstractEnvironment;
import choco.kernel.memory.AbstractStateBitSet;
import choco.kernel.memory.IStateBinaryTree;
import choco.kernel.memory.IStateBool;
import choco.kernel.memory.IStateDouble;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.IStateIntProcedure;
import choco.kernel.memory.IStateIntVector;
import choco.kernel.memory.IStateLong;
import choco.kernel.memory.IStateVector;
import choco.kernel.memory.PartiallyStoredIntVector;
import choco.kernel.memory.PartiallyStoredVector;

/**
 * The root class for managing memory and sessions.
 * <p/>
 * A environment is associated to each problem.
 * It is responsible for managing backtrackable data.
 */
public class EnvironmentTrailing extends AbstractEnvironment {


	/**
	 * The maximum numbers of objects that a
	 * {@link ITrailStorage} can handle.
	 */

	private int maxHist;


	/**
	 * The maximum numbers of worlds that a
	 * {@link ITrailStorage} can handle.
	 */

	private int maxWorld;



	/**
	 * Contains all the {@link ITrailStorage} trails for
	 * storing different kinds of data.
	 */

	protected ITrailStorage[] trails;

	/**
	 * Constructs a new <code>IEnvironment</code> with
	 * the default stack sizes : 50000 and 1000.
	 */

	public EnvironmentTrailing() {
		maxHist = 5000; //50000;
		maxWorld = 100; //1000;
		trails = new ITrailStorage[]{
				new StoredBoolTrail(this, maxHist, maxWorld),
				new StoredIntTrail(this, maxHist, maxWorld),
				new StoredVectorTrail(this, maxHist, maxWorld),
				new StoredIntVectorTrail(this, maxHist, maxWorld),
				new StoredDoubleTrail(this, maxHist, maxWorld),
				new StoredLongTrail(this, maxHist, maxWorld),
				new StoredBinaryTreeTrail(this,maxHist,maxWorld),
		};
	}

	/**
	 * Returns the <code>i</code>th trail in the trail array.
	 *
	 * @param i index of the trail.
	 */

	public ITrailStorage getTrail(int i) {
		return trails[i];
	}


	public void worldPush() {
		for (ITrailStorage trail : trails) {
			trail.worldPush();
		}
		currentWorld++;
		if (currentWorld + 1 == maxWorld) {
			resizeWorldCapacity(maxWorld * 3 / 2);
		}
	}


	public void worldPop() {
		for (ITrailStorage trail : trails) {
			trail.worldPop();
		}
		currentWorld--;
	}

	public void worldCommit() {
		if (currentWorld == 0) {
			throw new IllegalStateException("Commit in world 0?");
		}
		for (ITrailStorage trail : trails) {
			trail.worldCommit();
		}
		currentWorld--;
	}

	public IStateInt makeInt() {
		return makeInt(0);
	}

	public IStateInt makeInt(int initialValue) {
		return new StoredInt(this, initialValue);
	}

	@Override
	public IStateInt makeIntProcedure(IStateIntProcedure procedure,
			int initialValue) {
		return new StoredIntProcedure(this, procedure, initialValue);
	}

	public IStateBool makeBool(boolean initialValue) {
		return new StoredBool(this, initialValue);
	}

	public IStateIntVector makeIntVector() {
		return new StoredIntVector(this);
	}

	public IStateIntVector makeIntVector(int size, int initialValue) {
		return new StoredIntVector(this, size, initialValue);
	}

	public IStateIntVector makeIntVector(int[] entries) {
		return new StoredIntVector(this, entries);
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


	/**
	 * Increase the size of the shared bi partite set,
	 * it HAS to be called before the end of the environment creation
	 * BEWARE: be sure you are correctly calling this method
	 *
	 * @param gap
	 */
	@Override
	public void increaseSizeOfSharedBipartiteSet(int gap) {
		((StoredIndexedBipartiteSet)currentBitSet).increaseSize(gap);
	}


	public <T> IStateVector<T> makeVector() {
		return new StoredVector<T>(this);
	}

	public <T> PartiallyStoredVector<T> makePartiallyStoredVector() {
		return new PartiallyStoredVector<T>(this);
	}

	public PartiallyStoredIntVector makePartiallyStoredIntVector() {
		return new PartiallyStoredIntVector(this);
	}

	public AbstractStateBitSet makeBitSet(int size) {
		return new StoredBitSet(this, size);
	}

	public AbstractStateBitSet makeBitSet(int[] entries) {
		return new StoredBitSet(this, 0);  // TODO
	}

	public IStateDouble makeFloat() {
		return makeFloat(Double.NaN);
	}

	public IStateDouble makeFloat(double initialValue) {
		return new StoredDouble(this, initialValue);
	}

	public IStateBinaryTree makeBinaryTree(int inf, int sup) {
		return new StoredBinaryTree(this, inf, sup);
	}

	public IStateLong makeLong() {
		return makeLong(0);
	}

	public IStateLong makeLong(int init) {
		return new StoredLong(this,init);
	}

	public int getTrailSize() {
		int s = 0;
		for (ITrailStorage trail : trails) {
			s += trail.getSize();
		}
		return s;
	}

	public int getIntTrailSize() {
		return trails[INT_TRAIL].getSize();
	}

	public int getBoolTrailSize() {
		return trails[BOOL_TRAIL].getSize();
	}

	public int getIntVectorTrailSize() {
		return trails[INT_VECTOR_TRAIL].getSize();
	}

	public int getFloatTrailSize() {
		return trails[FLOAT_TRAIL].getSize();
	}

	public int getLongTrailSize() {
		return trails[LONG_TRAIL].getSize();
	}

	public int getVectorTrailSize() {
		return trails[VECTOR_TRAIL].getSize();
	}

	public int getBinaryTreeTrailSize() {
		return trails[BTREE_TRAIL].getSize();
	}

	private void resizeWorldCapacity(int newWorldCapacity) {
		for (ITrailStorage trail : trails) {
			trail.resizeWorldCapacity(newWorldCapacity);
		}
		maxWorld = newWorldCapacity;
	}




}

