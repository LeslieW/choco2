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
package choco.memory;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.memory.IStateBitSet;
import choco.kernel.memory.trailing.EnvironmentTrailing;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;
/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 8 d�c. 2003
 * Time: 15:46:11
 * To change this template use Options | File Templates.
 */
public class StoredBitSetTest {
	protected final static Logger LOGGER = ChocoLogging.getTestLogger();
  private EnvironmentTrailing env;
  private IStateBitSet bSetA;
  private IStateBitSet bSetB;

  /**
   * Makes the representedBy fiels public via an accessor for currentElement purpose.
   */
  /*class MyStoredBitSet extends StoredBitSet {
    public MyStoredBitSet(EnvironmentTrailing env, int size) {
      super(env, size);
    }

    public StoredIntVector getRepresentedBy() {
        return this.representedBy;
      }
  }   */

    @Before
  public void setUp() {
    LOGGER.fine("StoredBitSetTest Testing...");

    env = new EnvironmentTrailing();
    bSetA = env.makeBitSet(5);
    bSetB = env.makeBitSet(33);
  }

    @After
  public void tearDown() {
    bSetA = null;
    bSetB = null;
    env = null;
  }

  /**
   * testing the empty constructor with a few backtracks, additions, and updates
   */
  @Test
  public void test1() {
    LOGGER.finer("test1");
    assertEquals(0, env.getWorldIndex());
    assertEquals(0, env.getTrailSize());
    //LOGGER.finest("bsetA" + bSetA.getRepresentedBy().get(0));

    for (int i = 0; i < 5; i++) {
      assertFalse(bSetA.get(i));
    }
    LOGGER.finest("bSetA OK in root world 0");

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
    assertEquals(2, env.getTrailSize());
    bSetA.set(2);
    bSetA.set(3);
    assertEquals(2, env.getTrailSize());
    for (int i = 0; i < 5; i++) {
      assertTrue(bSetA.get(i));
    }
    env.worldPush();
    bSetA.set(2);
    assertEquals(2, env.getTrailSize());
    assertTrue(bSetA.get(2));
    env.worldPop();

    bSetA.clear(2);
    assertFalse(bSetA.get(2));
    assertEquals(2, env.getTrailSize());
    env.worldPop();
    assertEquals(0, env.getTrailSize());
    assertEquals(0, env.getWorldIndex());
    for (int i = 0; i < 5; i++) {
      assertFalse(bSetA.get(i));
    }

    LOGGER.finest("bSetA OK in world 0");
  }

    @Test
  public void test2() {
    LOGGER.finer("test2");
    assertEquals(0, env.getWorldIndex());
    assertEquals(0, env.getTrailSize());
    //LOGGER.finest("bsetB" + bSetB.getRepresentedBy().get(0));

    for (int i = 0; i < 33; i++) {
      assertFalse(bSetB.get(i));
    }
    LOGGER.finest("bSetB OK in root world 0");

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
    assertEquals(2, env.getTrailSize());
    bSetB.set(64);
    assertEquals(2, env.getTrailSize());

    assertTrue(bSetB.get(64));
    assertFalse(bSetB.get(63));

    bSetB.clear(64);
    assertFalse(bSetB.get(64));

    env.worldPop();
    assertEquals(0, env.getTrailSize());
    assertEquals(0, env.getWorldIndex());
    for (int i = 0; i < 33; i++) {
      assertFalse(bSetB.get(i));
    }
    LOGGER.finest("bSetB OK in world 0");
  }

  /**
   * A currentElement for methods trailingZeroCnt, startingZeroCnt
   */
  @Test
  public void test3() {
    LOGGER.finer("test3");
    // 10 = 2^3 + 2^1
    assertEquals(1, Long.numberOfTrailingZeros(10));
    assertEquals(60, Long.numberOfLeadingZeros(10));
    // 16 = 2^4
    assertEquals(4, Long.numberOfTrailingZeros(16));
    assertEquals(59, Long.numberOfLeadingZeros(16));
    for (int i = 0; i < 63; i++) {
      assertEquals(i, Long.numberOfTrailingZeros(1l << i));
      assertEquals(63 - i, Long.numberOfLeadingZeros((1l << i)));
    }
  }

  /**
   * A currentElement for methods nextSetBit, prevSetBit
   */
  @Test
  public void test4() {
    LOGGER.finer("test4");
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

    @Test
  public void test5() {
    LOGGER.finer("test5");
    bSetB.set(0);
    bSetB.set(2);
    assertEquals(bSetB.prevSetBit(7), 2);
  }

    @Test
  public void test6() {
    LOGGER.finer("test6");
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
