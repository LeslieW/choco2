package i_want_to_use_this_old_version_of_choco.mem;

import i_want_to_use_this_old_version_of_choco.mem.trailing.EnvironmentTrailing;
import junit.framework.TestCase;

import java.util.logging.Logger;

public class PartiallyStoredVectorTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private IEnvironment env;
  private PartiallyStoredVector vector;

  protected void setUp() {
    logger.fine("StoredIntVector Testing...");

    env = new EnvironmentTrailing();
    vector = env.makePartiallyStoredVector();
  }

  protected void tearDown() {
    vector = null;
    env = null;
  }

  /**
   * testing the empty constructor with a few backtracks, additions, and updates
   */
  public void test1() {
    logger.finer("test1");
    assertEquals(0, env.getWorldIndex());
    assertTrue(vector.isEmpty());
    env.worldPush();
    assertEquals(1, env.getWorldIndex());
    vector.add(new Integer(0));
    vector.add(new Integer(1));
    env.worldPush();
    assertEquals(2, env.getWorldIndex());
    vector.add(new Integer(2));
    vector.add(new Integer(3));
    vector.staticAdd(new Integer(4));
    assert(vector.size() == 5);
    env.worldPop();
    assert(vector.size() == 3);
    assertEquals(1, env.getWorldIndex());
    env.worldPop();
    assert(vector.size() == 1);
    assertEquals(0, env.getWorldIndex());
  }
}
