package i_want_to_use_this_old_version_of_choco.search;

import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.Solver;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntValSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntVarSelector;
import junit.framework.TestCase;

/**
 * J-CHOCO
 * Copyright (C) F. Laburthe, 1999-2003
 * <p/>
 * An open-source Constraint Programming Kernel
 * for Research and Education
 * <p/>
 * Created by: Guillaume on 3 nov. 2004
 */
public class RandomSearchTest extends TestCase {
  public static int nbQueensSolution[] = {0, 0, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712};

  public void testNQueens() {
    int n = 8;
    Problem pb;
    IntDomainVar[] queens;
    pb = new Problem();
    queens = new IntDomainVar[n];
    for (int i = 0; i < n; i++) {
      queens[i] = pb.makeEnumIntVar("Q" + i, 1, n);
    }
    // diagonal constraints
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        int k = j - i;
        pb.post(pb.neq(queens[i], queens[j]));
        pb.post(pb.neq(queens[i], pb.plus(queens[j], k)));
        pb.post(pb.neq(queens[i], pb.minus(queens[j], k)));
      }
    }
    Solver s = pb.getSolver();
    s.setVarSelector(new RandomIntVarSelector(pb));
    s.setValSelector(new RandomIntValSelector());
    pb.solveAll();
    if (n >= 4) {
      if (n <= 13) {
        assertEquals(Boolean.TRUE, pb.isFeasible());
        assertEquals(nbQueensSolution[n], s.getNbSolutions());
      }
    } else {
      assertEquals(Boolean.FALSE, pb.isFeasible());
    }
  }
}
