package i_want_to_use_this_old_version_of_choco.set;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.Solver;
import i_want_to_use_this_old_version_of_choco.set.search.AssignSetVar;
import i_want_to_use_this_old_version_of_choco.set.search.RandomSetValSelector;
import i_want_to_use_this_old_version_of_choco.set.search.RandomSetVarSelector;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 29 janv. 2008
 * Time: 17:38:34
 */
public class SetUnionTest extends TestCase {

	Problem pb;

	protected void tearDown() throws Exception {
		System.out.printf(pb.pretty());
		pb = null;
		super.tearDown();
	}

	protected void setUp() throws Exception {
		super.setUp();
		pb = new Problem();
	}

	public void test() {
		for (int seed = 0; seed < 20; seed++) {
			SetVar v1 = pb.makeSetVar("v1", 4, 6);
			SetVar v2 = pb.makeSetVar("v2", 3, 5);
			SetVar v3 = pb.makeSetVar("v3", 1, 6);
			SetVar v4 = pb.makeSetVar("v4", 4, 4);

			pb.post(pb.setDisjoint(v1, v4));
			pb.post(pb.eqCard(v4, 1));
			Constraint c1 = pb.setUnion(v1, v2, v3);
			pb.post(c1);
			Solver sol = pb.getSolver();
			sol.setFirstSolution(false);
			sol.generateSearchSolver(pb);
			sol.addGoal(new AssignSetVar(new RandomSetVarSelector(pb,seed), new RandomSetValSelector(seed+1)));
			pb.getSolver().launch();

			assertTrue(32 == pb.getSolver().getNbSolutions());
		}
	}

}
