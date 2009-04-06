package i_want_to_use_this_old_version_of_choco.igoals;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.goals.choice.Generate;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 20 mars 2008
 * Time: 08:35:12
 * To change this template use File | Settings | File Templates.
 */
public class SearchTests extends TestCase {



	public void testnode() {
	   int n1 = testNQueens(true);
	   int n2 = testNQueens(false);
	   assert(n1 == n2);
	 }

	 //return the number of nodes needed to solve the problem
	 private static int testNQueens(boolean withgoal) {
	   int NB_REINES = 8;

	   AbstractProblem pb = new Problem();


	   IntDomainVar[] vars = new IntDomainVar[NB_REINES];
	   for (int i = 0; i < NB_REINES; i++) {
	     vars[i] = pb.makeEnumIntVar("x" + i, 0, NB_REINES - 1);
	   }

	   for (int i = 0; i < NB_REINES; i++) {
	     for (int j = i + 1; j < NB_REINES; j++) {
	       pb.post(pb.neq(vars[i], vars[j]));
	     }
	   }

	   for (int i = 0; i < NB_REINES; i++) {
	     for (int j = i + 1; j < NB_REINES; j++) {
	       int k = j - i;
	       pb.post(pb.neq(vars[i], pb.plus(vars[j], k)));
	       pb.post(pb.neq(vars[i], pb.minus(vars[j], k)));
	     }
	   }

	   if (withgoal) {
	     pb.getSolver().setIlogGoal(new Generate(vars));
	   }

	   pb.solve();
	   while (pb.nextSolution()) {
	   }
	   System.out.println("Nb solutions = " + pb.getSolver().getNbSolutions());

	   pb.printRuntimeSatistics();
	   return pb.getSolver().getSearchSolver().getNodeCount();
	 }

}
