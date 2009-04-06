/** -------------------------------------------------
 *                   J-CHOCO
 *   Copyright (C) F. Laburthe, 1999-2003
 * --------------------------------------------------
 *    an open-source Constraint Programming Kernel
 *          for Research and Education
 * --------------------------------------------------
 *
 * file: choco.currentElement.util.PriorityQueueTest.java
 * last modified by Francois 28 ao�t 2003:14:59:09
 */
package i_want_to_use_this_old_version_of_choco.util;

import junit.framework.TestCase;

import java.util.logging.Logger;

public class PriorityQueueTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");

  public void setUp() {
    logger.fine("PriorityQueue Testing...");
  }

  protected void tearDown() {
  }

  public void test1() {
    logger.finer("test1");

    Entity obj1 = new Entity("Objet 1", 2);
    Entity obj2 = new Entity("Objet 2", 0);
    Entity obj3 = new Entity("Objet 3", 3);
    Entity obj4 = new Entity("Objet 4", 1);

    PriorityQueue queue = new PriorityQueue(4);

    Object[] ret;

    logger.finest("Step 1");
    queue.add(obj1);

    ret = queue.toArray();
    assertEquals(ret.length, 1);
    assertEquals(ret[0], obj1);

    logger.finest("Step 2");
    queue.add(obj2);

    ret = queue.toArray();
    assertEquals(ret.length, 2);
    assertEquals(ret[0], obj2);
    assertEquals(ret[1], obj1);

    logger.finest("Step 3");
    queue.add(obj3);

    ret = queue.toArray();
    assertEquals(ret.length, 3);
    assertEquals(ret[0], obj2);
    assertEquals(ret[1], obj1);
    assertEquals(ret[2], obj3);

    logger.finest("Step 4");
    queue.add(obj4);

    ret = queue.toArray();
    assertEquals(ret.length, 4);
    assertEquals(ret[0], obj2);
    assertEquals(ret[1], obj4);
    assertEquals(ret[2], obj1);
    assertEquals(ret[3], obj3);

    logger.finest("Step 5");
    obj3.priority = 1;
    queue.updatePriority(obj3);

    ret = queue.toArray();
    assertEquals(ret.length, 4);
    assertEquals(ret[0], obj2);
    assertEquals(ret[1], obj4);
    assertEquals(ret[2], obj3);
    assertEquals(ret[3], obj1);

    logger.finest("Step 6");
    obj2.priority = 3;
    queue.updatePriority(obj2);

    ret = queue.toArray();
    assertEquals(ret.length, 4);
    assertEquals(ret[0], obj4);
    assertEquals(ret[1], obj3);
    assertEquals(ret[2], obj1);
    assertEquals(ret[3], obj2);

    logger.finest("Step 7");
    Object obj = queue.popFirst();

    ret = queue.toArray();
    assertEquals(ret.length, 3);
    assertEquals(obj, obj4);
    assertEquals(ret[0], obj3);
    assertEquals(ret[1], obj1);
    assertEquals(ret[2], obj2);
  }

  private class Entity implements IPrioritizable {
    public String name;
    public int priority;

    public Entity(String name, int prio) {
      this.name = name;
      this.priority = prio;
    }

    public int getPriority() {
      return priority;
    }

    public String toString() {
      return this.name + " " + this.priority;
    }
  }
}
