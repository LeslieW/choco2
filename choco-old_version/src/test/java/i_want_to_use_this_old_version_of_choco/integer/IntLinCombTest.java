// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/* File choco.currentElement.search.IntLinCombTest.java, last modified by Francois 27 sept. 2003 12:08:59 */
package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import junit.framework.TestCase;

import java.util.logging.Logger;

public class IntLinCombTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private Problem pb;
  private IntDomainVar x1, x2, x3, x4, x5, x6, x7, y1, y2;

  protected void setUp() {
    logger.fine("IntLinComb Testing...");
    pb = new Problem();
    x1 = pb.makeBoundIntVar("X1", 0, 10);
    x2 = pb.makeBoundIntVar("X2", 0, 10);
    x3 = pb.makeBoundIntVar("X3", 0, 10);
    x4 = pb.makeBoundIntVar("X3", 0, 10);
    x5 = pb.makeBoundIntVar("X3", 0, 10);
    x6 = pb.makeBoundIntVar("X3", 0, 10);
    x7 = pb.makeBoundIntVar("X3", 0, 10);
    y1 = pb.makeBoundIntVar("Y1", 0, 10);
    y2 = pb.makeBoundIntVar("Y2", 0, 50);
  }

  protected void tearDown() {
    x1 = null;
    x2 = null;
    x3 = null;
    x4 = null;
    x5 = null;
    x6 = null;
    x7 = null;
    y1 = null;
    y2 = null;
    pb = null;
  }

  /**
   * Simple currentElement: 5 equations on 4 variables: 1 single search solution that should be found by propagation
   */
  public void test1() {
    logger.finer("test1");
    try {
      pb.post(pb.eq(pb.scalar(new int[]{3, 7, 9, -1}, new IntDomainVar[]{x1, x2, x3, y1}), 68));
      pb.post(pb.eq(pb.scalar(new int[]{5, 2, 8, -1}, new IntDomainVar[]{x1, x2, x3, y1}), 44));
      pb.post(pb.eq(pb.scalar(new int[]{3, 12, 2, -1}, new IntDomainVar[]{x1, x2, x3, y1}), 72));
      pb.post(pb.eq(pb.scalar(new int[]{15, 4, 1, -1}, new IntDomainVar[]{x1, x2, x3, y1}), 53));
      pb.post(pb.eq(pb.scalar(new int[]{12, 7, 9, -1}, new IntDomainVar[]{x1, x2, x3, y1}), 86));
      // pb.getPropagationEngine().getLogger().setVerbosity(choco.model.ILogger.TALK);
      pb.propagate();
      assertTrue(x1.isInstantiated());
      assertTrue(x2.isInstantiated());
      assertTrue(x3.isInstantiated());
      assertTrue(y1.isInstantiated());
      assertEquals(2, x1.getVal());
      assertEquals(5, x2.getVal());
      assertEquals(3, x3.getVal());
      assertEquals(0, y1.getVal());
    } catch (ContradictionException e) {
      assertFalse(true);
    }
  }

  /**
   * Exact same currentElement as test1, but expressed with binary +/x operators instead of "scalar" operator
   */
  public void test2() {
    logger.finer("test2");
    try {
      pb.post(pb.eq(pb.plus(pb.plus(pb.plus(pb.mult(3, x1), pb.mult(7, x2)), pb.mult(9, x3)), pb.mult(-1, y1)), 68));
      pb.post(pb.eq(pb.plus(pb.plus(pb.plus(pb.mult(5, x1), pb.mult(2, x2)), pb.mult(8, x3)), pb.mult(-1, y1)), 44));
      pb.post(pb.eq(pb.plus(pb.plus(pb.plus(pb.mult(3, x1), pb.mult(12, x2)), pb.mult(2, x3)), pb.mult(-1, y1)), 72));
      pb.post(pb.eq(pb.plus(pb.plus(pb.plus(pb.mult(15, x1), pb.mult(4, x2)), pb.mult(1, x3)), pb.mult(-1, y1)), 53));
      pb.post(pb.eq(pb.plus(pb.plus(pb.plus(pb.mult(12, x1), pb.mult(7, x2)), pb.mult(9, x3)), pb.mult(-1, y1)), 86));
      // pb.getPropagationEngine().getLogger().setVerbosity(choco.model.ILogger.TALK);
      pb.propagate();
      assertTrue(x1.isInstantiated());
      assertTrue(x2.isInstantiated());
      assertTrue(x3.isInstantiated());
      assertTrue(y1.isInstantiated());
      assertEquals(2, x1.getVal());
      assertEquals(5, x2.getVal());
      assertEquals(3, x3.getVal());
      assertEquals(0, y1.getVal());
    } catch (ContradictionException e) {
      assertFalse(true);
    }
  }

  /**
   * Another currentElement: 5 equations on 4 variables: the solution is found step by step
   */
  public void test3() {
    logger.finer("test3");
    try {
      x1.setInf(1);
      x2.setInf(1);
      x3.setInf(1);
      y1.setInf(1);
      pb.post(pb.eq(pb.scalar(new int[]{1, 3, 5, -1}, new IntDomainVar[]{x1, x2, x3, y1}), 23));
      pb.post(pb.eq(pb.scalar(new int[]{2, 10, 1, -1}, new IntDomainVar[]{x1, x2, x3, y2}), 14));
      pb.post(pb.eq(pb.scalar(new int[]{7, -1}, new IntDomainVar[]{y1, y2}), 0));
      pb.propagate();
      assertEquals(1, x1.getInf());
      assertEquals(10, x1.getSup());
      assertEquals(1, x2.getInf());
      assertEquals(6, x2.getSup());
      assertEquals(1, x3.getInf());
      assertEquals(5, x3.getSup());
      assertEquals(1, y1.getInf());
      assertEquals(7, y1.getSup());
      assertEquals(7, y2.getInf());
      assertEquals(49, y2.getSup());
      x1.setInf(7);
      pb.propagate();
      assertEquals(7, x1.getInf());
      assertEquals(10, x1.getSup());
      assertEquals(1, x2.getInf());
      assertEquals(4, x2.getSup());
      assertEquals(1, x3.getInf());
      assertEquals(4, x3.getSup());
      assertEquals(2, y1.getInf());
      assertEquals(7, y1.getSup());
      assertEquals(14, y2.getInf());
      assertEquals(49, y2.getSup());
      x3.setSup(2);
      pb.propagate();
      assertEquals(7, x1.getInf());
      assertEquals(10, x1.getSup());
      assertEquals(2, x2.getInf());
      assertEquals(4, x2.getSup());
      assertEquals(1, x3.getInf());
      assertEquals(2, x3.getSup());
      assertEquals(3, y1.getInf());
      assertEquals(6, y1.getSup());
      assertEquals(21, y2.getInf());
      assertEquals(42, y2.getSup());
      y2.setInf(30);
      pb.propagate();
      assertEquals(7, x1.getInf());
      assertEquals(10, x1.getSup());
      assertEquals(3, x2.getInf());
      assertEquals(4, x2.getSup());
      assertTrue(x3.isInstantiated());
      assertEquals(2, x3.getVal());
      assertEquals(5, y1.getInf());
      assertEquals(6, y1.getSup());
      assertEquals(35, y2.getInf());
      assertEquals(42, y2.getSup());
      x2.setInf(4);
      pb.propagate();
/*
      Logger.getLogger("choco").setLevel(Level.FINEST);
      Logger.getLogger("choco.var").setLevel(Level.FINEST);
      Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search").setLevel(Level.FINEST);
      Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search.IntDomainVar").setLevel(Level.FINEST);
      Logger.getLogger("choco.currentElement").setLevel(Level.FINEST);
      Logger.getLogger("choco.currentElement.search").setLevel(Level.FINEST);
      Logger.getLogger("choco.currentElement.search.IntLinCombTest").setLevel(Level.FINEST);
      pb.propagate();
*/
      assertTrue(x1.isInstantiated());
      assertTrue(x2.isInstantiated());
      assertTrue(x3.isInstantiated());
      assertTrue(y1.isInstantiated());
      assertTrue(y2.isInstantiated());
      assertEquals(7, x1.getVal());
      assertEquals(4, x2.getVal());
      assertEquals(2, x3.getVal());
      assertEquals(6, y1.getVal());
      assertEquals(42, y2.getVal());
    } catch (ContradictionException e) {
      assertFalse(true);
    }
  }

  /**
   * Slightly larger currentElement: 10 equations on 7 variables: 1 single search solution that should be found by propagation
   */
  public void test4() {
    try {
      pb.post(pb.eq(pb.scalar(new int[]{98527, 34588, 5872, 59422, 65159, -30704, -29649}, new IntDomainVar[]{x1, x2, x3, x5, x7, x4, x6}), 1547604));
      pb.post(pb.eq(pb.scalar(new int[]{98957, 83634, 69966, 62038, 37164, 85413, -93989}, new IntDomainVar[]{x2, x3, x4, x5, x6, x7, x1}), 1823553));
      pb.post(pb.eq(pb.scalar(new int[]{10949, 77761, 67052, -80197, -61944, -92964, -44550}, new IntDomainVar[]{x1, x2, x5, x3, x4, x6, x7}), -900032));
      pb.post(pb.eq(pb.scalar(new int[]{73947, 84391, 81310, -96253, -44247, -70582, -33054}, new IntDomainVar[]{x1, x3, x5, x2, x4, x6, x7}), 1164380));
      pb.post(pb.eq(pb.scalar(new int[]{13057, 42253, 77527, 96552, -60152, -21103, -97932}, new IntDomainVar[]{x3, x4, x5, x7, x1, x2, x6}), 1185471));
      pb.post(pb.eq(pb.scalar(new int[]{66920, 55679, -64234, -65337, -45581, -67707, -98038}, new IntDomainVar[]{x1, x4, x2, x3, x5, x6, x7}), -1394152));
      pb.post(pb.eq(pb.scalar(new int[]{68550, 27886, 31716, 73597, 38835, -88963, -76391}, new IntDomainVar[]{x1, x2, x3, x4, x7, x5, x6}), 279091));
      pb.post(pb.eq(pb.scalar(new int[]{76132, 71860, 22770, 68211, 78587, -48224, -82817}, new IntDomainVar[]{x2, x3, x4, x5, x6, x1, x7}), 480923));
      pb.post(pb.eq(pb.scalar(new int[]{94198, 87234, 37498, -71583, -25728, -25495, -70023}, new IntDomainVar[]{x2, x3, x4, x1, x5, x6, x7}), -519878));
      pb.post(pb.eq(pb.scalar(new int[]{78693, 38592, 38478, -94129, -43188, -82528, -69025}, new IntDomainVar[]{x1, x5, x6, x2, x3, x4, x7}), -361921));
      // pb.getPropagationEngine().getLogger().setVerbosity(choco.model.ILogger.DEBUG);

      pb.propagate();
      assertEquals(0, x1.getInf());
      assertEquals(10, x1.getSup());
      assertEquals(0, x2.getInf());
      assertEquals(10, x2.getSup());
      assertEquals(0, x3.getInf());
      assertEquals(10, x3.getSup());
      assertEquals(0, x4.getInf());
      assertEquals(10, x4.getSup());
      assertEquals(0, x5.getInf());
      assertEquals(10, x5.getSup());
      assertEquals(0, x6.getInf());
      assertEquals(10, x6.getSup());
      assertEquals(0, x7.getInf());
      assertEquals(10, x7.getSup());
      x1.setInf(6);
      pb.propagate();
      assertEquals(6, x1.getInf());
      assertEquals(1, x5.getInf());
      assertEquals(7, x6.getSup());
      assertEquals(3, x7.getInf());
      x3.setInf(8);
      pb.propagate();
      assertEquals(6, x1.getInf());
      assertEquals(6, x2.getSup());
      assertEquals(8, x3.getInf());
      x4.setInf(4);
      pb.propagate();
      assertEquals(5, x2.getSup());
      assertEquals(4, x4.getInf());
      assertEquals(4, x5.getInf());
      assertEquals(4, x7.getInf());
      x5.setInf(9);
      pb.propagate();
      assertEquals(4, x2.getSup());
      assertEquals(9, x5.getInf());
      assertEquals(6, x6.getSup());
      x6.setInf(3);
      pb.propagate();
      assertEquals(8, x1.getSup());
      assertEquals(2, x2.getSup());
      assertEquals(7, x4.getSup());
      assertEquals(3, x6.getInf());
      assertEquals(5, x6.getSup());
      assertEquals(8, x7.getInf());
      x7.setInf(9);
      pb.propagate();
      assertTrue(x1.isInstantiated());
      assertTrue(x2.isInstantiated());
      assertTrue(x3.isInstantiated());
      assertTrue(x4.isInstantiated());
      assertTrue(x5.isInstantiated());
      assertTrue(x6.isInstantiated());
      assertTrue(x7.isInstantiated());
      assertEquals(6, x1.getVal());
      assertEquals(0, x2.getVal());
      assertEquals(8, x3.getVal());
      assertEquals(4, x4.getVal());
      assertEquals(9, x5.getVal());
      assertEquals(3, x6.getVal());
      assertEquals(9, x7.getVal());
    } catch (ContradictionException e) {
      assertFalse(true);
    }
  }

  public void test5() {
    Problem myProb = new Problem();
    IntDomainVar a = myProb.makeEnumIntVar("a", 0, 4);
    myProb.post(myProb.eq(myProb.plus(a, 1), 2));
    myProb.solve();
    assertEquals(1, a.getVal());
    System.out.println("Variable " + a + " = " + a.getVal());
  }


  public void test6() {
    Problem myProb = new Problem();
    IntDomainVar[] a = myProb.makeEnumIntVarArray("a",4, 0, 4);
    int[] zeroCoef = new int[4];
    myProb.post(myProb.leq(myProb.scalar(a, zeroCoef), 2));
    myProb.solve();
    assertTrue(myProb.isFeasible());
  }

  public void test7() {
    Problem myProb = new Problem();
    IntDomainVar[] a = myProb.makeEnumIntVarArray("a",4, 0, 4);
    int[] zeroCoef = new int[4];
    myProb.post(myProb.geq(myProb.scalar(a, zeroCoef), 2));
    myProb.post(myProb.geq(myProb.scalar(a, zeroCoef), 10));
    myProb.post(myProb.geq(myProb.scalar(a, zeroCoef), 1));  
    myProb.solve();
    assertTrue(!myProb.isFeasible());
  }

   public void test8() {
    Problem myProb = new Problem();
    IntDomainVar[] a = myProb.makeEnumIntVarArray("a",4, 0, 4);
    int[] zeroCoef = new int[4];
    myProb.post(myProb.geq(myProb.scalar(a, zeroCoef), -2));
    myProb.post(myProb.geq(myProb.scalar(a, zeroCoef), -3));
    myProb.solve();
    assertTrue(myProb.isFeasible());
  }

   public void test9() {
    Problem myProb = new Problem();
    IntDomainVar[] a = myProb.makeEnumIntVarArray("a",4, 0, 4);
    int[] zeroCoef = new int[4];
    myProb.post(myProb.eq(myProb.scalar(a, zeroCoef), 0));
    myProb.solve();
    assertTrue(myProb.isFeasible());
  }
}

