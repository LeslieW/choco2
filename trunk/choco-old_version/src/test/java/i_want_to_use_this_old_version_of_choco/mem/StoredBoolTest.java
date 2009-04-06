package i_want_to_use_this_old_version_of_choco.mem;

import i_want_to_use_this_old_version_of_choco.mem.trailing.EnvironmentTrailing;
import junit.framework.TestCase;

import java.util.logging.Logger;


/**
 * a class implementing tests for backtrackable booleans
 */
public class StoredBoolTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private EnvironmentTrailing env;
  private IStateBool x1;
  private IStateBool x2;
  private IStateBool x3;

  protected void setUp() {
    logger.fine("StoredBool Testing...");
    env = new EnvironmentTrailing();
    x1 = env.makeBool(true);
    x2 = env.makeBool(true);
    x3 = env.makeBool(true);
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
    x1.set(false);
    assertTrue(x1.get() == false);
    assertTrue(env.getWorldIndex() == 1);
    assertTrue(env.getTrailSize() == 1);
    env.worldPop();
    assertTrue(x1.get() == true);
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
      x1.set(!x1.get());
      x1.set(!x1.get());
      x1.set(!x1.get());
      assertTrue(env.getWorldIndex() == i);
      assertTrue(env.getTrailSize() == i);
      assertTrue(x1.get() == ((i % 2) == 0));
    }
    for (int i = 100; i >= 1; i--) {
      env.worldPop();
      assertTrue(env.getWorldIndex() == i - 1);
      assertTrue(env.getTrailSize() == i - 1);
      assertTrue(x1.get() == ((i % 2) == 1));
    }
  }

}