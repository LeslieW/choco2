/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
	private final StoredLongVectorTrail longVectorTrail;
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
		longVectorTrail = new StoredLongVectorTrail(this,MaxHist,maxWorld);
		doubleVectorTrail = new StoredDoubleVectorTrail(this, MaxHist, maxWorld);
		doubleTrail = new StoredDoubleTrail(this, MaxHist, maxWorld);
		longTrail = new StoredLongTrail(this, MaxHist, maxWorld);
		btreeTrail = new StoredBinaryTreeTrail(this, MaxHist, maxWorld);
		trails = new ITrailStorage[]{
				boolTrail,intTrail,vectorTrail, intVectorTrail, longVectorTrail,
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
		longVectorTrail.worldPush();
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
		longVectorTrail.worldPop();
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
		longVectorTrail.worldCommit();
		doubleVectorTrail.worldCommit();
		doubleTrail.worldCommit();
		longTrail.worldCommit();
		btreeTrail.worldCommit();
		currentWorld--;
	}

    @Override
    public void clear() {
        boolTrail.clear();
		intTrail.clear();
		vectorTrail.clear();
		intVectorTrail.clear();
		longVectorTrail.clear();
		doubleVectorTrail.clear();
		doubleTrail.clear();
		longTrail.clear();
		btreeTrail.clear();
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
	public IStateLongVector makeLongVector() {
		return new StoredLongVector(this);
	}

	@Override
	public IStateLongVector makeLongVector(final int size, final long initialValue) {
		return new StoredLongVector(this, size, initialValue);
	}

	@Override
	public IStateLongVector makeLongVector(final long[] entries) {
		return new StoredLongVector(this, entries);
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
	public void savePreviousState(final StoredLongVector v, final int index, final long oldValue, final int oldStamp) {
		longVectorTrail.savePreviousState(v, index, oldValue, oldStamp);
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

