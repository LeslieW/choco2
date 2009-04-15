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
import choco.kernel.memory.IStateBool;
import choco.kernel.memory.trailing.EnvironmentTrailing;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * a class implementing tests for backtrackable booleans
 */
public class StoredBoolTest {
	protected final static Logger LOGGER = ChocoLogging.getTestLogger();
  private EnvironmentTrailing env;
  private IStateBool x1;
  private IStateBool x2;
  private IStateBool x3;

    @Before
  public void setUp() {
    LOGGER.fine("StoredBool Testing...");
    env = new EnvironmentTrailing();
    x1 = env.makeBool(true);
    x2 = env.makeBool(true);
    x3 = env.makeBool(true);
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
  @Test
  public void test2() {
    LOGGER.finer("test2");
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