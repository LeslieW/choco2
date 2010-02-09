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


import java.util.Arrays;

import choco.kernel.memory.AbstractEnvironment;
import choco.kernel.memory.IStateBinaryTree;
import choco.kernel.memory.IStateBool;
import choco.kernel.memory.IStateDouble;
import choco.kernel.memory.IStateDoubleVector;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.IStateIntProcedure;
import choco.kernel.memory.IStateIntVector;
import choco.kernel.memory.IStateLong;
import choco.kernel.memory.IStateObject;
import choco.kernel.memory.IStateVector;
import choco.kernel.memory.IStateBinaryTree.Node;
import choco.kernel.memory.trailing.trail.ITrailStorage;
import choco.kernel.memory.trailing.trail.StoredBinaryTreeTrail;
import choco.kernel.memory.trailing.trail.StoredBoolTrail;
import choco.kernel.memory.trailing.trail.StoredDoubleTrail;
import choco.kernel.memory.trailing.trail.StoredDoubleVectorTrail;
import choco.kernel.memory.trailing.trail.StoredIntTrail;
import choco.kernel.memory.trailing.trail.StoredIntVectorTrail;
import choco.kernel.memory.trailing.trail.StoredLongTrail;
import choco.kernel.memory.trailing.trail.StoredVectorTrail;

/**
 * The root class for managing memory and sessions.
 * <p/>
 * A environment is associated to each problem.
 * It is responsible for managing backtrackable data.
 */
public final class EnvironmentTrailing extends AbstractEnvironment {


	/**
	 * The maximum numbers of worlds that a
	 * {@link ITrailStorage} can handle.
	 */

	private int maxWorld;
	
	//Contains all the {@link ITrailStorage} trails for
	// storing different kinds of data.
	//private -> garbage collector can quickly delete trail from the heap after freeMemory();
	private StoredIntTrail intTrail;
	private StoredBoolTrail boolTrail;
	private StoredVectorTrail vectorTrail;
	private StoredIntVectorTrail intVectorTrail;
	private StoredDoubleVectorTrail doubleVectorTrail;
	private StoredDoubleTrail doubleTrail;
	private StoredLongTrail longTrail;
	private StoredBinaryTreeTrail btreeTrail;
	
	/**
	 * Contains all the {@link ITrailStorage} trails for
	 * storing different kinds of data.
	 */
	private final ITrailStorage[] trails;

	/**
	 * Constructs a new <code>IEnvironment</code> with
	 * the default stack sizes : 50000 and 1000.
	 */

	public EnvironmentTrailing() {
		int maxHist = 5000;
		maxWorld = 100; //1000;
		boolTrail = new StoredBoolTrail(this, maxHist, maxWorld); 
		intTrail = new StoredIntTrail(this, maxHist, maxWorld);
		vectorTrail = new StoredVectorTrail(this, maxHist, maxWorld);
		intVectorTrail = new StoredIntVectorTrail(this, maxHist, maxWorld);
		doubleVectorTrail = new StoredDoubleVectorTrail(this,maxHist,maxWorld);
		doubleTrail = new StoredDoubleTrail(this, maxHist, maxWorld);
		longTrail = new StoredLongTrail(this, maxHist, maxWorld);
		btreeTrail = new StoredBinaryTreeTrail(this, maxHist,maxWorld);
		trails = new ITrailStorage[]{
				boolTrail,intTrail,vectorTrail, intVectorTrail,
				doubleVectorTrail, doubleTrail,longTrail,btreeTrail
		};
	
	}


	@Override
	public void freeMemory() {
		Arrays.fill(trails, null);
		intTrail = null;
		boolTrail = null;
		vectorTrail = null;
		intVectorTrail = null;
		doubleVectorTrail = null;
		doubleTrail = null;
		longTrail = null;
		btreeTrail = null;
	}




	@Override
	public void worldPush() {
		//code optim.: replace loop by enumeration
		intTrail.worldPush();
		boolTrail.worldPush();
		vectorTrail.worldPush();
		intVectorTrail.worldPush();
		doubleVectorTrail.worldPush();
		doubleTrail.worldPush();
		longTrail.worldPush();
		btreeTrail.worldPush();
		currentWorld++;
		if (currentWorld + 1 == maxWorld) {
			resizeWorldCapacity(maxWorld * 3 / 2);
		}
	}


	@Override
	public void worldPop() {
		//code optim.: replace loop by enumeration
		intTrail.worldPop();
		boolTrail.worldPop();
		vectorTrail.worldPop();
		intVectorTrail.worldPop();
		doubleVectorTrail.worldPop();
		doubleTrail.worldPop();
		longTrail.worldPop();
		btreeTrail.worldPop();
		currentWorld--;
	}

	@Override
	public void worldCommit() {
		//code optim.: replace loop by enumeration;
		if (currentWorld == 0) {
			throw new IllegalStateException("Commit in world 0?");
		}
		intTrail.worldCommit();
		boolTrail.worldCommit();
		vectorTrail.worldCommit();
		intVectorTrail.worldCommit();
		doubleVectorTrail.worldCommit();
		doubleTrail.worldCommit();
		longTrail.worldCommit();
		btreeTrail.worldCommit();
		currentWorld--;
	}

