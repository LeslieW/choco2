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
package choco.common;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.objects.StoredPointerCycle;
import choco.kernel.memory.trailing.EnvironmentTrailing;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

// *********************************************
// *                   J-CHOCO                 *
// *   Copyright (c) F. Laburthe, 1999-2003    *
// *********************************************
// * Event-base contraint programming Engine   *
// *********************************************

// CVS Information
// File:               $RCSfile: StoredPointerCycleTest.java,v $
// Version:            $Revision: 1.3 $
// Last Modification:  $Date: 2007/07/16 15:17:34 $
// Last Contributor:   $Author: menana $

public class StoredPointerCycleTest {
 
	protected final static Logger LOGGER = ChocoLogging.getTestLogger();
	
  private choco.kernel.memory.IEnvironment env;
  private StoredPointerCycle pcyc1;
  private StoredPointerCycle pcyc2;
  private DisposableIntIterator it;

    @Before
  public void setUp() {
   LOGGER.fine("StoredPointerCycle Testing...");
    env = new EnvironmentTrailing();
    pcyc1 = new StoredPointerCycle(env);
    for (int i = 0; i < 5; i++) {
      pcyc1.add(i, true);
    }
    pcyc1.setOutOfCycle(1);
    pcyc1.setOutOfCycle(3);

    pcyc2 = new StoredPointerCycle(env);
    for (int i = 0; i < 5; i++) {
      pcyc2.add(i, true);
    }
    for (int i = 0; i < 5; i++) {
      pcyc2.setOutOfCycle(i);
    }
    pcyc2.setInCycle(3);
  }

    @After
  public void tearDown() {
    it = null;
    pcyc1 = null;
    pcyc2 = null;
    env = null;
  }

  /**
   * A simple currentElement of full cycle iteration with no removals during the iteration
   */
  @Test
  public void test1() {
   LOGGER.finer("test1");
    it = pcyc1.getCycleButIterator(-1);
    assertTrue(it.hasNext());
    assertEquals(0, it.next());
    assertTrue(it.hasNext());
    assertEquals(2, it.next());
    assertTrue(it.hasNext());
    assertEquals(4, it.next());
    assertFalse(it.hasNext());
  }

  /**
   * A simple currentElement of a cycle iteration, starting from a value within the cycle with no removals during the iteration
   */
  @Test
  public void test2() {
   LOGGER.finer("test2");
    it = pcyc1.getCycleButIterator(2);
    assertTrue(it.hasNext());
    assertEquals(4, it.next());
    assertTrue(it.hasNext());
    assertEquals(0, it.next());
    assertFalse(it.hasNext());
  }

  /**
   * A simple currentElement of a cycle iteration, starting from a value outside the cycle with no removals during the iteration
   */
  @Test
  public void test3() {
   LOGGER.finer("test3");
    it = pcyc1.getCycleButIterator(3);
    assertTrue(it.hasNext());
    assertEquals(4, it.next());
    assertTrue(it.hasNext());
    assertEquals(0, it.next());
    assertTrue(it.hasNext());
    assertEquals(2, it.next());
    assertFalse(it.hasNext());
  }

  /**
   * A simple currentElement of a cycle iteration, starting from the first value in the cycle with no removals during the iteration
   */
  @Test
  public void test4() {
   LOGGER.finer("test4");
    it = pcyc1.getCycleButIterator(0);
    assertTrue(it.hasNext());
    assertEquals(2, it.next());
    assertTrue(it.hasNext());
    assertEquals(4, it.next());
    assertFalse(it.hasNext());
  }

  /**
   * A simple currentElement of a cycle iteration, starting from the last value in the cycle with no removals during the iteration
   */
  @Test
  public void test5() {
   LOGGER.finer("test5");
    it = pcyc1.getCycleButIterator(4);
    assertTrue(it.hasNext());
    assertEquals(0, it.next());
    assertTrue(it.hasNext());
    assertEquals(2, it.next());
    assertFalse(it.hasNext());
  }

  /**
   * A simple currentElement of a cycle iteration, destroying the end of cycle while iterating
   */
  @Test
  public void test6() {
   LOGGER.finer("test6");
    it = pcyc1.getCycleButIterator(2);
    assertTrue(it.hasNext());
    assertEquals(4, it.next());
    pcyc1.setOutOfCycle(0);
    assertFalse(it.hasNext());
  }

  /**
   * A simple currentElement of a cycle iteration, destroying several elements while iterating
   */
  @Test
  public void test7() {
   LOGGER.finer("test7");
    it = pcyc1.getCycleButIterator(1);
    assertTrue(it.hasNext());
    assertEquals(2, it.next());
    pcyc1.setOutOfCycle(2);
    assertTrue(it.hasNext());
    assertEquals(4, it.next());
    assertTrue(it.hasNext());
    pcyc1.setOutOfCycle(0);
    assertFalse(it.hasNext());
  }

  /**
   * A simple currentElement of a cycle iteration, destroying several elements while iterating
   */
  @Test
  public void test8() {
   LOGGER.finer("test8");
    it = pcyc1.getCycleButIterator(1);
    assertTrue(it.hasNext());
    assertEquals(2, it.next());
    pcyc1.setOutOfCycle(4);
    pcyc1.setOutOfCycle(0);
    assertFalse(it.hasNext());
  }

  /**
   * A simple currentElement of a cycle iteration, destroying several elements while iterating
   */
  @Test
  public void test9() {
   LOGGER.finer("test9");
    it = pcyc1.getCycleButIterator(-1);
    pcyc1.setOutOfCycle(2);
    pcyc1.setOutOfCycle(4);
    pcyc1.setOutOfCycle(0);
    assertFalse(it.hasNext());
  }

  /**
   * A simple currentElement where the cycle becomes empty after the first element is considered
   */
  @Test
  public void test10() {
   LOGGER.finer("test10");
    it = pcyc1.getCycleButIterator(-1);
    assertTrue(it.hasNext());
    assertEquals(0, it.next());
    pcyc1.setOutOfCycle(2);
    pcyc1.setOutOfCycle(4);
    assertFalse(it.hasNext());
  }


  /**
   * A simple currentElement with a singleton cycle, starting before it
   */
  @Test
  public void test11() {
   LOGGER.finer("test11");
    it = pcyc2.getCycleButIterator(2);
    assertTrue(it.hasNext());
    assertEquals(3, it.next());
    assertFalse(it.hasNext());
  }

  /**
   * A simple currentElement with a singleton cycle, starting after it and immediately destroying it
   */
  @Test
  public void test12() {
   LOGGER.finer("test12");
    it = pcyc2.getCycleButIterator(4);
    assertTrue(it.hasNext());
    pcyc2.setOutOfCycle(3);
    assertFalse(it.hasNext());
  }


  /**
   * A simple currentElement with a singleton cycle, starting on it
   */
  @Test
  public void test13() {
   LOGGER.finer("test13");
    it = pcyc2.getCycleButIterator(3);
    assertFalse(it.hasNext());
  }

}
