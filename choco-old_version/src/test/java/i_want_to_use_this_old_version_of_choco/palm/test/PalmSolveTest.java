//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.test;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Solution;
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.IntVar;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.JumpProblem;
import i_want_to_use_this_old_version_of_choco.palm.PalmConstraint;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.PalmSolver;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.BetterConstraintComparator;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntDomain;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntVar;
import i_want_to_use_this_old_version_of_choco.palm.dbt.prop.PalmEngine;
import i_want_to_use_this_old_version_of_choco.palm.dbt.search.PalmGlobalSearchSolver;
import i_want_to_use_this_old_version_of_choco.palm.integer.constraints.PalmAssignment;
import i_want_to_use_this_old_version_of_choco.palm.integer.constraints.PalmElt;
import i_want_to_use_this_old_version_of_choco.palm.integer.constraints.PalmElt2D;
import i_want_to_use_this_old_version_of_choco.palm.integer.constraints.PalmGreaterOrEqualXYC;
import i_want_to_use_this_old_version_of_choco.search.AbstractGlobalSearchLimit;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;

public class PalmSolveTest extends TestCase {

  public void testBranchAndBoundMaximize() {
    PalmProblem myPb = new PalmProblem();
    IntDomainVar x = myPb.makeEnumIntVar("X", 1, 10);
    IntDomainVar y = myPb.makeEnumIntVar("Y", 1, 10);
    IntDomainVar z = myPb.makeEnumIntVar("Z", 1, 10);
    IntDomainVar obj = myPb.makeEnumIntVar("OBJ", 1, 100);
    myPb.post(myPb.geq(x, z));
    myPb.post(myPb.neq(x, z));

    myPb.post(myPb.geq(z, y));
    myPb.post(myPb.neq(z, y));

    myPb.post(myPb.neq(x, y));
    myPb.post(myPb.eq(myPb.scalar(new IntDomainVar[]{x, y, z}, new int[]{1, 1, -1}), obj));

    ((PalmProblem) myPb).maximize(obj, false);  // TODO
    int nbSolutions = myPb.getSolver().getNbSolutions();
    for (int i = 0; i < nbSolutions; i++) {
      Solution solution = ((PalmSolver) myPb.getSolver()).getSolution(i);
      System.out.println(solution);
    }

    System.out.println("nb Sol " + nbSolutions);

    assertEquals(9, ((PalmSolver) myPb.getSolver()).getOptimumValue().intValue());//getSolution(nbSolutions - 1).getVal(3));
  }

  public void testJumpBranchAndBoundMaximize() {
    JumpProblem myPb = new JumpProblem();
    IntDomainVar x = myPb.makeEnumIntVar("X", 1, 10);
    IntDomainVar y = myPb.makeEnumIntVar("Y", 1, 10);
    IntDomainVar z = myPb.makeEnumIntVar("Z", 1, 10);
    IntDomainVar obj = myPb.makeEnumIntVar("OBJ", 1, 100);
    myPb.post(myPb.geq(x, z));
    myPb.post(myPb.neq(x, z));

    myPb.post(myPb.geq(z, y));
    myPb.post(myPb.neq(z, y));

    myPb.post(myPb.neq(x, y));
    myPb.post(myPb.eq(myPb.scalar(new IntDomainVar[]{x, y, z}, new int[]{1, 1, -1}), obj));

    myPb.maximize(obj, false);  // TODO
    int nbSolutions = myPb.getSolver().getNbSolutions();
    System.out.println("nb Sol " + nbSolutions);

    assertEquals(9, myPb.getSolver().getOptimumValue().intValue());
  }

  public void testArithmetic() {
    PalmProblem myPb = new PalmProblem();
    IntDomainVar x = myPb.makeEnumIntVar("X", 1, 5);
    IntDomainVar y = myPb.makeBoundIntVar("Y", 1, 5);
    IntDomainVar z = myPb.makeEnumIntVar("Z", 1, 5);
    AbstractConstraint A = (AbstractConstraint) myPb.geq(x, myPb.plus(y, 1));
    AbstractConstraint B = (AbstractConstraint) myPb.geq(y, myPb.plus(z, 1));
    myPb.post(A);
    myPb.post(B);

    ((PalmConstraint) A).setPassive();
    ((PalmConstraint) A).setActive();

    try {
      myPb.propagate();
    } catch (ContradictionException e) {
      assertTrue(false);
    }

    assertEquals(3, x.getInf());
    assertEquals(5, x.getSup());
    assertEquals(2, y.getInf());
    assertEquals(4, y.getSup());
    assertEquals(1, z.getInf());
    assertEquals(3, z.getSup());

    Explanation expl = ((PalmProblem) myPb).makeExplanation();
    ((PalmIntVar) x).self_explain(PalmIntDomain.INF, expl);
    assertTrue(expl.contains(A));
    assertTrue(expl.contains(B));

    ((PalmEngine) myPb.getPropagationEngine()).remove(A);

    try {
      myPb.propagate();
    } catch (ContradictionException e) {
      assertTrue(false);
    }

    assertEquals(1, x.getInf());
    assertEquals(5, x.getSup());
    assertEquals(2, y.getInf());
    assertEquals(5, y.getSup());
    assertEquals(1, z.getInf());
    assertEquals(4, z.getSup());
  }

