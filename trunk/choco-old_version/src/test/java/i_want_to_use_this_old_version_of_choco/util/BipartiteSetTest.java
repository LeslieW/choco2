/** -------------------------------------------------
 *                   J-CHOCO
 *   Copyright (C) F. Laburthe, 1999-2003
 * --------------------------------------------------
 *    an open-source Constraint Programming Kernel
 *          for Research and Education
 * --------------------------------------------------
 *
 * file: choco.currentElement.util.BipartiteSetTest.java
 * last modified by Francois 28 ao�t 2003:14:59:09
 */
package i_want_to_use_this_old_version_of_choco.util;

import junit.framework.TestCase;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: Jul 4, 2003
 * Time: 10:07:46 AM
 * To change this template use Options | File Templates.
 */
public class BipartiteSetTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");

  public void setUp() {
    logger.fine("BipartiteSet Testing...");
  }

  protected void tearDown() {
  }

  public void test1() {
    logger.finer("test1");
    BipartiteSet set = new BipartiteSet();
    Object obj1 = new Object();
    Object obj2 = new Object();
    Object obj3 = new Object();

    set.addLeft(obj1);
    set.addLeft(obj2);
    set.addRight(obj3);

    assertEquals(2, set.getNbLeft());
    assertEquals(1, set.getNbRight());
    assertTrue(set.isLeft(obj1));
    logger.finest("First Step passed");

    set.moveRight(obj1);

    assertEquals(1, set.getNbLeft());
    assertEquals(2, set.getNbRight());
    assertTrue(set.isLeft(obj2));
    assertFalse(set.isLeft(obj1));
    assertFalse(set.isLeft(obj3));
    logger.finest("Second Step passed");

    set.moveAllLeft();

    assertEquals(3, set.getNbLeft());
    assertEquals(0, set.getNbRight());
    assertTrue(set.isLeft(obj1));
    assertTrue(set.isLeft(obj2));
    assertTrue(set.isLeft(obj3));
    logger.finest("Third Step passed");
  }
}
