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
package choco.scheduling;

import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import junit.framework.Assert;
import org.junit.Test;
import samples.scheduling.OpenShopExample;

import java.util.logging.Logger;

/**
 * @author Arnaud Malapert
 *
 */
public class TestBranching {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

	public final static int NB_TESTS=4;


	private void compare(CPSolver[] solvers) {
		//CPSolver.setVerbosity(CPSolver.SEARCH);
		int obj=-1;
		int n=0;
		for (int i = 0; i < solvers.length; i++) {
			CPSolver s=solvers[i];
			s.generateSearchStrategy();
			//LOGGER.info(s.pretty());
			s.launch();
			if(! s.isEncounteredLimit()) {
				if(obj==-1) { obj =s.getObjectiveValue().intValue();}
				n++;
			}
			SchedUtilities.message("solver "+i, s.getObjectiveValue(), s);
		}
		//check
		if(n==0) {LOGGER.severe("no optimal found");}
		else {
			if(n==1) {LOGGER.severe("only one optimal solution");}
			for (int i = 0; i < solvers.length; i++) {
				if(solvers[i].isEncounteredLimit()) {
					Assert.assertTrue("solver "+i, solvers[i].getObjectiveValue().intValue() >= obj );
				}else {
					Assert.assertEquals("solver "+i, obj,solvers[i].getObjectiveValue().intValue() );
				}
			}
		}
		LOGGER.info("optimum found: "+n+"/"+solvers.length);
	}



	public void test(int nbTests, OpenShopExample example) {
		for (int i = 0; i < nbTests; i++) {
			example.generateInstance();
			example.generateModel();
			CPSolver[] solvers = example.generateSolvers();
			compare(solvers);
		}
	}

	@Test
	public void openShop2() {
		OpenShopExample example=new OpenShopExample(2);
		example.restart=false;
		test(NB_TESTS,example);
	}

	@Test
	public void openShop3() {
		OpenShopExample example=new OpenShopExample(3);
		example.restart=false;
		test(NB_TESTS,example);
	}

	@Test
	public void openShop4() {
		OpenShopExample.MAX_DURATION=100;
		OpenShopExample example=new OpenShopExample(4);
		test(NB_TESTS,example);
	}

	@Test
	public void openShop5() {
		OpenShopExample.MAX_DURATION=15;
		OpenShopExample example=new OpenShopExample(5);
		test(NB_TESTS,example);
	}


}
