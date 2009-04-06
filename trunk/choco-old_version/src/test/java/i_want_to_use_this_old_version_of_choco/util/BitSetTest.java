/** -------------------------------------------------
 *                   J-CHOCO
 *   Copyright (C) F. Laburthe, 1999-2003
 * --------------------------------------------------
 *    an open-source Constraint Programming Kernel
 *          for Research and Education
 * --------------------------------------------------
 *
 * file: choco.currentElement.util.BitSetTest.java
 * last modified by Francois 28 ao�t 2003:14:59:09
 */
package i_want_to_use_this_old_version_of_choco.util;

import junit.framework.TestCase;

import java.util.logging.Logger;

/**
 * a class implementing tests for bit sets management
 */
public class BitSetTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");

  public void setUp() {
    logger.fine("BitSet Testing...");
  }

  protected void tearDown() {
  }

  public void test1() {
    logger.finer("test1");
    assertTrue(BitSet.getBit(3, 0));
    assertTrue(BitSet.getBit(3, 1));
    for (int i = 3; i < 31; i++)
      assertFalse(BitSet.getBit(3, i));
  }

  public void test2() {
    logger.finer("test2");
    assertTrue(BitSet.getBit(21, 0));
    assertFalse(BitSet.getBit(21, 1));
    assertTrue(BitSet.getBit(21, 2));
    assertFalse(BitSet.getBit(21, 3));
    assertTrue(BitSet.getBit(21, 4));
    for (int i = 5; i < 31; i++)
      assertFalse(BitSet.getBit(21, i));
  }

  public void test3() {
    logger.finer("test3");
    assertTrue(BitSet.setBit(3, 0) == 3);
    assertTrue(BitSet.setBit(3, 1) == 3);
    assertTrue(BitSet.setBit(3, 2) == 7);
  }

  public void test4() {
    logger.finer("test4");
    assertTrue(BitSet.setBit(21, 0) == 21);
    assertTrue(BitSet.setBit(21, 1) == 23);
    assertTrue(BitSet.setBit(21, 2) == 21);
  }

}