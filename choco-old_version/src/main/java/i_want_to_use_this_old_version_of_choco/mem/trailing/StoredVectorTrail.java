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
 * Implements a trail with the history of all the stored search vectors.
 */
public class StoredVectorTrail implements ITrailStorage {
  /**
   * Reference to an object for logging trace statements related memory & backtrack (using the java.util.logging package)
   */

  private static Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.mem");

  /**
   * The current environment.
   */

  private EnvironmentTrailing environment;


  /**
   * All the stored search vectors.
   */

  private StoredVector[] vectorStack;


  /**
   * Indices of the previous values in the stored vectors.
   */

  private int[] indexStack;


  /**
   * Previous values of the stored vector elements.
   */

  private Object[] valueStack;


  /**
   * World stamps associated to the previous values
   */

  private int[] stampStack;

  /**
   * The last world an search vector was modified in.
   */

  private int currentLevel;


  /**
   * Starts of levels in all the history arrays.
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
   * Constructs a trail for the specified environment with the
   * specified numbers of updates and worlds.
   */

  public StoredVectorTrail(EnvironmentTrailing env, int nUpdates, int nWorlds) {
    this.environment = env;
    this.currentLevel = 0;
    maxUpdates = nUpdates;
    maxWorlds = nWorlds;
    this.vectorStack = new StoredVector[nUpdates];
    this.indexStack = new int[nUpdates];
    this.valueStack = new Object[nUpdates];
    this.stampStack = new int[nUpdates];
    this.worldStartLevels = new int[nWorlds];
  }


  /**
   * Reacts on the modification of an element in a stored search vector.
   */

  public void savePreviousState(StoredVector vect, int index, Object oldValue, int oldStamp) {
    this.vectorStack[currentLevel] = vect;
    this.indexStack[currentLevel] = index;
    this.stampStack[currentLevel] = oldStamp;
    this.valueStack[currentLevel] = oldValue;
    currentLevel++;
    if (currentLevel == maxUpdates)
      resizeUpdateCapacity();
  }

  private void resizeUpdateCapacity() {
    int newCapacity = ((maxUpdates * 3) / 2);
    // first, copy the stack of variables
    StoredVector[] tmp1 = new StoredVector[newCapacity];
    System.arraycopy(vectorStack, 0, tmp1, 0, vectorStack.length);
    vectorStack = tmp1;
    // then, copy the stack of former values
    Object[] tmp2 = new Object[newCapacity];
    System.arraycopy(valueStack, 0, tmp2, 0, valueStack.length);
    valueStack = tmp2;
    // then, copy the stack of world stamps
    int[] tmp3 = new int[newCapacity];
    System.arraycopy(stampStack, 0, tmp3, 0, stampStack.length);
    stampStack = tmp3;
    // then, copy the stack of indices
    int[] tmp4 = new int[newCapacity];
    System.arraycopy(indexStack, 0, tmp4, 0, indexStack.length);
    indexStack = tmp4;

    // last update the capacity
    maxUpdates = newCapacity;
  }

  public void resizeWorldCapacity(int newWorldCapacity) {
    int[] tmp = new int[newWorldCapacity];
    System.arraycopy(worldStartLevels, 0, tmp, 0, worldStartLevels.length);
    worldStartLevels = tmp;
    maxWorlds = newWorldCapacity;
  }

  /**
   * Moving up to the next world.
   */

  public void worldPush() {
    this.worldStartLevels[this.environment.getWorldIndex() + 1] = currentLevel;
  }


  /**
   * Moving down to the previous world.
   */

  public void worldPop() {
    while (currentLevel > worldStartLevels[this.environment.getWorldIndex()]) {
      currentLevel--;
      StoredVector v = vectorStack[currentLevel];
      v._set(indexStack[currentLevel], valueStack[currentLevel], stampStack[currentLevel]);
    }
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
      StoredVector var = vectorStack[level];
      int idx = indexStack[level];
      Object val = valueStack[level];
      int stamp = stampStack[level];
      var.worldStamps[idx] = prevWorld;// update the stamp of the variable (current stamp refers to a world that no longer exists)
      if (stamp != prevWorld) {
        // shift the update if needed
        if (writeIdx != level) {
          valueStack[writeIdx] = val;
          indexStack[writeIdx] = idx;
          vectorStack[writeIdx] = var;
          stampStack[writeIdx] = stamp;
        }
        writeIdx++;
      }  //else:writeIdx is not incremented and the update will be discarded (since a good one is in prevWorld)
    }
    currentLevel = writeIdx;
  }


  /**
   * Returns the current size of the stack.
   */

  public int getSize() {
    return currentLevel;
  }
}
