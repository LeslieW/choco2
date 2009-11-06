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
package choco.common;


import choco.kernel.common.opres.ssp.AbstractSubsetSumSolver;
import choco.kernel.common.opres.ssp.BellmanWithLists;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Arnaud Malapert
 *
 */
public class TestSubsetSum {

	protected void test(long[] capacities,long[] optimums,AbstractSubsetSumSolver... solvers) {
		for (AbstractSubsetSumSolver solver : solvers) {
			for (int i = 0; i < capacities.length; i++) {
				solver.setCapacity(capacities[i]);
				solver.reset();
				Assert.assertEquals("subset sum problem ",optimums[i], solver.run());
			}
		}
	}

	protected void launch(int[] sizes,long[] capacities,long[] optimums) {
		test(capacities,optimums, new BellmanWithLists(sizes,0));
	}


	@Test
	public void testSSPNoLoopNeeded() {
		int[] w={1,3,5,7,11,13};
		long[] c={9,27,40,45};
		long[] r={9,27,40,40};
		launch(w, c, r);
	}


	@Test
	public void testSSP1() {
		int[] v={2,6,8,10,16,20,27};
		long[] c={35,31,86};
		long[] r={35,30,83};
		launch(v,c,r);
	}



	@Test
	public void testSSP2()  {
		int[] v={3,6,9,12,15,18};
		long[] c={32,34,43,45,51};
		long[] r={30,33,42,45,51};
		launch(v,c,r);
	}


	@Test
	public void testSSP3()  {
		int[] v={7,14,21,28,35,42,49};
		long[] c={98,83,110,112,116};
		long[] r={98,77,105,112,112};
		launch(v,c,r);
	}

}
