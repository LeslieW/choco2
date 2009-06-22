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

import choco.kernel.memory.*;
import static choco.kernel.memory.copy.RecomputableElement.NB_TYPE;

import java.util.ArrayList;
import java.util.Stack;

public class EnvironmentCopying extends AbstractEnvironment {


	Stack<Integer> clonedWorldIdxStack;



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
        //noinspection unchecked
        elements = new ArrayList[NB_TYPE];
		for (int i = 0 ; i < NB_TYPE ; i++ ) elements[i] = new ArrayList<RecomputableElement>();

		clonedWorldIdxStack = new Stack<Integer>();
		save = new RcSave(this);

	}

    public int getNbCopy() {
		return nbCopy;
	}

	public void add(RecomputableElement rc) {
		elements[rc.getType()].add(rc);
		newEl = true;
	}

   @Override
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

    @Override
	public void worldPop() {
		save.restore(--currentWorld);
		clonedWorldIdxStack.pop();
	}

    @Override
	public void worldCommit() {
		//TODO
		throw (new UnsupportedOperationException());
	}

    @Override
	public IStateInt makeInt() {
		return new RcInt(this);
	}

    @Override
	public IStateInt makeInt(int initialValue) {
		return new RcInt(this,initialValue);
	}

    @Override
	public IStateInt makeIntProcedure(IStateIntProcedure procedure,
			int initialValue) {
		return new RcIntProcedure(this, procedure, initialValue);
	}

    @Override
	public IStateBool makeBool(boolean initialValue) {
		return new RcBool(this,initialValue);
	}

    @Override
	public IStateIntVector makeIntVector() {
		return new RcIntVector(this);
	}

    @Override
	public IStateIntVector makeIntVector(int size, int initialValue) {
		return new RcIntVector(this,size,initialValue);
	}

    @Override
	public IStateIntVector makeIntVector(int[] entries) {
		return new RcIntVector(this,entries);
	}

    @Override
	public IStateBitSet makeBitSet(int size) {
		return new RcBitSet(this,size);
	}

    @Override
	public IStateDouble makeFloat() {
		return new RcDouble(this);
	}

    @Override
	public IStateDouble makeFloat(double initialValue) {
		return new RcDouble(this, initialValue);
	}

    @Override
	public IStateLong makeLong() {
		return new RcLong(this);
	}

    @Override
	public IStateLong makeLong(int init) {
		return new RcLong(this, init);
	}

    @Override
	public <T> IStateVector<T> makeVector() {
		return new RcVector<T>(this);
	}

    @Override
	public IStateBinaryTree makeBinaryTree(int inf, int sup) {
        return null;
	}

    @Override
    public IStateObject makeObject(Object obj){
        return new RcObject(this, obj);
    }
}

