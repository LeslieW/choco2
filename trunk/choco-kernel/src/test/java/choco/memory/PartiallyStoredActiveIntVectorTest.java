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
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.structure.PartiallyStoredActiveIntVector;
import choco.kernel.memory.trailing.EnvironmentTrailing;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

public class PartiallyStoredActiveIntVectorTest {

	protected final static Logger LOGGER = ChocoLogging.getTestLogger();

  private IEnvironment env;
  private PartiallyStoredActiveIntVector llist;

    @Before
  public void setUp() {
    LOGGER.fine("PartiallyStoredActiveIntVectorTest Testing...");

    env = new EnvironmentTrailing();
    llist = new PartiallyStoredActiveIntVector(env);
  }

    @After
  public void tearDown() {
    llist = null;
    env = null;
  }

  /**
   * testing the empty constructor with a few backtracks, additions, and updates
   */
  @Test
  public void test1() {
      LOGGER.finer("test1");
      llist.staticAdd(0, true);
      llist.staticAdd(1, false);
      llist.staticAdd(2, true);

      DisposableIntIterator it = llist.getIndexIterator();
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(0, it.next());
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(2, it.next());
      Assert.assertFalse(it.hasNext());

      env.worldPush();
      llist.staticAdd(3, true);

      llist.set(1, true);
      llist.set(0, false);

      it = llist.getIndexIterator();
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(2, it.next());
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(3, it.next());
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(1, it.next());
      Assert.assertFalse(it.hasNext());
  }

  @Test
  public void test2() {
      LOGGER.finer("test2");
      llist.add(1000000, true);
      llist.add(1000001, false);
      llist.add(1000002, true);

      DisposableIntIterator it = llist.getIndexIterator();
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(1000000, it.next());
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(1000002, it.next());
      Assert.assertFalse(it.hasNext());

      env.worldPush();
      llist.add(1000003, true);

      llist.set(1000001, true);
      llist.set(1000000, false);

      it = llist.getIndexIterator();
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(1000002, it.next());
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(1000003, it.next());
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(1000001, it.next());
      Assert.assertFalse(it.hasNext());

      env.worldPush();

      llist.set(1000003, false);

      it = llist.getIndexIterator();
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(1000002, it.next());
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(1000001, it.next());
      Assert.assertFalse(it.hasNext());

      llist.set(1000001, false);

      it = llist.getIndexIterator();
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(1000002, it.next());
      Assert.assertFalse(it.hasNext());

      llist.set(1000002, false);

      it = llist.getIndexIterator();
      Assert.assertFalse(it.hasNext());

      env.worldPop();

      it = llist.getIndexIterator();
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(1000002, it.next());
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(1000003, it.next());
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(1000001, it.next());
      Assert.assertFalse(it.hasNext());

      env.worldPop();

      it = llist.getIndexIterator();
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(1000000, it.next());
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(1000002, it.next());
      Assert.assertFalse(it.hasNext());
  }

   @Test
  public void test3() {
       LOGGER.finer("test3");
       llist.staticAdd(0, true);
       llist.staticAdd(1, true);
       llist.staticAdd(2, true);

       DisposableIntIterator it = llist.getIndexIterator();
       Assert.assertTrue(it.hasNext());
       Assert.assertEquals(0, it.next());
       Assert.assertTrue(it.hasNext());
       Assert.assertEquals(1, it.next());
       Assert.assertTrue(it.hasNext());
       Assert.assertEquals(2, it.next());
       Assert.assertFalse(it.hasNext());

       
       llist.set(2, false);
       llist.set(0, false);
       llist.set(1, false);

       it = llist.getIndexIterator();
       Assert.assertFalse(it.hasNext());

       llist.set(0, false);

       it = llist.getIndexIterator();
       Assert.assertFalse(it.hasNext());
   }

    @Test
  public void test4() {
       LOGGER.finer("test4");
       llist.add(1000000, true);
       llist.add(1000001, true);
       llist.add(1000002, true);

       DisposableIntIterator it = llist.getIndexIterator();
       Assert.assertTrue(it.hasNext());
       Assert.assertEquals(1000000, it.next());
       Assert.assertTrue(it.hasNext());
       Assert.assertEquals(1000001, it.next());
       Assert.assertTrue(it.hasNext());
       Assert.assertEquals(1000002, it.next());
       Assert.assertFalse(it.hasNext());

       llist.set(1000002, false);
       llist.set(1000000, false);
       llist.set(1000001, false);

       it = llist.getIndexIterator();
       Assert.assertFalse(it.hasNext());

       llist.set(1000000, false);

       it = llist.getIndexIterator();
       Assert.assertFalse(it.hasNext());
   }
}