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
import choco.kernel.memory.trailing.trail.*;

/**
 * The root class for managing memory and sessions.
 * <p/>
 * A environment is associated to each problem.
 * It is responsible for managing backtrackable data.
 */
public class EnvironmentTrailing extends AbstractEnvironment {


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
        int maxHist = 5000;
		maxWorld = 100; //1000;
		trails = new ITrailStorage[]{
				new StoredBoolTrail(this, maxHist, maxWorld),
				new StoredIntTrail(this, maxHist, maxWorld),
				new StoredVectorTrail(this, maxHist, maxWorld),
				new StoredIntVectorTrail(this, maxHist, maxWorld),
				new StoredDoubleTrail(this, maxHist, maxWorld),
				new StoredLongTrail(this, maxHist, maxWorld),
				new StoredBinaryTreeTrail(this, maxHist,maxWorld),
		};
	}

	/**
	 * Returns the <code>i</code>th trail in the trail array.
	 *
	 * @param i index of the trail.
     * @return the trail
	 */

	public ITrailStorage getTrail(int i) {
		return trails[i];
	}


    @Override
	public void worldPush() {
		for (ITrailStorage trail : trails) {
			trail.worldPush();
		}
		currentWorld++;
		if (currentWorld + 1 == maxWorld) {
			resizeWorldCapacity(maxWorld * 3 / 2);
		}
	}


    @Override
	public void worldPop() {
		for (ITrailStorage trail : trails) {
			trail.worldPop();
		}
		currentWorld--;
	}

    @Override
	public void worldCommit() {
		if (currentWorld == 0) {
			throw new IllegalStateException("Commit in world 0?");
		}
		for (ITrailStorage trail : trails) {
			trail.worldCommit();
		}
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

