/*
 * T_StoredInt.java
 *
 */

package i_want_to_use_this_old_version_of_choco.mem;

import i_want_to_use_this_old_version_of_choco.mem.trailing.EnvironmentTrailing;
import junit.framework.TestCase;

import java.util.logging.Logger;

/**
 * a class implementing tests for backtrackable search
 */
public class StoredIntTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private EnvironmentTrailing env;
  private IStateInt x1;
  private IStateInt x2;
  private IStateInt x3;

  protected void setUp() {
    logger.fine("StoredInt Testing...");
    env = new EnvironmentTrailing();
    x1 = env.makeInt(0);
    x2 = env.makeInt(0);
    x3 = env.makeInt();
  }

  protected void tearDown() {
    x1 = null;
    x2 = null;
    x3 = null;
    env = null;
  }

  /**
   * testing one backtrack
   */
  public void test1() {
    logger.finer("test1");
    assertTrue(env.getWorldIndex() == 0);
    assertTrue(env.getTrailSize() == 0);
    env.worldPush();
    x1.set(1);
    assertTrue(x1.get() == 1);
    assertTrue(env.getWorldIndex() == 1);
    assertTrue(env.getTrailSize() == 1);
    env.worldPop();
    assertTrue(x1.get() == 0);
    assertTrue(env.getWorldIndex() == 0);
    assertTrue(env.getTrailSize() == 0);
  }

  /**
   * testing a bunch of backtracks
   */
  public void test2() {
    logger.finer("test2");
    assertTrue(env.getWorldIndex() == 0);
    assertTrue(env.getTrailSize() == 0);
    for (int i = 1; i <= 100; i++) {
      env.worldPush();
      x1.set(i);
      x1.set(2 * i);
      x1.set(3 * i);
      x1.set(i);
      assertTrue(env.getWorldIndex() == i);
      assertTrue(env.getTrailSize() == i);
      assertTrue(x1.get() == i);
    }
    for (int i = 100; i >= 1; i--) {
      env.worldPop();
      assertTrue(env.getWorldIndex() == i - 1);
      assertTrue(env.getTrailSize() == i - 1);
      assertTrue(x1.get() == i - 1);
    }
  }

}