  public void testArithmetic2() {
    PalmProblem myPb = new PalmProblem();
    IntDomainVar x = myPb.makeBoundIntVar("X", 1, 5);
    IntDomainVar y = myPb.makeBoundIntVar("Y", 1, 5);
    IntDomainVar z = myPb.makeBoundIntVar("Z", 1, 5);
    Constraint A = myPb.geq(x, myPb.plus(y, 1));
    Constraint B = myPb.geq(y, myPb.plus(z, 1));
    myPb.post(A);
    myPb.post(B);

    ((PalmConstraint) A).setPassive();

    try {
      myPb.propagate();
    } catch (ContradictionException e) {
      assertTrue(false);
    }

    assertEquals(1, x.getInf());
    assertEquals(5, x.getSup());

    assertEquals(2, y.getInf());
    assertEquals(5, y.getSup());

    assertEquals(1, z.getInf());
    assertEquals(4, z.getSup());
  }

  public void testSolveArithmetic() {
    PalmProblem myPb = new PalmProblem();
    IntDomainVar x = myPb.makeBoundIntVar("X", 1, 5);
    IntDomainVar y = myPb.makeBoundIntVar("Y", 1, 5);
    IntDomainVar z = myPb.makeBoundIntVar("Z", 1, 5);
    Constraint A = myPb.geq(x, myPb.plus(y, 1));
    Constraint B = myPb.geq(y, myPb.plus(z, 1));
    myPb.post(A);
    myPb.post(B);

    myPb.solveAll();

    int nbSolutions = myPb.getSolver().getNbSolutions();

    System.out.println("Solve Solutions : " + nbSolutions);
    ((PalmProblem) myPb).printRuntimeSatistics(); // TODO
    assertEquals(10, nbSolutions);
  }

  public void testSolveArithmetic2() {
    PalmProblem myPb = new PalmProblem();
    IntDomainVar x = myPb.makeEnumIntVar("X", 1, 5);
    IntDomainVar y = myPb.makeBoundIntVar("Y", 1, 5);
    IntDomainVar z = myPb.makeEnumIntVar("Z", 1, 5);
    Constraint A = myPb.eq(x, y);
    Constraint B = myPb.eq(y, z);
    myPb.post(A);
    myPb.post(B);

    myPb.solveAll();

    int nbSolutions = myPb.getSolver().getNbSolutions();

    System.out.println("Solve2 Solutions : " + nbSolutions);
    ((PalmProblem) myPb).printRuntimeSatistics();
    assertEquals(5, nbSolutions);
  }

  public void testSolveArithmetic3() {
    PalmProblem myPb = new PalmProblem();
    IntDomainVar x = myPb.makeEnumIntVar("X", 1, 5);
    IntDomainVar y = myPb.makeEnumIntVar("Y", 1, 5);
    IntDomainVar z = myPb.makeBoundIntVar("Z", 1, 5);
    Constraint A = myPb.neq(x, y);
    Constraint B = myPb.neq(y, z);
    Constraint C = myPb.neq(x, z);
    myPb.post(A);
    myPb.post(B);
    myPb.post(C);

    myPb.solveAll();

    int nbSolutions = myPb.getSolver().getNbSolutions();

    System.out.println("Solve3 Solutions : " + nbSolutions);
    ((PalmProblem) myPb).printRuntimeSatistics();
    assertEquals(60, nbSolutions);
  }

  public void testLinComb() {
    PalmProblem myPb = new PalmProblem();
    IntDomainVar w = myPb.makeEnumIntVar("W", 1, 5);
    IntDomainVar x = myPb.makeBoundIntVar("X", 1, 5);
    IntDomainVar y = myPb.makeEnumIntVar("Y", 1, 5);
    IntDomainVar z = myPb.makeBoundIntVar("Z", 1, 5);
    Constraint A = myPb.eq(myPb.scalar(new int[]{1, 1, -1, -1}, new IntDomainVar[]{w, x, y, z}), 1);
    myPb.post(A);

    myPb.solveAll();

    int nbSolutions = myPb.getSolver().getNbSolutions();

    System.out.println("Lin Comb Solutions : " + nbSolutions);
    ((PalmProblem) myPb).printRuntimeSatistics();
    assertEquals(80, nbSolutions);
  }

