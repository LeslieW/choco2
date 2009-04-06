package i_want_to_use_this_old_version_of_choco.mem.trailing;
/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|       Choco-Solver.net    *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco-solver.net        *
 *     + support : support@chocosolver.net        *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                    N. Jussien   1999-2008      *
 **************************************************/

import i_want_to_use_this_old_version_of_choco.mem.ITrailStorage;

public class StoredIntIntervalTrail implements ITrailStorage {

  /**
   * Reference towards the overall environment
   * (responsible for all memory management).
   */

  private EnvironmentTrailing environment;


  /**
   * Stack of backtrackable search variables.
   */

  private StoredIntInterval[] variableStack;


  /**
   * Stack of inf values (former values that need be restored upon backtracking).
   */

  private int[] infStack;

  /**
   * Stack of sup values (former values that need be restored upon backtracking).
   */

  private int[] supStack;

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
   * @param env the environnement to deal with
   * @param nUpdates maximal number of updates that will be stored
   * @param nWorlds  maximal number of worlds that will be stored
   */

  public StoredIntIntervalTrail(EnvironmentTrailing env, int nUpdates, int nWorlds) {
    environment = env;
    currentLevel = 0;
    maxUpdates = nUpdates;
    maxWorlds = nWorlds;
    variableStack = new StoredIntInterval[nUpdates];
    infStack = new int[nUpdates];
    supStack = new int[nUpdates];
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
      StoredIntInterval v = variableStack[currentLevel];
      v._setInf(infStack[currentLevel], stampStack[currentLevel]);
      v._setSup(supStack[currentLevel], stampStack[currentLevel]);
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
      StoredIntInterval var = variableStack[level];
      int valInf = infStack[level];
      int valSup = supStack[level];
      int stamp = stampStack[level];
      var.worldStamp = prevWorld;// update the stamp of the variable (current stamp refers to a world that no longer exists)
      if (stamp != prevWorld) {
        // shift the update if needed
        if (writeIdx != level) {
          infStack[writeIdx] = valInf;
          supStack[writeIdx] = valSup;
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
   * @param v the variable to recrod
   * @param oldValues its old lower bound and upper bound
   * @param oldStamp its old timestamp
   */

  public void savePreviousState(StoredIntInterval v, int[] oldValues, int oldStamp) {
    infStack[currentLevel] = oldValues[0];
    supStack[currentLevel] = oldValues[1];
    variableStack[currentLevel] = v;
    stampStack[currentLevel] = oldStamp;
    currentLevel++;
    if (currentLevel == maxUpdates)
      resizeUpdateCapacity();
  }

  /**
   * Reacts when a StoredInt is modified: push the former inf and sup & timestamp
   * on the stacks.
   * @param v the variable to recrod
   * @param oldinf its old inf value
   * @param oldsup its old sup value
   * @param oldStamp its old timestamp
   */

  public void savePreviousState(StoredIntInterval v, int oldinf, int oldsup, int oldStamp) {
    infStack[currentLevel] = oldinf;
    supStack[currentLevel] = oldsup;
    variableStack[currentLevel] = v;
    stampStack[currentLevel] = oldStamp;
    currentLevel++;
    if (currentLevel == maxUpdates)
      resizeUpdateCapacity();
  }

    /**
     * Resizes the stored capacity
     */
  private void resizeUpdateCapacity() {
    int newCapacity = ((maxUpdates * 3) / 2);
    // first, copy the stack of variables
    StoredIntInterval[] tmp1 = new StoredIntInterval[newCapacity];
    System.arraycopy(variableStack, 0, tmp1, 0, variableStack.length);
    variableStack = tmp1;
    // then, copy the stack of former values
    int[] tmp2 = new int[newCapacity];
    int[] tmp3 = new int[newCapacity];
    System.arraycopy(infStack, 0, tmp2, 0, infStack.length);
    System.arraycopy(supStack, 0, tmp3, 0, supStack.length);
    infStack = tmp2;
    supStack = tmp3;  
    // then, copy the stack of world stamps
    int[] tmp4 = new int[newCapacity];
    System.arraycopy(stampStack, 0, tmp4, 0, stampStack.length);
    stampStack = tmp4;
    // last update the capacity
    maxUpdates = newCapacity;
  }

    /**
     * Resizes world capacity
     * @param newWorldCapacity the new world capactity
     */
  public void resizeWorldCapacity(int newWorldCapacity) {
    int[] tmp = new int[newWorldCapacity];
    System.arraycopy(worldStartLevels, 0, tmp, 0, worldStartLevels.length);
    worldStartLevels = tmp;
    maxWorlds = newWorldCapacity;
  }
}
