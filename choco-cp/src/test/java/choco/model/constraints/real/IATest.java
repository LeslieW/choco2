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
package choco.model.constraints.real;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.variables.real.RealInterval;
import choco.kernel.solver.variables.real.RealIntervalConstant;
import choco.kernel.solver.variables.real.RealMath;
import junit.framework.TestCase;

import java.util.Random;
import java.util.logging.Logger;

/**
 * J-CHOCO
 * Copyright (C) F. Laburthe, 1999-2003
 * <p/>
 * An open-source Constraint Programming Kernel
 * for Research and Education
 * <p/>
 * Created by: Guillaume on 18 juin 2004
 */
public class IATest extends TestCase {
    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

  RealInterval[] intervals = new RealInterval[3];
  public static int nbBox = 20;

  public void setUp() {
    Random rand = new Random();
    for (int i = 0; i < intervals.length; i++) {
      double a = rand.nextDouble() * 10 * (rand.nextBoolean() ? -1 : 1);
      double b = rand.nextDouble() * 10 * (rand.nextBoolean() ? -1 : 1);
      intervals[i] = new RealIntervalConstant(Math.min(a, b), Math.max(a, b));
    }
  }

  public void tearDown() {
    for (int i = 0; i < intervals.length; i++) {
      intervals[i] = null;
    }
  }

  public void testPlus() {
    RealInterval a = intervals[0];
    RealInterval b = intervals[1];
    LOGGER.info("Testing " + a + " + " + b);

    RealInterval res = RealMath.add(a, b);
    double aw = (a.getSup() - a.getInf()) / nbBox;
    double bw = (b.getSup() - b.getInf()) / nbBox;
    for (int i = 1; i < nbBox; i++) {
      double aa = a.getInf() + aw * i;
      for (int j = 1; j < nbBox; j++) {
        double bb = b.getInf() + bw * j;
        assertTrue(aa + bb > res.getInf());
        assertTrue(aa + bb < res.getSup());
      }
    }
  }

  public void testMinus() {
    RealInterval a = intervals[0];
    RealInterval b = intervals[1];
    LOGGER.info("Testing " + a + " - " + b);

    RealInterval res = RealMath.sub(a, b);
    double aw = (a.getSup() - a.getInf()) / nbBox;
    double bw = (b.getSup() - b.getInf()) / nbBox;
    for (int i = 1; i < nbBox; i++) {
      double aa = a.getInf() + aw * i;
      for (int j = 1; j < nbBox; j++) {
        double bb = b.getInf() + bw * j;
        assertTrue(aa - bb > res.getInf());
        assertTrue(aa - bb < res.getSup());
      }
    }
  }

  public void testMult() {
    RealInterval a = intervals[0];
    RealInterval b = intervals[1];
    LOGGER.info("Testing " + a + " * " + b);

    RealInterval res = RealMath.mul(a, b);
    double aw = (a.getSup() - a.getInf()) / nbBox;
    double bw = (b.getSup() - b.getInf()) / nbBox;
    for (int i = 1; i < nbBox; i++) {
      double aa = a.getInf() + aw * i;
      for (int j = 1; j < nbBox; j++) {
        double bb = b.getInf() + bw * j;
        assertTrue(aa * bb > res.getInf());
        assertTrue(aa * bb < res.getSup());
      }
    }
  }

  public void testDiv() {
    RealInterval a = intervals[0];
    RealInterval b = intervals[1];
    RealInterval c = intervals[2];
    LOGGER.info("Testing " + a + " / " + b + " in " + c);

    RealInterval res = RealMath.odiv_wrt(a, b, c);
    double aw = (a.getSup() - a.getInf()) / nbBox;
    double bw = (b.getSup() - b.getInf()) / nbBox;
    for (int i = 1; i < nbBox; i++) {
      double aa = a.getInf() + aw * i;
      for (int j = 1; j < nbBox; j++) {
        double bb = b.getInf() + bw * j;
        if (bb != 0) {
          assertTrue(aa / bb > res.getInf() || aa / bb > c.getSup() || aa / bb < c.getInf());
          assertTrue(aa / bb < res.getSup() || aa / bb > c.getSup() || aa / bb < c.getInf());
        }
      }
    }
  }

  public void testIPower() {
    int[] power = new int[]{2, 3, 4};
    RealInterval a = intervals[0];

      for (int p : power) {
          LOGGER.info("Testing " + a + " ** " + p);
          RealInterval res = RealMath.iPower(a, p);

          double aw = (a.getSup() - a.getInf()) / nbBox;
          for (int i = 1; i < nbBox; i++) {
              double aa = a.getInf() + aw * i;
              assertTrue(Math.pow(aa, p) > res.getInf());
              assertTrue(Math.pow(aa, p) < res.getSup());
          }
      }
  }

  /*public void testIRoot() {
    int[] power = new int[]{2,3,4};
    RealInterval a = intervals[0];
    RealInterval b = intervals[1];

    if (a.getInf() > 0) for (int powerIdx = 0; powerIdx < power.length; powerIdx++) {
      int p = power[powerIdx];
      LOGGER.info("Testing " + a + " ** 1/" + p);
      RealInterval res = RealMath.iRoot(a, p, b);

      double aw = (a.getSup() - a.getInf()) / nbBox;
      for(int i = 1; i < nbBox; i ++) {
        double aa = a.getInf() + aw * i;
        double calc = Math.pow(aa, 1/p);
        assertTrue(calc > res.getInf() || calc > b.getSup() || calc < b.getInf());
        assertTrue(calc < res.getSup() || calc > b.getSup() || calc < b.getInf());
      }
    }
  } */

  public void testSin() {
    RealInterval a = intervals[0];

    LOGGER.info("Testing sin(" + a + ")");
    RealInterval res = RealMath.sin(a);

    double aw = (a.getSup() - a.getInf()) / nbBox;
    for (int i = 1; i < nbBox; i++) {
      double aa = a.getInf() + aw * i;
      assertTrue(Math.sin(aa) > res.getInf());
      assertTrue(Math.sin(aa) < res.getSup());
    }
  }

  public void testCos() {
    RealInterval a = intervals[0];

    LOGGER.info("Testing cos(" + a + ")");
    RealInterval res = RealMath.cos(a);

    double aw = (a.getSup() - a.getInf()) / nbBox;
    for (int i = 1; i < nbBox; i++) {
      double aa = a.getInf() + aw * i;
      assertTrue(Math.cos(aa) > res.getInf());
      assertTrue(Math.cos(aa) < res.getSup());
    }
  }
}
