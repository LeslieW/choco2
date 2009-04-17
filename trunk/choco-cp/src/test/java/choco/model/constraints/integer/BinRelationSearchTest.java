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
package choco.model.constraints.integer;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerVariable;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.ArrayList;
import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class BinRelationSearchTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

  public static int nbQueensSolution[] = {0, 0, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712};


    @Test
  public void testNQueen1AC3() {
    queen0(9, 3, false);
  }

    @Test
  public void testNQueen1AC322() {
    queen0(9, 322, false);
  }

    @Test
  public void testNQueen1AC2001() {
    queen0(9, 2001, false);
  }

    @Test
  public void testNQueen1AC3rm() {
    queen0(9, 32, false);
  }


  private void queen0(int n, int ac, boolean nary) {
    boolean[][] matriceNeq = new boolean[n][n];
    ArrayList<int[]> tuples = new ArrayList<int[]>();
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++) {
        if (i == j) {
          matriceNeq[i][j] = false;
        } else {
          tuples.add(new int[]{i+1,j+1});
	      matriceNeq[i][j] = true;
        }
      }

    // create variables
    CPModel m = new CPModel();
    IntegerVariable[] queens = new IntegerVariable[n];
    for (int i = 0; i < n; i++) {
      queens[i] = makeIntVar("Q" + i, 1, n);
    }
    // diagonal constraints
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        int k = j - i;
        if (nary)
            m.addConstraint(feasTupleAC(tuples, queens[i], queens[j]));
	    else m.addConstraint(feasPairAC("cp:ac" + ac, queens[i], queens[j], matriceNeq));
        boolean[][] matriceNeqDec1 = new boolean[n][n];
        for (int z = 0; z < n; z++)
          for (int w = 0; w < n; w++) {
              matriceNeqDec1[z][w] = z != (w - k);
          }
        m.addConstraint(feasPairAC("cp:ac" + ac,queens[i], queens[j], matriceNeqDec1));   // pb.plus(queens[j], k)
        boolean[][] matriceNeqDec2 = new boolean[n][n];
        for (int z = 0; z < n; z++)
          for (int w = 0; w < n; w++) {
              matriceNeqDec2[z][w] = z != (w + k);
          }
        m.addConstraint(feasPairAC("cp:ac" + ac,queens[i], queens[j], matriceNeqDec2));  // pb.minus(queens[j], k)
      }
    }
    CPSolver s = new CPSolver();
      s.read(m);
      long time = System.currentTimeMillis();
      s.solveAll();
      time = System.currentTimeMillis() - time;
    assertEquals(nbQueensSolution[n], s.getNbSolutions());
    LOGGER.info("nb SolTh : " + nbQueensSolution[n] + " nb SolReal : " + s.getNbSolutions()+ " in " + (int) time + " ms with ac " + ac);
  }
}
