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
 * Implementing storage of historical values for backtrackable integers.
 *
 * @see ITrailStorage
 */
public final class StoredBoolTrail implements ITrailStorage {
  /**
   * Reference to an object for logging trace statements related memory & backtrack (using the java.util.logging package)
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

  private StoredBool[] variableStack;


  /**
   * Stack of values (former values that need be restored upon backtracking).
   */

  private boolean[] valueStack;


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
   * capacity of the trailing stack (in terms of number of updates that can be stored)
   */
  private int maxUpdates = 0;

  /**
   * capacity of the trailing stack (in terms of number of worlds that can be handled)
   */
  private int maxWorlds = 0;

  /**
   * Constructs a trail with predefined size.
   *
   * @param nUpdates maximal number of updates that will be stored
   * @param nWorlds  maximal number of worlds that will be stored
   */

  public StoredBoolTrail(EnvironmentTrailing env, int nUpdates, int nWorlds) {
    environment = env;
    currentLevel = 0;
    maxUpdates = nUpdates;
    maxWorlds = nWorlds;
    variableStack = new StoredBool[nUpdates];
    valueStack = new boolean[nUpdates];
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
      StoredBool v = variableStack[currentLevel];
      v._set(valueStack[currentLevel], stampStack[currentLevel]);
    }
  }


  /**
   * Returns the current size of the stack.
   */

  public int getSize() {
    return currentLevel;
  }


  /**
   * Comits a world: merging it with the previous one.
   */

  public void worldCommit() {
    // principle:
    //   currentLevel decreases to end of previous world
    //   updates of the committed world are scanned:
    //     if their stamp is the previous one (merged with the current one) -> remove the update (garbage collecting this position for the next update)
    //     otherwise update the worldStamp
    int startLevel = worldStartLevels[environment.getWorldIndex()];
    int prevWorld = environment.getWorldIndex() - 1;
    int writeIdx = startLevel;
    for (int level = startLevel; level < currentLevel; level++) {
      StoredBool var = variableStack[level];
      boolean val = valueStack[level];
      int stamp = stampStack[level];
      var.worldStamp = prevWorld;// update the stamp of the variable (current stamp refers to a world that no longer exists)
      if (stamp != prevWorld) {
        // shift the update if needed
        if (writeIdx != level) {
          valueStack[writeIdx] = val;
          variableStack[writeIdx] = var;
          stampStack[writeIdx] = stamp;
        }
        writeIdx++;
      }  //else:writeIdx is not incremented and the update will be discarded (since a good one is in prevWorld)
    }
    currentLevel = writeIdx;
  }


  /**
   * Reacts when a StoredInt is modified: push the former value & timestamp
   * on the stacks.
   */

  public void savePreviousState(StoredBool v, boolean oldValue, int oldStamp) {
    valueStack[currentLevel] = oldValue;
    variableStack[currentLevel] = v;
    stampStack[currentLevel] = oldStamp;
    currentLevel++;
    if (currentLevel == maxUpdates)
      resizeUpdateCapacity();
  }

  private void resizeUpdateCapacity() {
    int newCapacity = ((maxUpdates * 3) / 2);
    // first, copy the stack of variables
    StoredBool[] tmp1 = new StoredBool[newCapacity];
    System.arraycopy(variableStack, 0, tmp1, 0, variableStack.length);
    variableStack = tmp1;
    // then, copy the stack of former values
    boolean[] tmp2 = new boolean[newCapacity];
    System.arraycopy(valueStack, 0, tmp2, 0, valueStack.length);
    valueStack = tmp2;
    // then, copy the stack of world stamps
    int[] tmp3 = new int[newCapacity];
    System.arraycopy(stampStack, 0, tmp3, 0, stampStack.length);
    stampStack = tmp3;
    // last update the capacity
    maxUpdates = newCapacity;
  }

  public void resizeWorldCapacity(int newWorldCapacity) {
    int[] tmp = new int[newWorldCapacity];
    System.arraycopy(worldStartLevels, 0, tmp, 0, worldStartLevels.length);
    worldStartLevels = tmp;
    maxWorlds = newWorldCapacity;
  }
}

