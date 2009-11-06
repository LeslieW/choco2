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
package choco.kernel.memory.trailing.trail;

import choco.kernel.memory.trailing.EnvironmentTrailing;
import choco.kernel.memory.trailing.StoredDouble;


/**
 * A backtrackable float variable trail storing past values 
 * of all the float variables.
 */
public class StoredDoubleTrail implements ITrailStorage {


	/**
	 * Reference towards the overall environment
	 * (responsible for all memory management).
	 */
	private final EnvironmentTrailing environment;

	/**
	 * Stack of backtrackable search variables.
	 */
	private StoredDouble[] variableStack;

	/**
	 * Stack of values (former values that need be restored upon backtracking).
	 */
	private double[] valueStack;

	/**
	 * Stack of timestamps indicating the world where the former value
	 * had been written.
	 */
	private int[] stampStack;

	/**
	 * Points the level of the last entry.
	 */
	private int currentLevel;

	/**
	 * A stack of pointers (for each start of a world).
	 */
	private int[] worldStartLevels;

	/**
	 * Capacity of the trailing stack (in terms of number of updates that
	 * can be stored).
	 */
	private int maxUpdates = 0;

	/**
	 * Constructs a trail with predefined size.
	 * @param env the environment responsible of managing worlds
	 * @param nUpdates maximal number of updates that will be stored
	 * @param nWorlds  maximal number of worlds that will be stored
	 */
	public StoredDoubleTrail(final EnvironmentTrailing env, final int nUpdates,
			final int nWorlds) {
		environment = env;
		currentLevel = 0;
		maxUpdates = nUpdates;
		variableStack = new StoredDouble[maxUpdates];
		valueStack = new double[maxUpdates];
		stampStack = new int[maxUpdates];
		worldStartLevels = new int[nWorlds];
	}

	/**
	 * Moving up to the next world.
	 */
	public void worldPush() {
		worldStartLevels[environment.getWorldIndex() + 1] = currentLevel;
	}

	/**
	 * Moving down to the previous world.
	 */
	public void worldPop() {
		while (currentLevel > worldStartLevels[environment.getWorldIndex()]) {
			currentLevel--;
			final StoredDouble v = variableStack[currentLevel];
			v._set(valueStack[currentLevel], stampStack[currentLevel]);
		}
	}

	/**
	 * Returns the current size of the stack.
	 * @return the size of the trail
	 */
	public int getSize() {
		return currentLevel;
	}

	/**
	 * Commits a world: merging it with the previous one.
	 */
	public void worldCommit() {
		// principle:
		//   currentLevel decreases to end of previous world
		//   updates of the committed world are scanned:
		//     if their stamp is the previous one (merged with the current one) 
		//      -> remove the update (garbage collecting this position for 
		//        the next update)
		//     otherwise update the worldStamp
		final int startLevel = worldStartLevels[environment.getWorldIndex()];
		final int prevWorld = environment.getWorldIndex() - 1;
		int writeIdx = startLevel;
		for (int level = startLevel; level < currentLevel; level++) {
			final StoredDouble var = variableStack[level];
			final double val = valueStack[level];
			final int stamp = stampStack[level];
			var.worldStamp = prevWorld;
			// update the stamp of the variable 
			// (current stamp refers to a world that no longer exists)
			if (stamp != prevWorld) {
				// shift the update if needed
				if (writeIdx != level) {
					valueStack[writeIdx] = val;
					variableStack[writeIdx] = var;
					stampStack[writeIdx] = stamp;
				}
				writeIdx++;
			}  
			// else:writeIdx is not incremented and the update will be discarded 
			// (since a good one is in prevWorld)
		}
		currentLevel = writeIdx;
	}


	/**
	 * Reacts when a StoredInt is modified: push the former value & timestamp
	 * on the stacks.
	 * @param v tha variable to store the value
	 * @param oldValue the previous value to store
	 * @param oldStamp the previous stamp value (to know when this old value
	 * will be updated again when backtracking)
	 */
	public void savePreviousState(final StoredDouble v, final double oldValue,
			final int oldStamp) {
		valueStack[currentLevel] = oldValue;
		variableStack[currentLevel] = v;
		stampStack[currentLevel] = oldStamp;
		currentLevel++;
		if (currentLevel == maxUpdates) {
			resizeUpdateCapacity();
		}
	}

	/**
	 * Resizes the data structure to manage more values.
	 */
	private void resizeUpdateCapacity() {
		final int newCapacity = ((maxUpdates * 3) / 2);
		// first, copy the stack of variables
		final StoredDouble[] tmp1 = new StoredDouble[newCapacity];
		System.arraycopy(variableStack, 0, tmp1, 0, variableStack.length);
		variableStack = tmp1;
		// then, copy the stack of former values
		final double[] tmp2 = new double[newCapacity];
		System.arraycopy(valueStack, 0, tmp2, 0, valueStack.length);
		valueStack = tmp2;
		// then, copy the stack of world stamps
		final int[] tmp3 = new int[newCapacity];
		System.arraycopy(stampStack, 0, tmp3, 0, stampStack.length);
		stampStack = tmp3;
		// last update the capacity
		maxUpdates = newCapacity;
	}

	/**
	 * Resizes the data structure to manage more values.
	 * @param newWorldCapacity the new capacity requested for world
	 * management
	 */
	public void resizeWorldCapacity(final int newWorldCapacity) {
		final int[] tmp = new int[newWorldCapacity];
		System.arraycopy(worldStartLevels, 0, tmp, 0, worldStartLevels.length);
		worldStartLevels = tmp;
	}
}
