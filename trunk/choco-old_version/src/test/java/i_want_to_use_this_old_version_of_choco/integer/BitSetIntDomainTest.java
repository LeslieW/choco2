// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/* File choco.currentElement.search.BitSetIntDomainTest.java, last modified by Francois 21 sept. 2003 10:59:44 */

package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.var.AbstractIntDomain;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomain;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;
import junit.framework.TestCase;

import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * a class implementing tests for backtrackable search
 */
public class BitSetIntDomainTest extends TestCase {
    private Logger logger = Logger.getLogger("choco.currentElement");
    private Problem pb;
    private IntDomainVar x, y;
    AbstractIntDomain yDom;

    protected void setUp() {
        logger.fine("BitSetIntDomain Testing...");
        pb = new Problem();
        x = pb.makeBoundIntVar("X", 1, 100);
        y = pb.makeEnumIntVar("Y", 1, 15);
        yDom = (AbstractIntDomain) y.getDomain();
    }

    protected void tearDown() {
        yDom = null;
        y = null;
        x = null;
        pb = null;
    }

    public void test1() {
        logger.finer("test1");
        try {
            assertEquals(1, yDom.getInf());
            assertEquals(15, yDom.getSup());
            assertEquals(15, yDom.getSize());
            logger.finest("First step passed");

            pb.getEnvironment().worldPush();
            yDom.removeVal(2, -1);
            assertEquals(1, yDom.getInf());
            assertEquals(15, yDom.getSup());
            assertEquals(14, yDom.getSize());
            logger.finest("Second step passed");

            yDom.removeVal(1, -1);
            assertEquals(3, yDom.getInf());
            assertEquals(15, yDom.getSup());
            assertEquals(13, yDom.getSize());
            logger.finest("Third step passed");


            pb.worldPop();
            assertEquals(1, yDom.getInf());
            assertEquals(15, yDom.getSup());
            assertEquals(15, yDom.getSize());
            logger.finest("Fourth step passed");

        } catch (ContradictionException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    public void test2() {
        logger.finer("test2");
        try {

            yDom.removeVal(10,-1);
            yDom.removeVal(12,-1);
            yDom.removeVal(14,-1);
            yDom.removeVal(13,-1);
            yDom.updateSup(14);
            assertEquals(1, yDom.getInf());
            assertEquals(11, yDom.getSup());
            assertEquals(10, yDom.getSize());
            logger.finest("First step passed");

            yDom.updateInf(8);
            assertEquals(8, yDom.getInf());
            assertEquals(11, yDom.getSup());
            assertEquals(3, yDom.getSize());
            logger.finest("Second step passed");

            yDom.removeVal(11, -1);
            assertEquals(8, yDom.getInf());
            assertEquals(9, yDom.getSup());
            assertEquals(2, yDom.getSize());
            logger.finest("Third step passed");
        } catch (ContradictionException e) {
            assertTrue(false);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    /**
     * testing delta domain management
     */
    public void test3() {
        logger.finer("test3");
        Set expectedSet357 = new TreeSet();
        expectedSet357.add(new Integer(3));
        expectedSet357.add(new Integer(5));
        expectedSet357.add(new Integer(7));
        Set expectedSet9 = new TreeSet();
        expectedSet9.add(new Integer(9));

        {
            yDom.freezeDeltaDomain();
            IntIterator it = yDom.getDeltaIterator();
            assertFalse(it.hasNext());
            assertTrue(yDom.releaseDeltaDomain());
        }
        yDom.remove(3);
        yDom.remove(5);
        yDom.remove(7);
        Set tmp357 = new TreeSet();
        yDom.freezeDeltaDomain();
        yDom.remove(9);
        for (IntIterator it = yDom.getDeltaIterator(); it.hasNext();) {
            int val = it.next();
            tmp357.add(new Integer(val));
        }
        assertEquals(expectedSet357, tmp357);
        assertFalse(yDom.releaseDeltaDomain());
        yDom.freezeDeltaDomain();
        Set tmp9 = new TreeSet();
        for (IntIterator it = yDom.getDeltaIterator(); it.hasNext();) {
            int val = it.next();
            tmp9.add(new Integer(val));
        }
        assertEquals(expectedSet9, tmp9);
        assertTrue(yDom.releaseDeltaDomain());
    }

    /**
     * currentElement the restrict method
     */
    public void test4() {
        logger.finer("test2");
        try {
        yDom.removeVal(10,-1);
        yDom.removeVal(12,-1);
        yDom.removeVal(14,-1);
        yDom.removeVal(13,-1);
        yDom.updateSup(14);
        yDom.instantiate(7,-1);
        assertEquals(7, yDom.getInf());
        assertEquals(7, yDom.getSup());
        assertEquals(1, yDom.getSize());
        IntIterator it = yDom.getIterator();
        assertTrue(it.hasNext());
        assertEquals(7, it.next());
        assertFalse(it.hasNext());
        } catch (ContradictionException e) {
            assertTrue(false);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public void testRandomValue() {
        yDom.remove(5);
        yDom.remove(10);
        yDom.remove(12);
        for (int i = 0; i < 100; i++) {
            int val = yDom.getRandomValue();
            assertTrue(yDom.contains(val));
        }

        IntDomain xDom = x.getDomain();
        xDom.updateInf(5);
        xDom.updateSup(10);
        for (int i = 0; i < 100; i++) {
            int val = xDom.getRandomValue();
            assertTrue(xDom.contains(val));
        }
    }
}