  public void testElt() {
    PalmProblem myPb = new PalmProblem();
    IntDomainVar index = myPb.makeEnumIntVar("IndexVar", 0, 4);
    IntDomainVar value = myPb.makeEnumIntVar("ValueVar", 1, 10);
    Constraint A = new PalmElt(index, value, 0, new int[]{0, 2, 4, 5, 12});  //TODO : API pour poser la contrainte
    myPb.post(A);

    try {
      myPb.propagate();
    } catch (ContradictionException e) {
      assertTrue(false);
    }
    System.out.println("indexVar : " + index.getInf() + " - " + index.getSup());
    System.out.println("valueVar : " + value.getInf() + " - " + value.getSup());
    assertEquals(1, index.getInf());
    assertEquals(3, index.getSup());
    assertEquals(5, value.getSup());
    assertEquals(2, value.getInf());
    assertTrue(!value.canBeInstantiatedTo(3));

    myPb.solveAll();

    int nbSolutions = myPb.getSolver().getNbSolutions();

    for (int i = 0; i < nbSolutions; i++) {
      Solution solution = ((PalmSolver) myPb.getSolver()).getSolution(i);
      System.out.println(solution);
    }
    System.out.println("Element Solutions : " + nbSolutions);
    ((PalmProblem) myPb).printRuntimeSatistics();
    assertEquals(3, nbSolutions);
  }

  public void testElt2() {
    PalmProblem myPb = new PalmProblem();
    IntDomainVar index = myPb.makeEnumIntVar("IndexVar", 0, 3);
    IntDomainVar value1 = myPb.makeEnumIntVar("ValueVar1", 0, 1);
    IntDomainVar value2 = myPb.makeEnumIntVar("ValueVar2", 0, 1);
    IntDomainVar value3 = myPb.makeEnumIntVar("ValueVar3", 0, 1);
    IntDomainVar value4 = myPb.makeEnumIntVar("ValueVar4", 0, 1);
    Constraint A = new PalmElt(index, value1, 0, new int[]{1, 0, 0, 0});
    Constraint B = new PalmElt(index, value2, 0, new int[]{0, 1, 0, 0});
    Constraint C = new PalmElt(index, value3, 0, new int[]{0, 0, 1, 0});
    Constraint D = new PalmElt(index, value4, 0, new int[]{0, 0, 0, 1});
    myPb.post(A);
    myPb.post(B);
    myPb.post(C);
    myPb.post(D);

    myPb.solveAll();

    int nbSolutions = myPb.getSolver().getNbSolutions();
    for (int i = 0; i < nbSolutions; i++) {
      Solution solution = ((PalmSolver) myPb.getSolver()).getSolution(i);
      System.out.println(solution);
    }

    System.out.println("Element Solutions : " + nbSolutions);
    ((PalmProblem) myPb).printRuntimeSatistics();
    assertEquals(4, nbSolutions);
  }

  public void testElt2D() {
    PalmProblem myPb = new PalmProblem();
    IntDomainVar index1 = myPb.makeEnumIntVar("IndexVar1", 0, 2);
    IntDomainVar index2 = myPb.makeEnumIntVar("IndexVar2", 0, 3);
    IntDomainVar value = myPb.makeEnumIntVar("ValueVar", 0, 10);
    Constraint A = new PalmElt2D(index1, index2, value,
        new int[][]{{1, 2, 0}, {12, 1, 5}, {0, 6, 1}}, 3, 3);  //TODO : API pour poser la contrainte
    myPb.post(A);

    try {
      myPb.propagate();
    } catch (ContradictionException e) {
      assertTrue(false);
    }

    System.out.println("sup : " + value.getSup());
    assertEquals(6, value.getSup());
    myPb.solveAll();

    int nbSolutions = myPb.getSolver().getNbSolutions();
    for (int i = 0; i < nbSolutions; i++) {
      Solution solution = ((PalmSolver) myPb.getSolver()).getSolution(i);
      System.out.println(solution);
    }
    System.out.println("Element2D Solutions : " + nbSolutions);
    ((PalmProblem) myPb).printRuntimeSatistics();
    assertEquals(8, nbSolutions);
  }

