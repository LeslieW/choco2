package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.Solver;
import junit.framework.TestCase;

import java.util.ArrayList;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class BinRelationSearchTest extends TestCase {

  public static int nbQueensSolution[] = {0, 0, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712};


  public void testNQueen1AC3() {
    queen0(9, 3, false);
  }

  public void testNQueen1AC4() {
    queen0(9, 4, false);
  }

  public void testNQueen1AC2001() {
    queen0(9, 2001, false);
  }

  public void testNQueen1AC3rm() {
    queen0(9, 32, false);
  }

  public void testNQueen1GAC3rm() {
	  queen0(9, 32, true);
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
    Problem pb = new Problem();
    IntDomainVar[] queens = new IntDomainVar[n];
    for (int i = 0; i < n; i++) {
      queens[i] = pb.makeEnumIntVar("Q" + i, 1, n);
    }
    // diagonal constraints
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        int k = j - i;
        if (nary)
            pb.post(pb.feasTupleAC(new IntVar[]{queens[i], queens[j]}, tuples));
	    else pb.post(pb.feasPairAC(queens[i], queens[j], matriceNeq, ac));
        boolean[][] matriceNeqDec1 = new boolean[n][n];
        for (int z = 0; z < n; z++)
          for (int w = 0; w < n; w++) {
            if (z == (w - k))
              matriceNeqDec1[z][w] = false;
            else
              matriceNeqDec1[z][w] = true;
          }
        pb.post(pb.feasPairAC(queens[i], queens[j], matriceNeqDec1, ac));   // pb.plus(queens[j], k)
        boolean[][] matriceNeqDec2 = new boolean[n][n];
        for (int z = 0; z < n; z++)
          for (int w = 0; w < n; w++) {
            if (z == (w + k))
              matriceNeqDec2[z][w] = false;
            else
              matriceNeqDec2[z][w] = true;
          }
        pb.post(pb.feasPairAC(queens[i], queens[j], matriceNeqDec2, ac));  // pb.minus(queens[j], k)
      }
    }
    Solver s = pb.getSolver();
    long time = System.currentTimeMillis();
	pb.solveAll();
	time = System.currentTimeMillis() - time;
    assertEquals(nbQueensSolution[n], s.getNbSolutions());

	System.out.println("nb SolTh : " + nbQueensSolution[n] + " nb SolReal : " + s.getNbSolutions() + " in " + (int) time + " ms with ac " + ac + (nary ? " nary" : ""));
  }
}
