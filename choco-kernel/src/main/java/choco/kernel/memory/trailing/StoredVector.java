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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * Implements a backtrackable search vector.
 * </p>
 */
public final class StoredVector<E> implements choco.kernel.memory.IStateVector<E> {

	/**
	 * Contains the elements of the vector.
	 */
	private Object[] elementData;

	/**
	 * Contains time stamps for all entries (the world index of the last update for each entry)
	 */

	int[] worldStamps;

	/**
	 * A backtrackable search with the size of the vector.
	 */

	private StoredInt size;


	/**
	 * The current environment.
	 */

	private final EnvironmentTrailing environment;


	/**
	 * The history of all the backtrackable search vectors.
	 */

	private final StoredVectorTrail trail;


	/**
	 * Constructs a stored search vector with an initial size, and initial values.
	 *
	 * @param env The current environment.
	 */

	public StoredVector(EnvironmentTrailing env) {
		int initialCapacity = MIN_CAPACITY;
		int w = env.getWorldIndex();

		this.environment = env;
		this.elementData = new Object[initialCapacity];
		this.worldStamps = new int[initialCapacity];

		this.size = new StoredInt(env, 0);

		this.trail = (StoredVectorTrail) this.environment.getTrail(choco.kernel.memory.IEnvironment.VECTOR_TRAIL);
	}


	public StoredVector(int[] entries) {
		// TODO
		throw new UnsupportedOperationException();
	}


	public int size() {
		return size.get();
	}


	public boolean isEmpty() {
		return (size.get() == 0);
	}

	/*    public Object[] toArray() {
        // TODO : voir ci c'est utile
        return new Object[0];
    }*/


	public void ensureCapacity(int minCapacity) {
		int oldCapacity = elementData.length;
		if (minCapacity > oldCapacity) {
			Object[] oldData = elementData;
			int[] oldStamps = worldStamps;
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			elementData = new Object[newCapacity];
			worldStamps = new int[newCapacity];
			System.arraycopy(oldData, 0, elementData, 0, size.get());
			System.arraycopy(oldStamps, 0, worldStamps, 0, size.get());
		}
	}


	public boolean add(E i) {
		int newsize = size.get() + 1;
		ensureCapacity(newsize);
		size.set(newsize);
		elementData[newsize - 1] = i;
		worldStamps[newsize - 1] = environment.getWorldIndex();
		return true;
	}

	public void removeLast() {
		int newsize = size.get() - 1;
		if (newsize >= 0)
			size.set(newsize);
	}

	public E get(int index) {
		if (index < size.get() && index >= 0) {
			return (E)elementData[index];
		}
		throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size.get());
	}


	public E set(int index, E val) {
		if (index < size.get() && index >= 0) {
			assert(this.worldStamps[index] <= environment.getWorldIndex());
			E oldValue = (E) elementData[index];
			if (val != oldValue) {
				int oldStamp = this.worldStamps[index];
				// /!\  Logging statements really decrease performances
				//				if (LOGGER.isLoggable(Level.FINEST))
				//					LOGGER.log(Level.FINEST, "W: {0}@{1}ts:{2}", new Object[]{ environment.getWorldIndex(), index,this.worldStamps[index]});
				if (oldStamp < environment.getWorldIndex()) {
					trail.savePreviousState(this, index, oldValue, oldStamp);
					worldStamps[index] = environment.getWorldIndex();
				}
				elementData[index] = val;
			}
			return oldValue;
		}
		throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size.get());
	}


	/**
	 * Sets an element without storing the previous value.
	 */

	E _set(int index, Object val, int stamp) {
		E oldval = (E)elementData[index];
		elementData[index] = val;
		worldStamps[index] = stamp;
		return oldval;
	}
}
