package i_want_to_use_this_old_version_of_choco.mem;

import i_want_to_use_this_old_version_of_choco.mem.trailing.EnvironmentTrailing;
import i_want_to_use_this_old_version_of_choco.mem.trailing.StoredBitSet;
import i_want_to_use_this_old_version_of_choco.mem.trailing.StoredIntVector;
import junit.framework.TestCase;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 8 d�c. 2003
 * Time: 15:46:11
 * To change this template use Options | File Templates.
 */
public class StoredBitSetTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private EnvironmentTrailing env;
  private MyStoredBitSet bSetA;
  private MyStoredBitSet bSetB;

  /**
   * Makes the representedBy fiels public via an accessor for currentElement purpose.
   */
  class MyStoredBitSet extends StoredBitSet {
    public MyStoredBitSet(EnvironmentTrailing env, int size) {
      super(env, size);
    }

    public StoredIntVector getRepresentedBy() {
        return this.representedBy;
      }
  }

  protected void setUp() {
    logger.fine("StoredBitSetTest Testing...");

    env = new EnvironmentTrailing();
    bSetA = new MyStoredBitSet(env, 5);
    bSetB = new MyStoredBitSet(env, 33);
  }

  protected void tearDown() {
    bSetA = null;
    bSetB = null;
    env = null;
  }

  /**
   * testing the empty constructor with a few backtracks, additions, and updates
   */
  public void test1() {
    logger.finer("test1");
    assertEquals(0, env.getWorldIndex());
    assertEquals(0, env.getTrailSize());
    logger.finest("bsetA" + bSetA.getRepresentedBy().get(0));

    for (int i = 0; i < 5; i++) {
      assertFalse(bSetA.get(i));
    }
    logger.finest("bSetA OK in root world 0");

    env.worldPush();
    assertEquals(0, env.getTrailSize());
    assertEquals(1, env.getWorldIndex());
    bSetA.set(0);
    bSetA.set(1);
    bSetA.set(2);
    bSetA.set(3);
    bSetA.set(4);
    for (int i = 0; i < 5; i++) {
      assertTrue(bSetA.get(i));
    }
    assertEquals(1, env.getTrailSize());
    bSetA.set(2);
    bSetA.set(3);
    assertEquals(1, env.getTrailSize());
    for (int i = 0; i < 5; i++) {
      assertTrue(bSetA.get(i));
    }
    env.worldPush();
    bSetA.set(2);
    assertEquals(1, env.getTrailSize());
    assertTrue(bSetA.get(2));
    env.worldPop();

    bSetA.clear(2);
    assertFalse(bSetA.get(2));
    assertEquals(1, env.getTrailSize());
    env.worldPop();
    assertEquals(0, env.getTrailSize());
    assertEquals(0, env.getWorldIndex());
    for (int i = 0; i < 5; i++) {
      assertFalse(bSetA.get(i));
    }

    logger.finest("bSetA OK in world 0");
  }

  public void test2() {
    logger.finer("test2");
    assertEquals(0, env.getWorldIndex());
    assertEquals(0, env.getTrailSize());
    logger.finest("bsetB" + bSetB.getRepresentedBy().get(0));

    for (int i = 0; i < 33; i++) {
      assertFalse(bSetB.get(i));
    }
    logger.finest("bSetB OK in root world 0");

    env.worldPush();
    assertEquals(0, env.getTrailSize());
    assertEquals(1, env.getWorldIndex());
    bSetB.set(0);
    bSetB.set(1);
    bSetB.set(2);
    bSetB.set(3);
    bSetB.set(4);
    for (int i = 0; i < 5; i++) {
      assertTrue(bSetB.get(i));
    }
    assertEquals(1, env.getTrailSize());
    bSetB.set(32);
    assertEquals(2, env.getTrailSize());

    assertTrue(bSetB.get(32));
    assertFalse(bSetB.get(31));

    bSetB.clear(32);
    assertFalse(bSetB.get(32));

    env.worldPop();
    assertEquals(0, env.getTrailSize());
    assertEquals(0, env.getWorldIndex());
    for (int i = 0; i < 33; i++) {
      assertFalse(bSetB.get(i));
    }
    logger.finest("bSetB OK in world 0");
  }

  /**
   * A currentElement for methods trailingZeroCnt, startingZeroCnt
   */
  public void test3() {
    logger.finer("test3");
    // 10 = 2^3 + 2^1
    assertEquals(1, StoredBitSet.trailingZeroCnt(10));
    assertEquals(28, StoredBitSet.startingZeroCnt(10));
    // 16 = 2^4
    assertEquals(4, StoredBitSet.trailingZeroCnt(16));
    assertEquals(27, StoredBitSet.startingZeroCnt(16));
    for (int i = 0; i < 31; i++) {
      assertEquals(i, StoredBitSet.trailingZeroCnt(1 << i));
      assertEquals(31 - i, StoredBitSet.startingZeroCnt(1 << i));
    }
  }

  /**
   * A currentElement for methods nextSetBit, prevSetBit
   */
  public void test4() {
    logger.finer("test4");
    assertEquals(-1, bSetA.prevSetBit(4));
    assertEquals(-1, bSetA.nextSetBit(0));
    bSetA.set(2);
    bSetA.set(4);
    assertEquals(4, bSetA.prevSetBit(4));
    assertEquals(2, bSetA.prevSetBit(3));
    assertEquals(2, bSetA.prevSetBit(2));
    assertEquals(-1, bSetA.prevSetBit(1));
    assertEquals(2, bSetA.nextSetBit(0));
    assertEquals(2, bSetA.nextSetBit(2));
    assertEquals(4, bSetA.nextSetBit(3));
    assertEquals(4, bSetA.nextSetBit(4));

    assertEquals(-1, bSetB.prevSetBit(33));
    assertEquals(-1, bSetB.nextSetBit(0));
    bSetB.set(2);
    bSetB.set(17);
    bSetB.set(32);
    assertEquals(32, bSetB.prevSetBit(32));
    assertEquals(17, bSetB.prevSetBit(31));
    assertEquals(17, bSetB.prevSetBit(17));
    assertEquals(2, bSetB.prevSetBit(16));
    assertEquals(2, bSetB.prevSetBit(2));
    assertEquals(-1, bSetB.prevSetBit(1));
    assertEquals(2, bSetB.nextSetBit(0));
    assertEquals(2, bSetB.nextSetBit(2));
    assertEquals(17, bSetB.nextSetBit(3));
    assertEquals(17, bSetB.nextSetBit(17));
    assertEquals(32, bSetB.nextSetBit(18));
    assertEquals(32, bSetB.nextSetBit(32));
  }

  public void test5() {
    logger.finer("test5");
    bSetB.set(0);
    bSetB.set(2);
    assertEquals(bSetB.prevSetBit(7), 2);
  }

  public void test6() {
    logger.finer("test6");
    bSetB.set(0);
    bSetB.set(2);
    assertEquals(bSetB.prevSetBit(7), 2);
    env.worldPush();
	bSetB.set(1);
	env.worldPush();
	bSetB.clear();
	assertEquals(bSetB.cardinality(), 0);
	assertEquals(bSetB.nextSetBit(0), -1);	  
	env.worldPop();
	assertEquals(bSetB.cardinality(), 3);
  }
}
