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

import choco.kernel.memory.IStateLong;
import choco.kernel.memory.trailing.trail.StoredLongTrail;

public final class StoredLong extends AbstractStoredObject implements IStateLong {


	private long currentValue;


	/**
	 * Constructs a stored search with an unknown initial value.
	 * Note: this constructor should not be used directly: one should instead
	 * use the IEnvironment factory
	 */

	public StoredLong(EnvironmentTrailing env) {
		this(env, 0);
	}


	/**
	 * Constructs a stored search with an initial value.
	 * Note: this constructor should not be used directly: one should instead
	 * use the IEnvironment factory
	 */

	public StoredLong(EnvironmentTrailing env, long d) {
		super(env);
		currentValue = d;
	}


	@Override
	public long get() {
		return currentValue;
	}



	public void set(final long y) {
		if (y != currentValue) {
			if (this.worldStamp < environment.getWorldIndex()) {
				environment.savePreviousState(this, currentValue, worldStamp);
				worldStamp = environment.getWorldIndex();
			}
			currentValue = y;
		}
	}

	public void add(final long delta) {
		set(currentValue + delta);
	}

	/**
	 * Modifies the value without storing the former value on the trailing stack.
	 *
	 * @param y      the new value
	 * @param wstamp the stamp of the world in which the update is performed
	 */

    public void _set(final long y, final int wstamp) {
		currentValue = y;
		worldStamp = wstamp;
	}


	@Override
	public String toString() {
		return String.valueOf(currentValue);
	}

	
	
}
