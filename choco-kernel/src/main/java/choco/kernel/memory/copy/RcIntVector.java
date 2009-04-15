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

import choco.kernel.common.util.DisposableIntIterator;
import choco.kernel.memory.IStateIntVector;

import java.util.logging.Logger;

/* 
 * Created by IntelliJ IDEA.
 * User: Julien
 * Date: 29 mars 2007
 * Since : Choco 2.0.0
 *
 */
public class RcIntVector implements IStateIntVector, RecomputableElement {

    /**
     * Minimal capacity of a vector
     */
    public static final int MIN_CAPACITY = 8;

    /**
     * Contains the elements of the vector.
     */

    private int[] elementData;

    /**
     * A backtrackable search with the size of the vector.
     */

    private RcInt size;


    /**
     * The current environment.
     */

    private final EnvironmentCopying environment;

    private int timeStamp;


    /**
     * Constructs a stored search vector with an initial size, and initial values.
     *
     * @param env          The current environment.
     * @param initialSize  The initial size.
     * @param initialValue The initial common value.
     */

    public RcIntVector(EnvironmentCopying env, int initialSize, int initialValue) {
        int initialCapacity = MIN_CAPACITY;
        int w = env.getWorldIndex();

        if (initialCapacity < initialSize)
            initialCapacity = initialSize;

        this.environment = env;
        timeStamp = environment.getWorldIndex();
        this.elementData = new int[initialCapacity];
        for (int i = 0; i < initialSize; i++) {
            this.elementData[i] = initialValue;
        }
        this.size = new RcInt(env, initialSize);
        env.add(this);
    }


    public RcIntVector(EnvironmentCopying env, int[] entries) {
        int initialCapacity = MIN_CAPACITY;
        int w = env.getWorldIndex();
        int initialSize = entries.length;

        if (initialCapacity < initialSize)
            initialCapacity = initialSize;

        this.environment = env;
        this.elementData = new int[initialCapacity];
        for (int i = 0; i < initialSize; i++) {
            this.elementData[i] = entries[i]; // could be a System.arrayCopy but since the loop is needed...
        }
        this.size = new RcInt(env, initialSize);
        env.add(this);
        timeStamp = environment.getWorldIndex();
    }

    /**
     * Constructs an empty stored search vector.
     *
     * @param env The current environment.
     */

    public RcIntVector(EnvironmentCopying env) {
        this(env, 0, 0);
    }


    /**
     * Returns the current size of the stored search vector.
     */

    public int size() {
        return size.get();
    }


    /**
     * Checks if the vector is empty.
     */

    public boolean isEmpty() {
        return (size.get() == 0);
    }

/*    public Object[] toArray() {
        // TODO : voir ci c'est utile
        return new Object[0];
    }*/


    /**
     * Checks if the capacity is great enough, else the capacity
     * is extended.
     *
     * @param minCapacity the necessary capacity.
     */

    public void ensureCapacity(int minCapacity) {
        int oldCapacity = elementData.length;
        if (minCapacity > oldCapacity) {
            int[] oldData = elementData;
            int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity)
                newCapacity = minCapacity;
            elementData = new int[newCapacity];
            System.arraycopy(oldData, 0, elementData, 0, size.get());
        }
    }


    /**
     * Adds a new search at the end of the vector.
     *
     * @param i The search to add.
     */

    public void add(int i) {
        timeStamp = environment.getWorldIndex();
        int newsize = size.get() + 1;
        ensureCapacity(newsize);
        size.set(newsize);
        elementData[newsize - 1] = i;
    }

    /**
     * removes the search at the end of the vector.
     * does nothing when called on an empty vector
     */

    public void removeLast() {
        timeStamp = environment.getWorldIndex();
        int newsize = size.get() - 1;
        if (newsize >= 0)
            size.set(newsize);
    }

    /**
     * Returns the <code>index</code>th element of the vector.
     */

    public int get(int index) {
        if (index < size.get() && index >= 0) {
            return elementData[index];
        }
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size.get());
    }


    /**
     * Assigns a new value <code>val</code> to the element <code>index</code>.
     */

    public int set(int index, int val) {
        if (index < size.get() && index >= 0) {
            //<hca> je vire cet assert en cas de postCut il n est pas vrai ok ?
            //assert(this.worldStamps[index] <= environment.getWorldIndex());
            int oldValue = elementData[index];
            if (val != oldValue) {
                elementData[index] = val;
            }
            timeStamp = environment.getWorldIndex();
            return oldValue;
        }
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size.get());
    }

    public int[] toArray(int[] tab) {
        return new int[0];
    }

    public void remove(int i) {

    }

    public void _set(int[] vals) {
        timeStamp = environment.getWorldIndex();
        System.arraycopy(vals,0,elementData,0,vals.length);
    }

    public int[] deepCopy() {
        int[] ret = new int[size.get()];
        System.arraycopy(elementData,0,ret,0,size.get());
        return ret;
    }

    public int getType() {
        return INTVECTOR;
    }

    public int getTimeStamp() {
        return timeStamp;
    }
    
	@Override
	public DisposableIntIterator getIterator() {
		throw new UnsupportedOperationException("not yet implemented");
	}

}