	@Override
	public IStateInt makeInt() {
		return makeInt(0);
	}

	@Override
	public IStateInt makeInt(int initialValue) {
		return new StoredInt(this, initialValue);
	}

	@Override
	public IStateInt makeIntProcedure(IStateIntProcedure procedure,
			int initialValue) {
		return new StoredIntProcedure(this, procedure, initialValue);
	}

	@Override
	public IStateBool makeBool(boolean initialValue) {
		return new StoredBool(this, initialValue);
	}

	@Override
	public IStateIntVector makeIntVector() {
		return new StoredIntVector(this);
	}

	@Override
	public IStateIntVector makeIntVector(int size, int initialValue) {
		return new StoredIntVector(this, size, initialValue);
	}

	@Override
	public IStateIntVector makeIntVector(int[] entries) {
		return new StoredIntVector(this, entries);
	}

	@Override
    public IStateDoubleVector makeDoubleVector() {
        return new StoredDoubleVector(this);
    }

    @Override
    public IStateDoubleVector makeDoubleVector(int size, double initialValue) {
        return new StoredDoubleVector(this, size, initialValue);
    }

    @Override
    public IStateDoubleVector makeDoubleVector(double[] entries) {
        return new StoredDoubleVector(this, entries);
    }

    @Override
	public <T> IStateVector<T> makeVector() {
		return new StoredVector<T>(this);
	}

	//    @Override
	//	public AbstractStateBitSet makeBitSet(int size) {
	//		return new StoredBitSet(this, size);
	//	}

	@Override
	public IStateDouble makeFloat() {
		return makeFloat(Double.NaN);
	}

	@Override
	public IStateDouble makeFloat(double initialValue) {
		return new StoredDouble(this, initialValue);
	}

	@Override
	public IStateBinaryTree makeBinaryTree(int inf, int sup) {
		return new StoredBinaryTree(this, inf, sup);
	}

	@Override
	public IStateLong makeLong() {
		return makeLong(0);
	}

	@Override
	public IStateLong makeLong(int init) {
		return new StoredLong(this,init);
	}

	@Override
	public IStateObject makeObject(Object obj) {
		throw (new UnsupportedOperationException());
	}

	public int getTrailSize() {
		int s = 0;
		for (ITrailStorage trail : trails) {
			s += trail.getSize();
		}
		return s;
	}

	public int getIntTrailSize() {
		return intTrail.getSize();
	}

	public int getBoolTrailSize() {
		return boolTrail.getSize();
	}

	public int getIntVectorTrailSize() {
		return intVectorTrail.getSize();
	}
    public int getDoubleVectorTrailSize() {
        return doubleVectorTrail.getSize();
    }

	public int getFloatTrailSize() {
		return doubleTrail.getSize();
	}

	public int getLongTrailSize() {
		return longTrail.getSize();
	}

	public int getVectorTrailSize() {
		return vectorTrail.getSize();
	}

	public int getBinaryTreeTrailSize() {
		return btreeTrail.getSize();
	}

	private void resizeWorldCapacity(int newWorldCapacity) {
		for (ITrailStorage trail : trails) {
			trail.resizeWorldCapacity(newWorldCapacity);
		}
		maxWorld = newWorldCapacity;
	}

	/**
	 * Reacts when a StoredInt is modified: push the former value & timestamp
	 * on the stacks.
	 */
	public void savePreviousState(StoredInt v, int oldValue, int oldStamp) {
		intTrail.savePreviousState(v, oldValue, oldStamp);
	}

	/**
	 * Reacts when a StoredDouble is modified: push the former value & timestamp
	 * on the stacks.
	 */
	public void savePreviousState(StoredDouble v, double oldValue, int oldStamp) {
		doubleTrail.savePreviousState(v, oldValue, oldStamp);
	}

	/**
	 * Reacts when a StoredDouble is modified: push the former value & timestamp
	 * on the stacks.
	 */
	public void savePreviousState(StoredLong v, long oldValue, int oldStamp) {
		longTrail.savePreviousState(v, oldValue, oldStamp);
	}

	/**
	 * Reacts when a StoredBool is modified: push the former value & timestamp
	 * on the stacks.
	 */
	public void savePreviousState(StoredBool v, boolean oldValue, int oldStamp) {
		boolTrail.savePreviousState(v, oldValue, oldStamp);
	}
	/**
	 * Reacts when a StoredVector is modified: push the former value & timestamp
	 * on the stacks.
	 */
	public void savePreviousState(StoredIntVector v,int index,int oldValue,int oldStamp) {
		intVectorTrail.savePreviousState(v, index, oldValue, oldStamp);
	}

	public <E> void savePreviousState(StoredVector<E> v,int index,E oldValue,int oldStamp) {
		vectorTrail.savePreviousState(v, index, oldValue, oldStamp);
	}

	public <E> void savePreviousState(IStateBinaryTree v,Node n,int op) {
		btreeTrail.stack(v, n,op);
	}


	public void savePreviousState(StoredDoubleVector v,
			int index, double oldValue, int oldStamp) {
		doubleVectorTrail.savePreviousState(v, index, oldValue, oldStamp);
		
	}
}

