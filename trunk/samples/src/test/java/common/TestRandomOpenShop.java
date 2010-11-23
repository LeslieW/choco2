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
package common;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;

import samples.tutorials.scheduling.OpenShopScheduling;
import choco.kernel.common.logging.ChocoLogging;

/**
 * @author Arnaud Malapert
 *
 */
public class TestRandomOpenShop {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

	private final OpenShopScheduling oss = new OpenShopScheduling();
	
	private int opt;
	
	private final static int[][] generateRandOS(int n, int maxDur, int seed) {
		final Random rnd = new Random(seed);
		final int[][] durations = new int[n][n];
		LOGGER.log(Level.INFO,"generate a new open shop instance {0}x{0}",n);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				durations[i][j]=rnd.nextInt(maxDur);
			}
		}
		return durations;
	}


	
	private void testOSS(int[][] durations) {
		oss.setBranching(0);
		oss.execute(durations);
		if(oss.solver.existsSolution() && oss.solver.isObjectiveOptimal()) {
			opt = oss.solver.getOptimumValue().intValue();
			for (int i = 1; i < 3; i++) {
				oss.setBranching(i);
				oss.execute(durations);
				if(oss.solver.existsSolution()) {
					if(oss.solver.isObjectiveOptimal()) {
						Assert.assertEquals("branching "+oss.branching ,opt, oss.solver.getObjectiveValue());
					}else {
						Assert.assertTrue("branching "+oss.branching,opt <= oss.solver.getObjectiveValue().intValue());
					}
				}
			}
		}else LOGGER.info("Unknown optimum : test cancelled");
	}

	public void testOSS(int nbTests, int n, int maxDur) {
		LOGGER.setLevel(Level.INFO);
		for (int i = 0; i < nbTests; i++) {
			testOSS(generateRandOS(n, maxDur, i));
		}
		ChocoLogging.flushLogs();
	}

	@Test
	public void openShop2() {
		testOSS(5, 2, 100);
	}

	@Test
	public void openShop3() {
		testOSS(10, 3, 50);
	}

	@Test
	public void openShop4() {
		testOSS(4, 4, 40);
	}

	@Test
	public void openShop5() {
		testOSS(3, 5, 12);
	}


}
