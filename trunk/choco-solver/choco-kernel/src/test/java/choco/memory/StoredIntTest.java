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
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.copy.EnvironmentCopying;
import choco.kernel.memory.trailing.EnvironmentTrailing;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;
/**
 * a class implementing tests for backtrackable search
 */
public class StoredIntTest {
	protected final static Logger LOGGER = ChocoLogging.getTestLogger();
  private EnvironmentTrailing env;
  private IStateInt x1;
  private IStateInt x2;
  private IStateInt x3;

    @Before
  public void setUp() {
    LOGGER.fine("StoredInt Testing...");
    env = new EnvironmentTrailing();
    x1 = env.makeInt(0);
    x2 = env.makeInt(0);
    x3 = env.makeInt();
  }

    @After
  public void tearDown() {
    x1 = null;
    x2 = null;
    x3 = null;
    env = null;
  }

  /**
   * testing one backtrack
   */
  @Test
  public void test1() {
    LOGGER.finer("test1");
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
  @Test
  public void test2() {
    LOGGER.finer("test2");
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

    @Test
    public void test3(){
        EnvironmentTrailing et = new EnvironmentTrailing();
        EnvironmentCopying ec = new EnvironmentCopying();
        IStateInt t1 = et.makeInt(0);
        IStateInt c1 = ec.makeInt(0);

        et.worldPush();
        ec.worldPush();
        Assert.assertEquals(t1.get(), c1.get());

        t1.set(1);
        c1.set(1);
        Assert.assertEquals(t1.get(), c1.get());

        et.worldPush();
        ec.worldPush();
        Assert.assertEquals(t1.get(), c1.get());

        t1.set(2);
        c1.set(2);
        Assert.assertEquals(t1.get(), c1.get());

        et.worldPush();
        ec.worldPush();
        Assert.assertEquals(t1.get(), c1.get());

        t1.set(1);
        c1.set(1);
        Assert.assertEquals(t1.get(), c1.get());

        et.worldPop();
        ec.worldPop();
        Assert.assertEquals(t1.get(), c1.get());

        et.worldPop();
        ec.worldPop();
        Assert.assertEquals(t1.get(), c1.get());

        et.worldPop();
        ec.worldPop();
        Assert.assertEquals(t1.get(), c1.get());

        et.worldPush();
        ec.worldPush();
        Assert.assertEquals(t1.get(), c1.get());

        t1.set(1);
        c1.set(1);
        Assert.assertEquals(t1.get(), c1.get());
    }


}