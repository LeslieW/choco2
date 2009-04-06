package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.var.IntervalIntDomain;
import junit.framework.TestCase;

import java.util.logging.Logger;

// *********************************************
// *                   J-CHOCO                 *
// *   Copyright (c) F. Laburthe, 1999-2003    *
// *********************************************
// * Event-base contraint programming Engine   *
// *********************************************

// CVS Information
// File:               $RCSfile: IntervalIntDomainTest.java,v $
// Version:            $Revision: 1.4 $
// Last Modification:  $Date: 2007/07/16 15:17:33 $
// Last Contributor:   $Author: menana $

public class IntervalIntDomainTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private Problem pb;
  private IntDomainVar x, y;
  IntervalIntDomain yDom;

  protected void setUp() {
    logger.fine("BitSetIntDomain Testing...");
    pb = new Problem();
    x = pb.makeBoundIntVar("X", 1, 100);
    y = pb.makeBoundIntVar("Y", 1, 15);
    yDom = (IntervalIntDomain) y.getDomain();
  }

  protected void tearDown() {
    yDom = null;
    x = null;
    y = null;
    pb = null;
  }

  /**
   * testing read and write on bounds with backtracking
   */
  public void test1() {
    logger.finer("test1");

    assertEquals(1, yDom.getInf());
    assertEquals(15, yDom.getSup());
    assertEquals(15, yDom.getSize());
    logger.finest("First step passed");

    pb.worldPush();
    yDom.updateInf(2);
    yDom.updateInf(3);
    assertEquals(3, yDom.getInf());
    assertEquals(15, yDom.getSup());
    assertEquals(13, yDom.getSize());
    logger.finest("Second step passed");

    pb.worldPush();
    yDom.updateSup(13);
    yDom.updateInf(4);
    assertEquals(4, yDom.getInf());
    assertEquals(13, yDom.getSup());
    assertEquals(10, yDom.getSize());
    logger.finest("Third step passed");

    pb.worldPop();
    assertEquals(3, yDom.getInf());
    assertEquals(15, yDom.getSup());
    assertEquals(13, yDom.getSize());

    pb.worldPop();
    assertEquals(1, yDom.getInf());
    assertEquals(15, yDom.getSup());
    assertEquals(15, yDom.getSize());
  }

  /**
   * testing freeze and release for the delta domain
   */
  public void test2() {
    logger.finer("test2");

    yDom.freezeDeltaDomain();
    assertTrue(yDom.releaseDeltaDomain());

    yDom.updateInf(2);
    yDom.updateSup(12);
    yDom.freezeDeltaDomain();
    yDom.updateInf(3);
    assertFalse(yDom.releaseDeltaDomain());

    yDom.freezeDeltaDomain();
    assertTrue(yDom.releaseDeltaDomain());
  }

}
