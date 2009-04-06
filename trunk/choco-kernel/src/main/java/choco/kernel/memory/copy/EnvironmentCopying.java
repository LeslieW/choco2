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
package choco.kernel.memory.copy;
/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|       Choco-Solver.net    *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco-solver.net        *
 *     + support : support@chocosolver.net        *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                    N. Jussien   1999-2008      *
 **************************************************/

import choco.kernel.memory.*;
import choco.kernel.memory.trailing.IndexedObject;
import choco.kernel.memory.trailing.StoredIndexedBipartiteSet;

import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Logger;

public class EnvironmentCopying extends AbstractEnvironment {

	Logger logger = Logger.getLogger("environment");

	Stack<Integer> clonedWorldIdxStack;


	public static int BOOL = 0;
	public static int INT = 1;
	public static int VECTOR = 2;
	public static int INTVECTOR = 3;
	public static int BITSET = 4;

	public static int NB_TYPE = 5;



	/**
	 * The current world number (should be less
	 * than <code>maxWorld</code>).
	 */


	private boolean newEl = false;
	public ArrayList<RecomputableElement>[] elements;
	public RecomputableElement[][] test;
	private RcSave save;

	public int nbCopy = 0 ;



	public EnvironmentCopying() {
		elements = new ArrayList[NB_TYPE];
		for (int i = 0 ; i < NB_TYPE ; i++ ) elements[i] = new ArrayList<RecomputableElement>();

		clonedWorldIdxStack = new Stack<Integer>();
		save = new RcSave(this);

	}

	public void worldPush() {

		if (newEl) {
			save.currentElement = new RecomputableElement[elements.length][];

			for (int i = 0 ; i < elements.length ; i++) {
				save.currentElement[i] = elements[i].toArray(new RecomputableElement[elements[i].size()]);
			}

			newEl = false;
		}


		this.saveEnv();
		currentWorld++;

	}

	private void saveEnv() {

		if (!(currentWorld != 0 && currentWorld == clonedWorldIdxStack.peek())) {

			nbCopy++;

			if (clonedWorldIdxStack.empty())
				clonedWorldIdxStack.push(currentWorld);
			else if (clonedWorldIdxStack.peek() < currentWorld)
				clonedWorldIdxStack.push(currentWorld);

			save.save(currentWorld);
		}
	}

	public void worldPop() {
		save.restore(--currentWorld);
		clonedWorldIdxStack.pop();
	}

	public void worldCommit() {
		//TODO
		throw (new UnsupportedOperationException());
	}

	public IStateInt makeInt() {
		return new RcInt(this);
	}

	public IStateInt makeInt(int initialValue) {
		return new RcInt(this,initialValue);
	}

	@Override
	public IStateInt makeIntProcedure(IStateIntProcedure procedure,
			int initialValue) {
		return null;
	}

	public IStateBool makeBool(boolean initialValue) {
		return new RcBool(this,initialValue);
	}

	public IStateIntVector makeIntVector() {
		return new RcIntVector(this);
	}

	public IStateIntVector makeIntVector(int size, int initialValue) {
		return new RcIntVector(this,size,initialValue);
	}

	public IStateIntVector makeIntVector(int[] entries) {
		return new RcIntVector(this,entries);
	}

	public <T> PartiallyStoredVector<T> makePartiallyStoredVector() {
		return new PartiallyStoredVector<T>(this);
	}

	public PartiallyStoredIntVector makePartiallyStoredIntVector() {
		return new PartiallyStoredIntVector(this);
	}

	public AbstractStateBitSet makeBitSet(int size) {
		return new RcBitSet(this,size);
	}

	public AbstractStateBitSet makeBitSet(int size, boolean fixed) {
		return new RcBitSet(this,size,fixed);
	}

	public AbstractStateBitSet makeBitSet(int[] entries) {
		return new RcBitSet(this,0);
	}

	@Override
	public IStateIntVector makeBipartiteIntList(int[] entries) {
		return null;
	}

	public IStateIntVector makeBipartiteSet(int[] entries) {
		logger.warning("using trailing data structure inside copying environnement...");
		return new StoredIndexedBipartiteSet(this,entries);
	}

	public IStateIntVector makeBipartiteSet(int nbEntries) {
		logger.warning("using trailing data structure inside copying environnement...");
		return new StoredIndexedBipartiteSet(this,nbEntries);
	}

	public IStateIntVector makeBipartiteSet(IndexedObject[] entries) {
		logger.warning("using trailing data structure inside copying environnement...");
		return new StoredIndexedBipartiteSet(this,entries);
	}

	public IStateIntVector makeBipartiteSet(ArrayList<IndexedObject> entries) {
		logger.warning("using trailing data structure inside copying environnement...");
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
	}

	public IStateDouble makeFloat() {
		return null;
	}

	public IStateDouble makeFloat(double initialValue) {
		return null;
	}

	public IStateLong makeLong() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public IStateLong makeLong(int init) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public <T> IStateVector<T> makeVector() {
		return new RcVector<T>(this);
	}

	public int getNbCopy() {
		return nbCopy;
	}

	public void add(RecomputableElement rc) {
		elements[rc.getType()].add(rc);
		newEl = true;
	}

	public IStateIntInterval makeIntInterval(int initialInf, int initialSup) {
		return new RCIntInterval(this, initialInf, initialSup);
	}

	public IStateBinaryTree makeBinaryTree(int inf, int sup) {
		return null;
	}
}