/*
(     choco/post(p, list(98527, 34588, 5872, 59422, 65159, -30704, -29649) scalar list(x1,x2,x3,x5,x7,x4,x6) + 1547604),
      choco/post(p, list(98957, 83634, 69966, 62038, 37164, 85413, -93989) scalar list(x2,x3,x4,x5,x6,x7,x1) ==  1823553),
      choco/post(p, list(10949, 77761, 67052, -80197, -61944, -92964, -44550) scalar list(x1,x2,x5,x3,x4,x6,x7) == -900032),
      choco/post(p, list(73947, 84391, 81310, -96253, -44247, -70582, -33054) scalar list(x1,x3,x5,x2,x4,x6,x7) == 1164380),
      choco/post(p, list(13057, 42253, 77527, 96552, -60152, -21103, -97932) scalar list(x3,x4,x5,x7,x1,x2,x6) == 1185471),
      choco/post(p, list(66920, 55679, -64234, -65337, -45581, -67707, -98038) scalar list(x1,x4,x2,x3,x5,x6,x7) == -1394152),
      choco/post(p, list(68550, 27886, 31716, 73597, 38835, -88963, -76391) scalar list(x1,x2,x3,x4,x7,x5,x6) == 279091),
      choco/post(p, list(76132, 71860, 22770, 68211, 78587, -48224, -82817) scalar list(x2,x3,x4,x5,x6,x1,x7) == 480923),
      choco/post(p, list(94198, 87234, 37498, -71583, -25728, -25495, -70023) scalar list(x2,x3,x4,x1,x5,x6,x7) == -519878),
      choco/post(p, list(78693, 38592, 38478, -94129, -43188, -82528, -69025) scalar list(x1,x5,x6,x2,x3,x4,x7) == -361921),
   */