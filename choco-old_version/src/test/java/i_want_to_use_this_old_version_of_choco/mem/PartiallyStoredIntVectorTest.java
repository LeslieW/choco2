package i_want_to_use_this_old_version_of_choco.mem;

import i_want_to_use_this_old_version_of_choco.mem.trailing.EnvironmentTrailing;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;
import junit.framework.TestCase;

import java.util.logging.Logger;

public class PartiallyStoredIntVectorTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private IEnvironment env;
  private PartiallyStoredIntVector vector;

  protected void setUp() {
    logger.fine("StoredIntVector Testing...");

    env = new EnvironmentTrailing();
    vector = env.makePartiallyStoredIntVector();
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
    vector.add(0);
    vector.add(1);
    env.worldPush();
    assertEquals(2, env.getWorldIndex());
    vector.add(2);
    vector.add(3);
    vector.staticAdd(4);
    assert(vector.size() == 5);
    int[] values = new int[]{4, 0, 1, 2, 3};
    int nValue = 0;
    for (IntIterator it = vector.getIndexIterator(); it.hasNext();) {
      int index = it.next();
      int value = vector.get(index);
      assertEquals(values[nValue], value);
      nValue++;
    }
    env.worldPop();
    assert(vector.size() == 3);
    values = new int[]{4, 0, 1};
    nValue = 0;
    for (IntIterator it = vector.getIndexIterator(); it.hasNext();) {
      int index = it.next();
      int value = vector.get(index);
      assertEquals(values[nValue], value);
      nValue++;
    }

    assertEquals(1, env.getWorldIndex());
    env.worldPop();
    assert(vector.size() == 1);
    values = new int[]{4};
    nValue = 0;
    for (IntIterator it = vector.getIndexIterator(); it.hasNext();) {
      int index = it.next();
      int value = vector.get(index);
      assertEquals(values[nValue], value);
      nValue++;
    }

    assertEquals(0, env.getWorldIndex());
  }
}
