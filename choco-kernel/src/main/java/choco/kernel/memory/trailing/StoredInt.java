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

import choco.kernel.memory.IStateInt;
import choco.kernel.memory.trailing.trail.StoredIntTrail;


/**
 * A class implementing backtrackable integers.
 */
public class StoredInt extends AbstractStoredObject implements IStateInt {

	private int currentValue;
	
	/**
	 * The current {@link StoredIntTrail}.
	 */
	private final StoredIntTrail trail;


	/**
	 * Constructs a stored search with an initial value.
	 * Note: this constructor should not be used directly: one should instead
	 * use the IEnvironment factory
	 */

	public StoredInt(EnvironmentTrailing env, int i) {
		super(env);
		currentValue = i;
		trail = (StoredIntTrail) this.environment.getTrail(choco.kernel.memory.IEnvironment.INT_TRAIL);
	}





	@Override
	public final void add(final int delta) {
		set( currentValue + delta);
		
	}

	@Override
	public final int get() {
		return currentValue;
	}





	/**
	 * Modifies the value and stores if needed the former value on the
	 * trailing stack.
	 */

	public final void set(final int y) {
		if (y != currentValue) {
			if (this.worldStamp < environment.getWorldIndex()) {
				trail.savePreviousState(this, currentValue, worldStamp);
				worldStamp = environment.getWorldIndex();
			}
			currentValue = y;
		}
	}



	/**
	 * Modifies the value without storing the former value on the trailing stack.
	 *
	 * @param y      the new value
	 * @param wstamp the stamp of the world in which the update is performed
	 */

    public void _set(final int y, final int wstamp) {
		currentValue = y;
		worldStamp = wstamp;
	}


	@Override
	public final String toString() {
		return String.valueOf(currentValue);
	}
}

