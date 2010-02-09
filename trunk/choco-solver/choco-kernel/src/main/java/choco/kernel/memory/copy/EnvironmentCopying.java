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

import java.util.Stack;

public class EnvironmentCopying extends AbstractEnvironment {


    /**
	 * The current world number (should be less
	 * than <code>maxWorld</code>).
	 */

	private boolean newEl = false;

    protected final static Stack<Integer> clonedWorldIdxStack;
    public static RecomputableElement[][] elements;
    public static int[] indices;
	private static RcSave save;

	public int nbCopy = 0 ;


    static{
        elements = new RecomputableElement[NB_TYPE][64];
        indices = new int[NB_TYPE];
        clonedWorldIdxStack = new Stack<Integer>();
    }


	public EnvironmentCopying() {
        for(int i = NB_TYPE; --i>=0;)indices[i] = 0;
        clonedWorldIdxStack.clear();
        save = new RcSave(this);
	}
	
	

    @Override
	public void freeMemory() {
		//do nothing (static storage)		
	}



	public int getNbCopy() {
		return nbCopy;
	}

	public void add(RecomputableElement rc) {
        ensureCapacity(rc.getType(), indices[rc.getType()]+1);
		elements[rc.getType()][indices[rc.getType()]++] = rc;
		newEl = true;
	}

    private void ensureCapacity(int type, int n) {
        if (n > elements[type].length) {
          int newSize = elements[type].length;
          while (n >= newSize) {
              newSize = (3 * newSize) / 2;
          }
          RecomputableElement[] newStaticObjects = new RecomputableElement[newSize];
          System.arraycopy(elements[type], 0, newStaticObjects, 0, elements[type].length);
          elements[type] = newStaticObjects;
      }
    }

    @Override
	public void worldPush() {
		if (newEl) {
			save.currentElement = new RecomputableElement[NB_TYPE][];
			for (int i = NB_TYPE ; --i>=0;) {
				save.currentElement[i] = new RecomputableElement[indices[i]];
                System.arraycopy(elements[i], 0, save.currentElement[i], 0, indices[i]);
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
    public IStateDoubleVector makeDoubleVector() {
        return new RcDoubleVector(this);
    }

    @Override
    public IStateDoubleVector makeDoubleVector(int size, double initialValue) {
        return new RcDoubleVector(this,size,initialValue);
    }

    @Override
    public IStateDoubleVector makeDoubleVector(double[] entries) {
        return new RcDoubleVector(this,entries);
    }

//    @Override
//	public IStateBitSet makeBitSet(int size) {
//		return new RcBitSet(this,size);
//	}

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

