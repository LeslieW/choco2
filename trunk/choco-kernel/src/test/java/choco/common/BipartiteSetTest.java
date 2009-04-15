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

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.common.util.BipartiteSet;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: Jul 4, 2003
 * Time: 10:07:46 AM
 * To change this template use Options | File Templates.
 */
public class BipartiteSetTest {

	protected final static Logger LOGGER = ChocoLogging.getTestLogger();

	@Before
	public void setUp() {
		LOGGER.fine("BipartiteSet Testing...");
	}

	@After
	public void tearDown() {
	}

	@Test
	public void test1() {
		LOGGER.finer("test1");
		BipartiteSet set = new BipartiteSet();
		Object obj1 = new Object();
		Object obj2 = new Object();
		Object obj3 = new Object();

		set.addLeft(obj1);
		set.addLeft(obj2);
		set.addRight(obj3);

		assertEquals(2, set.getNbLeft());
		assertEquals(1, set.getNbRight());
		assertTrue(set.isLeft(obj1));
		LOGGER.finest("First Step passed");

		set.moveRight(obj1);

		assertEquals(1, set.getNbLeft());
		assertEquals(2, set.getNbRight());
		assertTrue(set.isLeft(obj2));
		assertFalse(set.isLeft(obj1));
		assertFalse(set.isLeft(obj3));
		LOGGER.finest("Second Step passed");

		set.moveAllLeft();

		assertEquals(3, set.getNbLeft());
		assertEquals(0, set.getNbRight());
		assertTrue(set.isLeft(obj1));
		assertTrue(set.isLeft(obj2));
		assertTrue(set.isLeft(obj3));
		LOGGER.finest("Third Step passed");
		ChocoLogging.flushLogs();
	}
}
