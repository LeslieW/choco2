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
import static choco.kernel.common.opres.pack.AbstractHeurisic1BP.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import org.junit.Test;

import java.util.Arrays;

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


	public final static int CAPACITE_DFF=10;

	public final static int[][] DFF1= {
		{9,2,6,7,3,7,5,5,9,4,7,2,3,10,5,3,3,8,3,7},
		{5,4,10,5,6,10,1,3,6,2,6,7,8,4,4,10,8,7,8,8}
	};

	public final static int[][] DFF2= {
		{2,8,2,3,4,1,9,5,3,1,2,2,9,5,7,2,4,7,1,8},
		{2,6,10,1,8,03,1,1,6,1,4,9,1,9,4,2,3,9,4,9}
	};

	public final static int[][] DFF3= {
		{5,6,6,2,8,10,5,6,9,3,10,5,7,9,6,6,3,2,1,4},
		{7,10,5,7,4,9,8,8,4,9,3,9,1,8,4,3,4,10,6,1}
	};
	public final static int[] DFF={8,5,8};

	public final static int[] MODE={COPY_AND_SORT,COPY_AND_SORT,INCREASING,DECREASING,DECREASING};



	private AbstractDDFF lb;






	public void applyFunction(int k,int[] res,int c) {
		lb.applyFunction(k);
		assertTrue("DDFF",Arrays.equals(res,lb.getSizes()));
		assertEquals("capacity",c,lb.getCapacity());
	}


	@Test
	public void testNilSize() {
		AbstractHeurisic1BP h;
		int[] s={5,5,5,5,0,0};
		h=new BestFit1BP(s,10,DECREASING);
		assertEquals("UB", 2,h.computeUB());
	}

	@Test
	public void testBestFit() {
		AbstractHeurisic1BP h;
		for (int i = 0; i < SIZES.length; i++) {
			h=new BestFit1BP(SIZES[i],CAPACITE,MODE[i]);
			assertEquals("UB", BEST_FIT[i],h.computeUB());
		}
	}

	@Test
	public void testFirstFit() {
		AbstractHeurisic1BP h;
		for (int i = 0; i < SIZES.length; i++) {
			h=new BestFit1BP(SIZES[i],CAPACITE,MODE[i]);
			assertEquals("UB", FIRST_FIT[i],h.computeUB());
		}
	}




	@Test
	public void testF0() {
		int[][] res={ {0,6,0,8,10,15,0,7,15,15,5,9},{0,6,0,8,15,15,0,7,15,15,0,9}};
		lb=new FunctionDFF_f0(SIZES[0],CAPACITE);
		applyFunction(4,res[0],CAPACITE);
		lb=new FunctionDFF_f0(SIZES[0],CAPACITE);
		applyFunction(6,res[1],CAPACITE);
	}

	@Test
	public void testF1() {
		int[][] res={{1,1,0,1,2,3,1,1,2,3,1,1},{0,1,0,1,1,2,0,1,2,2,1,1}};
		lb=new FunctionDDFF_f1(SIZES[0],CAPACITE);
		applyFunction(3,res[0],3);
		lb=new FunctionDDFF_f1(SIZES[0],CAPACITE);
		applyFunction(4,res[1],2);
	}

	@Test
	public void testF2() {
		int[][] res={{2,4,0,6,8,10,2,5,8,10,2,6},{0,2,0,4,4,6,0,3,6,6,2,4}};
		lb=new FunctionDFF_f2(SIZES[0],CAPACITE);
		applyFunction(3,res[0],10);
		lb=new FunctionDFF_f2(SIZES[0],CAPACITE);
		applyFunction(4,res[1],6);
	}

	@Test
	public void testDFF() {
		LowerBound2BP dff=new LowerBound2BP(DFF1[0],CAPACITE_DFF,DFF1[1],CAPACITE_DFF);
		assertEquals("LB", DFF[0],dff.computeL_2CM());

		dff=new LowerBound2BP(DFF2[0],CAPACITE_DFF,DFF2[1],CAPACITE_DFF);
		assertEquals("LB", DFF[1],dff.computeL_2CM());

		dff=new LowerBound2BP(DFF3[0],CAPACITE_DFF,DFF3[1],CAPACITE_DFF);
		assertEquals("LB", DFF[2],dff.computeL_2CM());

	}
	//TODO how to integrate the DFF test database
}
