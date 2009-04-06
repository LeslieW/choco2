package i_want_to_use_this_old_version_of_choco.util;

import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.trailing.EnvironmentTrailing;
import junit.framework.TestCase;

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

public class StoredPointerCycleTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private IEnvironment env;
  private StoredPointerCycle pcyc1;
  private StoredPointerCycle pcyc2;
  private IntIterator it;

  public void setUp() {
    logger.fine("StoredPointerCycle Testing...");
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

  protected void tearDown() {
    it = null;
    pcyc1 = null;
    pcyc2 = null;
    env = null;
  }

  /**
   * A simple currentElement of full cycle iteration with no removals during the iteration
   */
  public void test1() {
    logger.finer("test1");
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
  public void test2() {
    logger.finer("test2");
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
  public void test3() {
    logger.finer("test3");
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
  public void test4() {
    logger.finer("test4");
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
  public void test5() {
    logger.finer("test5");
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
  public void test6() {
    logger.finer("test6");
    it = pcyc1.getCycleButIterator(2);
    assertTrue(it.hasNext());
    assertEquals(4, it.next());
    pcyc1.setOutOfCycle(0);
    assertFalse(it.hasNext());
  }

  /**
   * A simple currentElement of a cycle iteration, destroying several elements while iterating
   */
  public void test7() {
    logger.finer("test7");
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
  public void test8() {
    logger.finer("test8");
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
  public void test9() {
    logger.finer("test9");
    it = pcyc1.getCycleButIterator(-1);
    pcyc1.setOutOfCycle(2);
    pcyc1.setOutOfCycle(4);
    pcyc1.setOutOfCycle(0);
    assertFalse(it.hasNext());
  }

  /**
   * A simple currentElement where the cycle becomes empty after the first element is considered
   */
  public void test10() {
    logger.finer("test10");
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
  public void test11() {
    logger.finer("test11");
    it = pcyc2.getCycleButIterator(2);
    assertTrue(it.hasNext());
    assertEquals(3, it.next());
    assertFalse(it.hasNext());
  }

  /**
   * A simple currentElement with a singleton cycle, starting after it and immediately destroying it
   */
  public void test12() {
    logger.finer("test12");
    it = pcyc2.getCycleButIterator(4);
    assertTrue(it.hasNext());
    pcyc2.setOutOfCycle(3);
    assertFalse(it.hasNext());
  }


  /**
   * A simple currentElement with a singleton cycle, starting on it
   */
  public void test13() {
    logger.finer("test13");
    it = pcyc2.getCycleButIterator(3);
    assertFalse(it.hasNext());
  }

}
