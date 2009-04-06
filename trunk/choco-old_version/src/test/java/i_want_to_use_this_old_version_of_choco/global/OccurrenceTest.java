package i_want_to_use_this_old_version_of_choco.global;


import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import junit.framework.TestCase;

import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class OccurrenceTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private Problem pb;
  private IntDomainVar x1, x2, x3, x4, x5, x6, x7, y1, x, y, n, m, xx;

  protected void setUp() {
    logger.fine("Occurrence Testing...");
    pb = new Problem();
    x = pb.makeBoundIntVar("x", 0, 2);
    xx = pb.makeBoundIntVar("xx", 1, 1);
    y = pb.makeBoundIntVar("y", 0, 2);
    n = pb.makeBoundIntVar("n", 0, 5);
    m = pb.makeBoundIntVar("m", 0, 5);
    x1 = pb.makeEnumIntVar("X1", 0, 10);
    x2 = pb.makeEnumIntVar("X2", 0, 10);
    x3 = pb.makeEnumIntVar("X3", 0, 10);
    x4 = pb.makeEnumIntVar("X4", 0, 10);
    x5 = pb.makeEnumIntVar("X5", 0, 10);
    x6 = pb.makeEnumIntVar("X6", 0, 10);
    x7 = pb.makeEnumIntVar("X7", 0, 10);
    y1 = pb.makeEnumIntVar("Y1", 0, 10);
  }

  protected void tearDown() {
    pb = null;
    x1 = x2 = x3 = x4 = x5 = x6 = x7 = y1 = x = y = n = m = xx = null;
  }

  /**
   * Simple currentElement: 5 equations on 4 variables: 1 single search solution that should be found by propagation
   */
  public void test1() {
    logger.finer("test1");
    try {
      pb.post(pb.occurrence(new IntDomainVar[]{x1, x2, x3, x4, x5, x6, x7}, 3, y1)); // OccurenceEq
      // pb.getPropagationEngine().getLogger().setVerbosity(choco.model.ILogger.TALK);
      x1.setVal(3);
      x2.setVal(3);
      x3.setVal(3);
      x4.remVal(3);
      x5.remVal(3);
      pb.propagate();
      assertTrue(y1.getInf() >= 3);
      assertTrue(y1.getSup() <= 5);
    } catch (ContradictionException e) {
      assertFalse(true);
    }
  }

  public void test2() {
    logger.finer("test2");
    try {
      pb.post(pb.occurrence(new IntDomainVar[]{x1, x2, x3}, 3, y1));
      pb.post(pb.occurrence(new IntDomainVar[]{x1, x5, x4, x6}, 4, y1));
      x1.setVal(3);
      y1.setInf(3);
      pb.propagate();
      assertTrue(x2.isInstantiatedTo(3));
      assertTrue(x3.isInstantiatedTo(3));
      assertTrue(x5.isInstantiatedTo(4));
      assertTrue(x4.isInstantiatedTo(4));
      assertTrue(x6.isInstantiatedTo(4));
    } catch (ContradictionException e) {
      assertFalse(true);
    }
  }

  public void test3() {
    logger.finer("test3 : first old choco currentElement");
    try {
      pb.post(pb.occurrence(new IntDomainVar[]{x, y}, 1, n));
      pb.post(pb.occurrence(new IntDomainVar[]{x, y}, 2, m));
      // pb.getPropagationEngine().getLogger().setVerbosity(choco.model.ILogger.TALK);
      pb.propagate();
      n.setVal(0);
      x.setSup(1);
      pb.propagate();
      assertTrue(x.getVal() == 0);
    } catch (ContradictionException e) {
      assertFalse(true);
    }
  }

  public void test4() {
    logger.finer("test3 : third old choco currentElement");
    try {
      pb.post(pb.occurrence(new IntDomainVar[]{xx, m}, 1, n));
      // pb.getPropagationEngine().getLogger().setVerbosity(choco.model.ILogger.TALK);
      pb.propagate();
      assertTrue(n.getInf() >= 1);
    } catch (ContradictionException e) {
      assertFalse(true);
    }
  }

  public static void testMagicSeries() {
    int n = 4;
    Problem pb = new Problem();
    IntDomainVar[] vs = new IntDomainVar[n];
    for (int i = 0; i < n; i++) {
      vs[i] = pb.makeEnumIntVar("" + i, 0, n - 1);
    }
    for (int i = 0; i < n; i++) {
      pb.post(pb.occurrence(vs, i, vs[i]));
    }
    pb.post(pb.eq(pb.sum(vs), n));     // contrainte redondante 1
    int[] coeff2 = new int[n - 1];
    IntDomainVar[] vs2 = new IntDomainVar[n - 1];
    for (int i = 1; i < n; i++) {
      coeff2[i - 1] = i;
      vs2[i - 1] = vs[i];
    }
    pb.post(pb.eq(pb.scalar(coeff2, vs2), n)); // contrainte redondante 2
    pb.solve();
    do {
      for (int i = 0; i < vs.length; i++) {
        System.out.print(vs[i].getVal() + " ");
      }
      System.out.println("");
    } while (pb.nextSolution() == Boolean.TRUE);
    assertEquals(2, pb.getSolver().getNbSolutions());
  }
}
