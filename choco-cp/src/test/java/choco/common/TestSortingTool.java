/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
 *         |  Â°(..)  |                           *
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

import static choco.cp.solver.SettingType.ADDITIONAL_RULES;
import static choco.cp.solver.SettingType.DETECTABLE_PRECEDENCE;
import static choco.cp.solver.SettingType.EDGE_FINDING_D;
import static choco.cp.solver.SettingType.FILL_BIN;
import static choco.kernel.common.util.MathUtil.combinaison;
import static choco.kernel.common.util.MathUtil.factoriel;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import choco.cp.solver.constraints.BitFlags;
import choco.kernel.common.util.ChocoUtil;
import choco.kernel.common.util.IPermutation;



/**
 * @author Arnaud Malapert</br>
 * @since 4 déc. 2008 <b>version</b> 2.0.1</br>
 * @version 2.0.1</br>
 */
public class TestSortingTool {

	public final static int[] IDENTITY = {1,2,3,4,5,6,7,8};

	public final static int[] EXAMPLE1 = {8,3,4,2,5,5,6,5,10};

	public final static int[] EXAMPLE2 = {4,3,4,7,4,3,2,5,12};


	@Test
	public void testIdentity() {
		IPermutation st= ChocoUtil.getSortingPermuation(IDENTITY);
		assertTrue("identity",st.isIdentity());
		test(IDENTITY);
	}

	protected void test(int[] original) {
		IPermutation st= ChocoUtil.getSortingPermuation(original);
		int[] c = Arrays.copyOf(original, original.length);
		Arrays.sort(c);
		assertArrayEquals("get sorted array",c, st.applyPermutation(original));
		for (int i = 0; i < original.length; i++) {
			assertEquals("pi ° pi-1",i,st.getOriginalIndex(st.getPermutationIndex(i)));
		}
		System.out.println(st);
	}

	@Test
	public void testArray1() {
		test(EXAMPLE1);
	}

	@Test
	public void testArray2() {
		test(EXAMPLE2);
	}

	@Test
	public void testMathUtil() {
		assertEquals("factorielle",1,factoriel(-1));
		assertEquals("factorielle",6,factoriel(3));
		assertEquals("factorielle",120,factoriel(5));
		assertEquals("combinaison",1,combinaison(1, 1));
		assertEquals("combinaison",3,combinaison(3, 2));
		assertEquals("combinaison",6,combinaison(4, 2));
		assertEquals("combinaison",1,combinaison(5, 0));
		assertEquals("combinaison",10,combinaison(5, 2));
		assertEquals("combinaison",10,combinaison(5, 3));
	}


	@Test
	public void testBitFlags() {
		BitFlags flags = new BitFlags();
		
		flags.set(ADDITIONAL_RULES,	DETECTABLE_PRECEDENCE, EDGE_FINDING_D);
		//get ans set
		assertTrue( "get", flags.contains(ADDITIONAL_RULES));
		assertTrue( "get", flags.contains(DETECTABLE_PRECEDENCE));
		assertTrue( "get", flags.contains(EDGE_FINDING_D));
		assertFalse( "get" , flags.contains(FILL_BIN));
		//toggle
		flags.toggle(ADDITIONAL_RULES.getBitMask());
		assertFalse( "get", flags.contains(ADDITIONAL_RULES));
		flags.toggle(ADDITIONAL_RULES.getBitMask());
		assertTrue( "get", flags.contains(ADDITIONAL_RULES));
		//unset 
		flags.unset(ADDITIONAL_RULES.getBitMask());
		flags.unset(DETECTABLE_PRECEDENCE.getBitMask());
		
		assertFalse( "get", flags.contains(ADDITIONAL_RULES));
		assertFalse( "get", flags.contains(DETECTABLE_PRECEDENCE));
		assertTrue( "get", flags.contains(EDGE_FINDING_D));
		
	}
}
