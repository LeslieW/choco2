package i_want_to_use_this_old_version_of_choco.real;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.real.constraint.MixedEqXY;
import i_want_to_use_this_old_version_of_choco.real.exp.RealIntervalConstant;
import junit.framework.TestCase;

/**
 * J-CHOCO
 * Copyright (C) F. Laburthe, 1999-2003
 * <p/>
 * An open-source Constraint Programming Kernel
 * for Research and Education
 * <p/>
 * Created by: Guillaume on 18 juin 2004
 */
public class MixedEqualityTests extends TestCase {
  Problem pb;
  RealVar v1;
  IntDomainVar v2;

  public void setUp() {
    pb = new Problem();
    v1 = pb.makeRealVar("v1", 0.0, 8.0);
    v2 = pb.makeEnumIntVar("v2", 2, 10);
    pb.post(new MixedEqXY(v1, v2));
  }

  public void tearDown() {
    pb = null;
    v1 = null;
    v2 = null;
  }

  public void testInt2Real() {
    try {
      pb.propagate();
      assertEquals(2.0, v1.getInf(), 1e-10);
      v2.setSup(6);
      pb.propagate();
      assertEquals(6.0, v1.getSup(), 1e-10);
    } catch (ContradictionException e) {
      assertTrue("The problem is consistent !", false);
    }
  }

  public void testReal2Int() {
    try {
      pb.propagate();
      assertEquals(8, v2.getSup());
      v1.intersect(new RealIntervalConstant(4.0, Double.POSITIVE_INFINITY));
      pb.propagate();
      assertEquals(4, v2.getInf());
    } catch (ContradictionException e) {
      assertTrue("The problem is consistent !", false);
    }
  }
}
