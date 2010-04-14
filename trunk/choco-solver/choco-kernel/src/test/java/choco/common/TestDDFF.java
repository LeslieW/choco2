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

import choco.kernel.common.opres.pack.*;
import choco.kernel.common.util.tools.MathUtils;
import static choco.kernel.common.opres.pack.AbstractHeurisic1BP.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import gnu.trove.TIntArrayList;

import java.util.Arrays;
import java.util.Random;

/**
 * @author Arnaud Malapert
 *
 */
public class TestDDFF {

	public final static int[][] SIZES= {
		{3,6,1,8,10,14,3,7,12,13,5,9},
		{3,15,4,7,15,12,11,14,2,0,0,3},
		{0,0,2,3,7,8,8,12,12,13,14,14},
		{15,13,12,11,11,11,5,5,3,3,1,0},
		{0,0,0,0,0,0,0,0,0,0,0,0}
	};

	public final static int[] FIRST_FIT={7,6,7,7,0};
	public final static int[] BEST_FIT={7,6,7,7,0};

	public final static int CAPACITE=15;

	private FunctionDDFF ddff;

	private final TIntArrayList items = new TIntArrayList();


	private void testHeuristics(AbstractHeurisic1BP h, int[] res) {
		for (int i = 0; i < SIZES.length; i++) {
			items.resetQuick();
			items.add(SIZES[i]);
			items.sort();
			assertEquals("UB", res[i],h.computeUB(items));
		}
	}

	@Test
	public void testFirstFit() {
		testHeuristics( new FirstFit1BP(CAPACITE), FIRST_FIT);
	}

	@Test
	public void testBestFit() {
		testHeuristics( new BestFit1BP(CAPACITE), BEST_FIT);
	}


	private void testDDFF(int[] expected, int expectedCapacity) {
		for (int i = 0; i < SIZES[0].length; i++) {
			assertEquals(expected[i], ddff.apply(SIZES[0][i]));
		}
		assertEquals(expectedCapacity, ddff.getCurrentCapacity());
	}


	@Test
	public void testF0() {
		final int[][] res={ {0,6,0,8,10,15,0,7,15,15,5,9},{0,6,0,8,15,15,0,7,15,15,0,9}};
		ddff = new FunctionF0(CAPACITE);
		ddff.setParameter(4);
		testDDFF( res[0], CAPACITE);
		ddff.setParameter(6);
		testDDFF( res[1], CAPACITE);
	}

	@Test
	public void testF1() {
		final int[][] res={{1,1,0,1,2,3,1,1,2,3,1,1},{0,1,0,1,1,2,0,1,2,2,1,1}};
		ddff =new FunctionF1(CAPACITE);
		items.resetQuick();
		items.add(SIZES[0]);
		items.sort();
		( (FunctionF1) ddff).setItems(items);
		ddff.setParameter(3);
		testDDFF(res[0], 3);
		ddff.setParameter(4);
		testDDFF(res[1], 2);
	}

	@Test
	public void testF2() {
		final int[][] res={{2,4,0,6,8,10,2,4,8,10,2,6},{0,2,0,4,4,6,0,2,6,6,2,4}};
		ddff =new FunctionF2(CAPACITE);
		ddff.setParameter(3);
		testDDFF(res[0], 10);
		ddff.setParameter(4);
		testDDFF(res[1], 6);
	}
	@Test
	public void testBugF2() {
		final int[] bugF2 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
				2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 
				3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 7, 7, 7, 
				8, 8, 9, 9, 9, 10, 11, 11, 11, 11, 11, 11, 12, 13, 14, 15, 15};
		ddff =new FunctionF2(CAPACITE);
		ddff.setParameter(1);
		for (int i = 0; i < SIZES[0].length; i++) {
			assertEquals(2 * bugF2[i], ddff.apply(bugF2[i]));
		}
		assertEquals(2 * CAPACITE, ddff.getCurrentCapacity());
	}
	
	private void generatePacking(int nbB, int capa, long seed) {
		items.resetQuick();
		final Random rnd = new Random(seed);
		int b = 0;
		while(b < nbB) {
			int c = capa;
			while( c > 0) {
				final int s =  rnd.nextInt(c + 1);
				items.add(s);
				c -= s;
			}
			b++;
		}
		items.sort();
		assertEquals( MathUtils.sum(items.toNativeArray()), nbB*capa);
	}


	private void testPacking(int n, int capa, int seed) {
		generatePacking(n, capa, seed);
		final PackDDFF ddffs = new PackDDFF(capa);
		//System.out.println(items);
		ddffs.setItems(items);
		int lb = ddffs.computeDDFF();
		assertEquals(n, lb);
	}

	@Test
	public void testPacking() {
		final int n = 10;
		for (int seed = 0; seed < n; seed++) {
			testPacking(20, CAPACITE, seed);
		}
	}

	@Test
	public void testPackingLargeCapa() {
		final int n = 10;
		for (int seed = 0; seed < n; seed++) {
			testPacking(10, CAPACITE*10, seed);
		}
	}

}
