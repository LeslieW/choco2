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

import choco.kernel.memory.IStateDouble;


/**
 * A class implementing a backtrackable float variable.
 */
public final class StoredDouble extends AbstractStoredObject implements IStateDouble {



	/**
	 * Current value of the search.
	 */

	private double currentValue;



	/**
	 * The current {@link StoredIntTrail}.
	 */
	private final StoredDoubleTrail trail;




	/**
	 * Constructs a stored search with an initial value.
	 * Note: this constructor should not be used directly: one should instead
	 * use the IEnvironment factory
	 */

	public StoredDouble(EnvironmentTrailing env, double d) {
		super(env);
		currentValue = d;
		worldStamp = env.getWorldIndex();
		trail = (StoredDoubleTrail) this.environment.getTrail(choco.kernel.memory.IEnvironment.FLOAT_TRAIL);
	}


	public double get() {
		return currentValue;
	}



	public void set(final double y) {
		if (y != currentValue) {
			if (this.worldStamp < environment.getWorldIndex()) {
				trail.savePreviousState(this, currentValue, worldStamp);
				worldStamp = environment.getWorldIndex();
			}
			currentValue = y;
		}
	}

	public void add(final double delta) {
		set(get() + delta);
	}

	/**
	 * Modifies the value without storing the former value on the trailing stack.
	 *
	 * @param y      the new value
	 * @param wstamp the stamp of the world in which the update is performed
	 */

	void _set(final double y, final int wstamp) {
		currentValue = y;
		worldStamp = wstamp;
	}


	@Override
	public String toString() {
		return String.valueOf(currentValue);
	}



}
