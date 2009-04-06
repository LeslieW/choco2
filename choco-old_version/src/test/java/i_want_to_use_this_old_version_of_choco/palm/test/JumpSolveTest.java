package i_want_to_use_this_old_version_of_choco.palm.test;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.JumpProblem;
import junit.framework.TestCase;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class JumpSolveTest extends TestCase {

  public void testJump() {
    AbstractProblem pb = new JumpProblem();
    int n = 16;
    IntDomainVar[] vs = new IntDomainVar[n];
    for (int i = 0; i < vs.length; i++) {
      vs[i] = pb.makeEnumIntVar("" + i, 1, n / 2 - 1);
    }
    for (int i = 0; i < n / 2; i++)
      for (int j = 0; j < i; j++) {
        pb.post(pb.neq(vs[i], vs[j]));
      }
    pb.post(pb.neq(vs[0], vs[n / 2]));
    for (int i = n / 2; i < n; i++)
      for (int j = n / 2; j < i; j++) {
        pb.post(pb.neq(vs[i], vs[j]));
      }

    pb.solve();
    System.out.println("" + ((JumpProblem) pb).getContradictionExplanation());
    assertEquals(n / 2 * (n / 2 - 1) / 2, ((JumpProblem) pb).getContradictionExplanation().size());
    pb.printRuntimeSatistics();
    assertEquals(0, pb.getSolver().getNbSolutions());
  }

  public static void testMagicSeries() {
    int n = 4;
    AbstractProblem pb = new JumpProblem();
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
