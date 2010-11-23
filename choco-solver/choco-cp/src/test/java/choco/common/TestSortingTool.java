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

import static choco.cp.solver.constraints.global.scheduling.disjunctive.Disjunctive.*;
import static choco.kernel.common.util.tools.MathUtils.combinaison;
import static choco.kernel.common.util.tools.MathUtils.factoriel;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.logging.Logger;

import org.junit.Test;

import choco.cp.solver.constraints.global.pack.PackSConstraint;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.bitmask.BitMask;
import choco.kernel.common.util.comparator.IPermutation;
import choco.kernel.common.util.tools.PermutationUtils;


/**
 * @author Arnaud Malapert</br>
 * @since 4 déc. 2008 <b>version</b> 2.0.1</br>
 * @version 2.0.1</br>
 */
public class TestSortingTool {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

	public final static int[] IDENTITY = {1,2,3,4,5,6,7,8};

	public final static int[] EXAMPLE1 = {8,3,4,2,5,5,6,5,10};

	public final static int[] EXAMPLE2 = {4,3,4,7,4,3,2,5,12};


	@Test
	public void testIdentity() {
		IPermutation st= PermutationUtils.getSortingPermuation(IDENTITY);
		assertTrue("identity",st.isIdentity());
		test(IDENTITY);
	}

	protected void test(int[] original) {
		IPermutation st= PermutationUtils.getSortingPermuation(original);
		int[] c = Arrays.copyOf(original, original.length);
		Arrays.sort(c);
		assertArrayEquals("get sorted array",c, st.applyPermutation(original));
		for (int i = 0; i < original.length; i++) {
			assertEquals("pi ° pi-1",i,st.getOriginalIndex(st.getPermutationIndex(i)));
		}
		LOGGER.info(""+st);
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
		BitMask flags = new BitMask();
		
		flags.set(NF_NL, DETECTABLE_PRECEDENCE, EDGE_FINDING_D);
		//get ans set
		assertTrue( "get", flags.contains(NF_NL));
		assertTrue( "get", flags.contains(DETECTABLE_PRECEDENCE));
		assertTrue( "get", flags.contains(EDGE_FINDING_D));
		assertFalse( "get" , flags.contains(OVERLOAD_CHECKING));
		//toggle
		flags.toggle(NF_NL.getBitMask());
		assertFalse( "get", flags.contains(NF_NL));
		flags.toggle(NF_NL.getBitMask());
		assertTrue( "get", flags.contains(NF_NL));
		//unset 
		flags.unset(NF_NL.getBitMask());
		flags.unset(DETECTABLE_PRECEDENCE.getBitMask());
		
		assertFalse( "get", flags.contains(NF_NL));
		assertFalse( "get", flags.contains(DETECTABLE_PRECEDENCE));
		assertTrue( "get", flags.contains(EDGE_FINDING_D));
		
	}
}
