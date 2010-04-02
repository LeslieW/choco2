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


import choco.kernel.memory.*;
import choco.kernel.memory.IStateBinaryTree.Node;
import choco.kernel.memory.trailing.trail.*;

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
    private int maxWorld = 100; //1000;

    /**
	 * The maximum numbers of updates that a
	 * {@link ITrailStorage} can handle.
	 */
    private static final int MaxHist = 5000;

    //Contains all the {@link ITrailStorage} trails for
	// storing different kinds of data.
	private final StoredIntTrail intTrail;
    private final StoredBoolTrail boolTrail;
    private final StoredVectorTrail vectorTrail;
    private final StoredIntVectorTrail intVectorTrail;
    private final StoredDoubleVectorTrail doubleVectorTrail;
    private final StoredDoubleTrail doubleTrail;

    private final StoredLongTrail longTrail;

    private final StoredBinaryTreeTrail btreeTrail;
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
        boolTrail = new StoredBoolTrail(this, MaxHist, maxWorld);
		intTrail = new StoredIntTrail(this, MaxHist, maxWorld);
		vectorTrail = new StoredVectorTrail(this, MaxHist, maxWorld);
		intVectorTrail = new StoredIntVectorTrail(this, MaxHist, maxWorld);
		doubleVectorTrail = new StoredDoubleVectorTrail(this, MaxHist, maxWorld);
		doubleTrail = new StoredDoubleTrail(this, MaxHist, maxWorld);
		longTrail = new StoredLongTrail(this, MaxHist, maxWorld);
		btreeTrail = new StoredBinaryTreeTrail(this, MaxHist, maxWorld);
		trails = new ITrailStorage[]{
				boolTrail,intTrail,vectorTrail, intVectorTrail,
				doubleVectorTrail, doubleTrail,longTrail,btreeTrail
		};
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
        timestamp++;
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
        timestamp++;
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
        timestamp++;
	}

	@Override
	public IStateInt makeInt() {
		return makeInt(0);
	}

	@Override
	public IStateInt makeInt(final int initialValue) {
		return new StoredInt(this, initialValue);
	}

	@Override
	public IStateInt makeIntProcedure(final IStateIntProcedure procedure,
			final int initialValue) {
		return new StoredIntProcedure(this, procedure, initialValue);
	}

	@Override
	public IStateBool makeBool(final boolean initialValue) {
		return new StoredBool(this, initialValue);
	}

	@Override
	public IStateIntVector makeIntVector() {
		return new StoredIntVector(this);
	}

	@Override
	public IStateIntVector makeIntVector(final int size, final int initialValue) {
		return new StoredIntVector(this, size, initialValue);
	}

	@Override
	public IStateIntVector makeIntVector(final int[] entries) {
		return new StoredIntVector(this, entries);
	}

	@Override
    public IStateDoubleVector makeDoubleVector() {
        return new StoredDoubleVector(this);
    }

    @Override
    public IStateDoubleVector makeDoubleVector(final int size, final double initialValue) {
        return new StoredDoubleVector(this, size, initialValue);
    }

    @Override
    public IStateDoubleVector makeDoubleVector(final double[] entries) {
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
	public IStateDouble makeFloat(final double initialValue) {
		return new StoredDouble(this, initialValue);
	}

	@Override
	public IStateBinaryTree makeBinaryTree(final int inf, final int sup) {
		return new StoredBinaryTree(this, inf, sup);
	}

	@Override
	public IStateLong makeLong() {
		return makeLong(0);
	}

	@Override
	public IStateLong makeLong(final int init) {
		return new StoredLong(this,init);
	}

	@Override
	public IStateObject makeObject(final Object obj) {
		throw (new UnsupportedOperationException());
	}

	public int getTrailSize() {
		int s = 0;
		for (final ITrailStorage trail : trails) {
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

	private void resizeWorldCapacity(final int newWorldCapacity) {
		for (final ITrailStorage trail : trails) {
			trail.resizeWorldCapacity(newWorldCapacity);
		}
		maxWorld = newWorldCapacity;
	}

	/**
	 * Reacts when a StoredInt is modified: push the former value & timestamp
	 * on the stacks.
	 */
	public void savePreviousState(final StoredInt v, final int oldValue, final int oldStamp) {
		intTrail.savePreviousState(v, oldValue, oldStamp);
	}

	/**
	 * Reacts when a StoredDouble is modified: push the former value & timestamp
	 * on the stacks.
	 */
	public void savePreviousState(final StoredDouble v, final double oldValue, final int oldStamp) {
		doubleTrail.savePreviousState(v, oldValue, oldStamp);
	}

	/**
	 * Reacts when a StoredDouble is modified: push the former value & timestamp
	 * on the stacks.
	 */
	public void savePreviousState(final StoredLong v, final long oldValue, final int oldStamp) {
		longTrail.savePreviousState(v, oldValue, oldStamp);
	}

	/**
	 * Reacts when a StoredBool is modified: push the former value & timestamp
	 * on the stacks.
	 */
	public void savePreviousState(final StoredBool v, final boolean oldValue, final int oldStamp) {
		boolTrail.savePreviousState(v, oldValue, oldStamp);
	}
	/**
	 * Reacts when a StoredVector is modified: push the former value & timestamp
	 * on the stacks.
	 */
	public void savePreviousState(final StoredIntVector v, final int index, final int oldValue, final int oldStamp) {
		intVectorTrail.savePreviousState(v, index, oldValue, oldStamp);
	}

	public <E> void savePreviousState(final StoredVector<E> v, final int index, final E oldValue, final int oldStamp) {
		vectorTrail.savePreviousState(v, index, oldValue, oldStamp);
	}

	public <E> void savePreviousState(final IStateBinaryTree v, final Node n, final int op) {
		btreeTrail.stack(v, n,op);
	}


	public void savePreviousState(final StoredDoubleVector v,
			final int index, final double oldValue, final int oldStamp) {
		doubleVectorTrail.savePreviousState(v, index, oldValue, oldStamp);
		
	}
}

