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
package choco.solver.search;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * J-CHOCO
 * Copyright (C) F. Laburthe, 1999-2003
 * <p/>
 * An open-source Constraint Programming Kernel
 * for Research and Education
 * <p/>
 * Created by: Guillaume on 3 nov. 2004
 */
public class RandomSearchTest {
  public static int nbQueensSolution[] = {0, 0, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712};

    @Test
  public void testNQueens() {
    //ChocoLogging.setVerbosity(Verbosity.VERBOSE);
    	int n = 8;
    Model m;
    IntegerVariable[] queens;
    m = new CPModel();
    queens = new IntegerVariable[n];
    for (int i = 0; i < n; i++) {
      queens[i] = makeIntVar("Q" + i, 1, n);
    }
    // diagonal constraints
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        int k = j - i;
        m.addConstraint(neq(queens[i], queens[j]));
        m.addConstraint(neq(queens[i], plus(queens[j], k)));
        m.addConstraint(neq(queens[i], minus(queens[j], k)));
      }
    }
    Solver s = new CPSolver();
        s.read(m);
    s.setVarIntSelector(new RandomIntVarSelector(s));
    s.setValIntSelector(new RandomIntValSelector());
    s.solveAll();
    if (n >= 4) {
      if (n <= 13) {
        assertEquals(Boolean.TRUE, s.isFeasible());
        assertEquals(nbQueensSolution[n], s.getNbSolutions());
      }
    } else {
      assertEquals(Boolean.FALSE, s.isFeasible());
    }
  }
}
