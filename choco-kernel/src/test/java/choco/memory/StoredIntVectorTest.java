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

import choco.kernel.memory.IStateIntVector;
import choco.kernel.memory.trailing.EnvironmentTrailing;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

public class StoredIntVectorTest {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private EnvironmentTrailing env;
  private IStateIntVector iVectA;
  private IStateIntVector iVectB;
  private IStateIntVector vector;

    @Before
  public void setUp() {
    logger.fine("StoredIntVector Testing...");

    env = new EnvironmentTrailing();
    iVectA = env.makeIntVector();
    iVectB = env.makeIntVector(10, 1000);
    vector = env.makeIntVector();
  }

    @After
  public void tearDown() {
    vector = null;
    iVectA = null;
    iVectB = null;
    env = null;
  }

  /**
   * testing the empty constructor with a few backtracks, additions, and updates
   */
  @Test
  public void test1() {
    logger.finer("test1");
    assertEquals(0, env.getWorldIndex());
    assertEquals(0, env.getTrailSize());
    assertTrue(iVectA.isEmpty());
    logger.finest("iVectA OK in root world 0");

    env.worldPush();
    assertEquals(1, env.getWorldIndex());
    iVectA.add(0);
    iVectA.add(1);
    iVectA.add(2);
    iVectA.add(0);
    iVectA.set(3, 3);
    assertEquals(0, iVectA.get(0));
    assertEquals(1, iVectA.get(1));
    assertEquals(2, iVectA.get(2));
    assertEquals(3, iVectA.get(3));
    assertEquals(4, iVectA.size());
    assertEquals(1, env.getTrailSize());
    logger.finest("iVectA OK in updated world 1");

    env.worldPush();
    for (int i = 0; i < 4; i++)
      iVectA.set(i, 50 + i);
    for (int i = 0; i < 4; i++)
      assertTrue(iVectA.get(i) == 50 + i);
    assertEquals(5, env.getTrailSize());
    assertEquals(2, env.getWorldIndex());
    logger.finest("iVectA OK in updated world 2");

    env.worldPop();
    assertEquals(0, iVectA.get(0));
    assertEquals(1, iVectA.get(1));
    assertEquals(2, iVectA.get(2));
    assertEquals(3, iVectA.get(3));
    assertEquals(4, iVectA.size());
    assertEquals(1, env.getTrailSize());
    assertEquals(1, env.getWorldIndex());
    logger.finest("iVectA OK in restored world 1");

    env.worldPop();
    assertEquals(0, env.getWorldIndex());
    assertEquals(0, env.getTrailSize());
    assertTrue(iVectA.isEmpty());
    logger.finest("iVectA OK in world 0");
  }


  /**
   * testing the two constructors with a few backtrack, additions, updates and deletions
   */
  @Test
  public void test2() {
    assertEquals(0, env.getWorldIndex());
    assertEquals(0, env.getTrailSize());
    assertTrue(!iVectB.isEmpty());
    assertEquals(10, iVectB.size());
    for (int i = 0; i < 10; i++)
      assertEquals(1000, iVectB.get(i));
    logger.finest("iVectB OK in root world 0");

    env.worldPush();
    assertEquals(1, env.getWorldIndex());
    for (int i = 0; i < 10; i++) {
      iVectB.set(i, 2000 + i);
      iVectB.set(i, 3000 + i);
    }
    for (int i = 0; i < 10; i++)
      assertEquals(3000 + i, iVectB.get(i));
    assertEquals(10, env.getTrailSize());   // 10 entries
    logger.finest("iVectB OK in updated world 1");

    env.worldPush();
    assertEquals(2, env.getWorldIndex());
    for (int i = 10; i < 20; i++)
      iVectB.add(4000 + i);
    assertEquals(20, iVectB.size());
    for (int i = 10; i < 20; i++)
      assertEquals(4000 + i, iVectB.get(i));
    assertEquals(11, env.getTrailSize());  // only the size is pushed on the trail, not the additions
    for (int i = 10; i < 20; i++)
      iVectB.set(i, 5000 + i);
    assertEquals(11, env.getTrailSize());// 10 modified entries, but in same world -> nothing trailed
    logger.finest("iVectB OK in updated world 2");

    logger.finest("OK before worldPush");
    env.worldPush();
    assertEquals(3, env.getWorldIndex());
    for (int i = 20; i > 10; i--)
      iVectB.removeLast();
    assertEquals(10, iVectB.size());
    assertEquals(12, env.getTrailSize());  // modified the size
    logger.finest("iVectB OK in updated world 3");

    logger.finest("OK before worldPop");
    env.worldPop();
    assertEquals(2, env.getWorldIndex());
    assertEquals(11, env.getTrailSize());
    assertTrue(iVectB.size() == 20);
    for (int i = 10; i < 20; i++)
      assertTrue(iVectB.get(i) == 5000 + i);
    logger.finest("iVectB OK in restored world 2");

    logger.finest("OK before worldPop");
    env.worldPop();
    assertEquals(1, env.getWorldIndex());
    assertEquals(10, iVectB.size());
    assertEquals(10, env.getTrailSize());
    for (int i = 0; i < 10; i++)
      assertTrue(iVectB.get(i) == 3000 + i);
    logger.finest("iVectB OK in restored world 1");

    logger.finest("OK before worldPop");
    env.worldPop();
    assertEquals(0, env.getWorldIndex());
    assertTrue(iVectB.size() == 10);
    assertEquals(0, env.getTrailSize());
    logger.finest("iVectB OK in root world 0");
  }

  /**
   * another small currentElement
   */
  @Test
  public void test3() {
    logger.finer("test3");
    env.worldPush();
    vector.add(1);
    vector.add(2);
    env.worldPush();
    vector.set(0, 2);
    vector.add(3);
    env.worldPush();
    assertEquals(vector.size(), 3);
    assertEquals(vector.get(0), 2);
    assertEquals(vector.get(1), 2);
    assertEquals(vector.get(2), 3);

    env.worldPop();
    env.worldPop();
    assertEquals(vector.size(), 2);
    assertEquals(vector.get(0), 1);
    assertEquals(vector.get(1), 2);

    env.worldPop();
    assertEquals(vector.size(), 0);
  }
}