  public void testSolveArithmeticBench() {
    PalmProblem myPb = new PalmProblem();
    int n = 5;
    IntDomainVar[] vars = new IntDomainVar[n];
    for (int i = 0; i < n; i++) {
      if (i % 2 == 0)
        vars[i] = myPb.makeBoundIntVar("X" + i, 1, n + 3);
      else
        vars[i] = myPb.makeEnumIntVar("X" + i, 1, n + 3);
    }
    for (int i = 0; i < n - 1; i++) {
      IntDomainVar var1 = vars[i];
      IntDomainVar var2 = vars[i + 1];
      myPb.post(myPb.geq(var1, myPb.plus(var2, 1)));
    }

    myPb.solveAll();

    int nbSolutions = myPb.getSolver().getNbSolutions();
    assertEquals(56, nbSolutions);
    System.out.println("Arith Bench -- Solutions : " + nbSolutions);
    ((PalmProblem) myPb).printRuntimeSatistics();
  }

  public void testNextSolutionAPI() {
    PalmProblem myPb = new PalmProblem();
    int n = 5;
    IntDomainVar[] vars = new IntDomainVar[n];
    for (int i = 0; i < n; i++) {
      if (i % 2 == 0)
        vars[i] = myPb.makeBoundIntVar("X" + i, 1, n + 3);
      else
        vars[i] = myPb.makeEnumIntVar("X" + i, 1, n + 3);
    }
    for (int i = 0; i < n - 1; i++) {
      IntDomainVar var1 = vars[i];
      IntDomainVar var2 = vars[i + 1];
      myPb.post(myPb.geq(var1, myPb.plus(var2, 1)));
    }

    myPb.solve();
    while (myPb.nextSolution() == Boolean.TRUE) {
    }

    int nbSolutions = myPb.getSolver().getNbSolutions();
    assertEquals(56, nbSolutions);
    System.out.println("Arith Bench -- Solutions : " + nbSolutions);
    ((PalmProblem) myPb).printRuntimeSatistics();
  }

  public void testComparator() {
    PalmProblem myPb = new PalmProblem();
    IntDomainVar x = myPb.makeBoundIntVar("X", 1, 5);
    IntDomainVar y = myPb.makeBoundIntVar("Y", 1, 5);
    IntDomainVar z = myPb.makeBoundIntVar("Z", 1, 5);
    Constraint A = new PalmGreaterOrEqualXYC(x, y, 1);
    Constraint B = new PalmGreaterOrEqualXYC(y, z, 1);
    Constraint C = new PalmAssignment(x, 2);
    myPb.post(A);
    myPb.post(B);
    myPb.post(C, 0);

    ArrayList list = new ArrayList();
    list.add(B);
    list.add(C);
    list.add(A);

    assertEquals(C, Collections.min(list, new BetterConstraintComparator()));
    assertEquals(A, Collections.max(list, new BetterConstraintComparator()));
  }

  public void testLimit() {
    PalmProblem pb = new PalmProblem();
    int n = 10;
    IntDomainVar[] vars = new IntDomainVar[10];
    for (int i = 0; i < vars.length; i++) {
      vars[i] = pb.makeBoundIntVar("v" + i, 1, n + 1);
      if (i > 0) {
        pb.post(pb.gt(vars[i], vars[i - 1]));
      }
    }

    pb.getSolver().setNodeLimit(2);
    pb.solveAll();
    assertEquals(2, ((AbstractGlobalSearchLimit)
        ((PalmGlobalSearchSolver) pb.getSolver().getSearchSolver()).
        getLimit(PalmGlobalSearchSolver.LIMIT_NODES)).getNbTot());
  }


  public static void testMagicSeries() {
    int n = 4;
    PalmProblem pb = new PalmProblem();
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


	public void testSimone() {
		PalmProblem pb = new PalmProblem();

		//Two integer variables, x and y
		IntVar chocovarx = pb.makeBoundIntVar(new String("?x"), 0, 50);
		IntVar chocovary = pb.makeBoundIntVar(new String("?y"), 0, 100);

		//x > 5
		IntConstraint c1 = (IntConstraint) pb.gt(chocovarx, 5);
		pb.post(c1);

		//y >= 10
		IntConstraint c2 = (IntConstraint) pb.geq(chocovary, 10);
		pb.post(c2);


		try {
			pb.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
		System.out.println("br " + chocovarx.pretty());
		System.out.println("br " + chocovary.pretty());

		//Removing the first constraint
		pb.remove(c1);
		//c1.delete();
		pb.eraseConstraint(c1);

		try {
			pb.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
		System.out.println("ar " + chocovarx.pretty());
		System.out.println("ar " + chocovary.pretty());

		//Adding a new constraint
		//x > 6
		IntConstraint c3 = (IntConstraint) pb.gt(chocovarx, 4);
		pb.post(c3);

		try {
			pb.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
		System.out.println("ar " + chocovarx.pretty());
		System.out.println("ar " + chocovary.pretty());
		
		System.out.println(pb.pretty());
	}
}
