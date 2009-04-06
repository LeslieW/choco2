package i_want_to_use_this_old_version_of_choco.search;

import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.search.DomOverWDeg;
import i_want_to_use_this_old_version_of_choco.integer.search.MinDomain;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 11 avr. 2008
 * Time: 18:26:25
 * To change this template use File | Settings | File Templates.
 */
public class HeuristicTests extends TestCase {


	public void testDomWdeg() {
		int nb1 = testHeuristic(false);
		int nb2 = testHeuristic(true);
		System.out.println(nb1 + " " + nb2);
		assertTrue(nb1 > nb2);
	}

	public int testHeuristic(boolean domWdeg) {
		Problem pb = new Problem();
		IntDomainVar[] vars = pb.makeEnumIntVarArray("vtab",6,0,1);
		IntDomainVar[] vars2 = pb.makeEnumIntVarArray("vtab",5,0,3);

		for (int i = 0; i < vars2.length; i++) {
			for (int j = i + 1; j < vars2.length; j++) {
				pb.post(pb.neq(vars2[i],vars2[j]));
			}
		}
		if (domWdeg) {
			pb.getSolver().setVarIntSelector(new DomOverWDeg(pb));
		} else {
			pb.getSolver().setVarIntSelector(new MinDomain(pb));
		}
		pb.solve();
		assertTrue(!pb.isFeasible().booleanValue());
		return pb.getSolver().getSearchSolver().getNodeCount();
	}
}
