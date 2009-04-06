// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.mem.trailing;

import i_want_to_use_this_old_version_of_choco.mem.ITrailStorage;

import java.util.logging.Logger;

/**
 * A backtrackable float variable trail storing past values 
 * of all the float variables.
 */
public class StoredFloatTrail implements ITrailStorage {
  /**
   * Reference to an object for logging trace statements related 
   * memory & backtrack (using the java.util.logging package).
   */
  private static Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.mem");

  /**
   * Reference towards the overall environment
   * (responsible for all memory management).
   */
  private EnvironmentTrailing environment;

  /**
   * Stack of backtrackable search variables.
   */
  private StoredFloat[] variableStack;

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
   * Capacity of the trailing stack (in terms of number of worlds that 
   * can be handled).
   */
  private int maxWorlds = 0;

  /**
   * Constructs a trail with predefined size.
   * @param env the environment responsible of managing worlds
   * @param nUpdates maximal number of updates that will be stored
   * @param nWorlds  maximal number of worlds that will be stored
   */
  public StoredFloatTrail(final EnvironmentTrailing env, final int nUpdates,
      final int nWorlds) {
    environment = env;
    currentLevel = 0;
    maxUpdates = nUpdates;
    maxWorlds = nWorlds;
    variableStack = new StoredFloat[nUpdates];
    valueStack = new double[nUpdates];
    stampStack = new int[nUpdates];
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
      StoredFloat v = variableStack[currentLevel];
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
    int startLevel = worldStartLevels[environment.getWorldIndex()];
    int prevWorld = environment.getWorldIndex() - 1;
    int writeIdx = startLevel;
    for (int level = startLevel; level < currentLevel; level++) {
      StoredFloat var = variableStack[level];
      double val = valueStack[level];
      int stamp = stampStack[level];
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
  public void savePreviousState(final StoredFloat v, final double oldValue,
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
    int newCapacity = ((maxUpdates * 3) / 2);
    // first, copy the stack of variables
    StoredFloat[] tmp1 = new StoredFloat[newCapacity];
    System.arraycopy(variableStack, 0, tmp1, 0, variableStack.length);
    variableStack = tmp1;
    // then, copy the stack of former values
    double[] tmp2 = new double[newCapacity];
    System.arraycopy(valueStack, 0, tmp2, 0, valueStack.length);
    valueStack = tmp2;
    // then, copy the stack of world stamps
    int[] tmp3 = new int[newCapacity];
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
    int[] tmp = new int[newWorldCapacity];
    System.arraycopy(worldStartLevels, 0, tmp, 0, worldStartLevels.length);
    worldStartLevels = tmp;
    maxWorlds = newWorldCapacity;
  }
}
