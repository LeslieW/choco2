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
package choco.memory;

import choco.kernel.common.util.DisposableIntIterator;
import choco.kernel.memory.trailing.EnvironmentTrailing;
import choco.kernel.memory.trailing.IndexedObject;
import choco.kernel.memory.trailing.StoredIndexedBipartiteSet;
import choco.kernel.memory.trailing.StoredIntBipartiteList;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: Aug 4, 2008
 * Time: 4:17:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class StoredIntBipartiteListTest {
    private Logger logger = Logger.getLogger("src.choco.currentElement");
    private EnvironmentTrailing env;
    private StoredIntBipartiteList iVectA;

    @Before
    public void setUp() {
        logger.fine("StoredIntBipartiteList Testing...");

        env = new EnvironmentTrailing();
        iVectA = (StoredIntBipartiteList) env.makeBipartiteIntList(new int[]{1, 2, 3, 4, 5, 10, 11, 12, 13, 14, 15, 200});
    }

    @After
    public void tearDown() {
        iVectA = null;
        env = null;
    }

    @Test
    public void test1() {
        assertEquals(12, iVectA.size());
        System.out.println("" + iVectA.pretty());
        env.worldPush();
        DisposableIntIterator it = iVectA.getIterator();
        int cpt = 6;
        while (it.hasNext() && cpt > 0) {
            it.next();
            cpt--;
            if (cpt == 0) {
                it.remove();
            }
        }
        it.dispose();
        System.out.println("" + iVectA.pretty());
        assertEquals(11, iVectA.size());
        assertEquals(15, iVectA.get(10));
        env.worldPush();
        it = iVectA.getIterator();
        cpt = 6;
        while (it.hasNext() && cpt > 0) {
            it.next();
            it.remove();
            cpt--;
        }
        it.dispose();
        System.out.println("" + iVectA.pretty());
        assertEquals(5, iVectA.size());
        env.worldPop();
        System.out.println("" + iVectA.pretty());
        assertEquals(11, iVectA.size());
        env.worldPop();
        assertEquals(12, iVectA.size());
        env.worldPush();
        it = iVectA.getIterator();
        System.out.println("before " + iVectA.pretty());
        while (it.hasNext()) {
            System.out.println("value " + it.next());
            it.remove();
        }
        it.dispose();
        System.out.println("after" + iVectA.pretty());
        assertEquals(0, iVectA.size());

    }

    public class StupidInt implements IndexedObject {
        public int index;
        public int value;

        public StupidInt(int value, int id) {
            this.index = id;
            this.value = value;
        }

        public int getObjectIdx() {
            return index;
        }
    }

    @Test
    public void test2() {

        StoredIndexedBipartiteSet iVectB;
        IndexedObject[] stint = new StupidInt[12];
        stint[0] = new StupidInt(10, 0);
        stint[1] = new StupidInt(100, 1);
        stint[2] = new StupidInt(103, 2);
        stint[3] = new StupidInt(1000, 3);
        stint[4] = new StupidInt(1003, 4);
        stint[5] = new StupidInt(20, 15);
        stint[6] = new StupidInt(200, 6);
        stint[7] = new StupidInt(203, 14);
        stint[8] = new StupidInt(2003, 8);
        stint[9] = new StupidInt(10000, 39);
        stint[10] = new StupidInt(11, 10);
        stint[11] = new StupidInt(13, 11);
        env = new EnvironmentTrailing();
        iVectB = (StoredIndexedBipartiteSet) env.makeBipartiteSet(stint);

        assertEquals(12, iVectB.size());
        System.out.println("" + iVectB.pretty());
        env.worldPush();
        StoredIndexedBipartiteSet.BipartiteSetIterator it = iVectB.getObjectIterator();
        int cpt = 6;
        while (it.hasNext() && cpt > 0) {
            it.nextObject();
            cpt--;
            if (cpt == 0) {
                it.remove();
            }
        }
        it.dispose();
        System.out.println("" + iVectB.pretty());
        assertEquals(11, iVectB.size());
        env.worldPush();
        iVectB.remove(stint[9]);
        System.out.println("" + iVectB.pretty());
        assertEquals(10, iVectB.size());
        env.worldPop();
        System.out.println("" + iVectB.pretty());
        assertEquals(11, iVectB.size());
        iVectB.remove(stint[9]);
        System.out.println("" + iVectB.pretty());
        it = iVectB.getObjectIterator();
        cpt = 2;
        while (it.hasNext() && cpt > 0) {
            it.next();
            it.remove();
            cpt--;
        }
        it.dispose();
        System.out.println("" + iVectB.pretty());
        iVectB.remove(stint[11]);
        System.out.println("" + iVectB.pretty());
        assertEquals(7, iVectB.size());
        env.worldPop();
        System.out.println("" + iVectB.pretty());
        assertEquals(12, iVectB.size());
    }
}
