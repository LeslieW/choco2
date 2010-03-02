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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import choco.kernel.common.opres.nosum.NoSumList;

/**
 * @author Arnaud Malapert
 *
 */
@SuppressWarnings({"PMD.LocalVariableCouldBeFinal","PMD.MethodArgumentCouldBeFinal"})
public class TestNoSum {

	private final static String MSG="noSum";

	//arrays sorted in non-increasing order
	private final int[] sizes={20,17,15,13,11,9,7,5,3,2,1};

	private final int[] sizes2={100,85,70,60,47,35,27,17,6,4};

	private NoSumList nosum;

	private void initialize(int[] sizes) {
		nosum=new NoSumList(sizes);
		nosum.fillCandidates();
	}

	@Test
	public void testHasSum() {
		initialize(sizes);
		assertFalse(MSG,nosum.noSum(10, 20));
		assertFalse(MSG,nosum.noSum(34, 40));
		assertFalse(MSG,nosum.noSum(65,80));
		assertFalse(MSG,nosum.noSum(65,80));
		assertFalse(MSG,nosum.noSum(85,85));
		assertFalse(MSG,nosum.noSum(103,103));
		assertFalse(MSG,nosum.noSum(50,55));
		assertFalse(MSG,nosum.noSum(55,65));
	}

	@Test
	public void testHasSum2() {
		initialize(sizes2);
		assertFalse(MSG,nosum.noSum(35,39));
		assertFalse(MSG,nosum.noSum(50,51));
		assertFalse(MSG,nosum.noSum(215,225));
		assertFalse(MSG,nosum.noSum(313,314));
		assertFalse(MSG,nosum.noSum(375,378));
		assertFalse(MSG,nosum.noSum(421,427));
		assertFalse(MSG,nosum.noSum(431,436));
	}

	@Test
	public void testHasSumBug() {
		int[] sizes={8,7,6,1};
		initialize(sizes);
		assertFalse(MSG,nosum.noSum(9,9));
	}

	@Test
	public void testNoSum() {
		initialize(sizes);
		nosum.remove(8);
		nosum.remove(9);
		assertTrue(MSG,nosum.noSum(94,96));
		assertTrue(MSG,nosum.noSum(2,4));
		nosum.remove(6);
		assertTrue(MSG,nosum.noSum(7,8));
		assertFalse(MSG,nosum.noSum(82,84));
		assertTrue(MSG,nosum.noSum(87,89));
		nosum.remove(7);
		assertTrue(MSG,nosum.noSum(2,8));
		assertTrue(MSG,nosum.noSum(78,84));
	}

	@Test
	public void testNoSum2() {
		initialize(sizes2);
		assertTrue(MSG,nosum.noSum(11,16));
		assertFalse(MSG,nosum.noSum(42,43)); //false positive
		assertFalse(MSG,nosum.noSum(408,409)); //false positive
		assertFalse(MSG,nosum.noSum(421,423)); //false positive
		assertTrue(MSG,nosum.noSum(435,440));
		assertTrue(MSG,nosum.noSum(448,450));
	}
}


