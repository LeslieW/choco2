/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2008      *
 **************************************************/
package choco.memory;

import choco.kernel.common.util.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.structure.TwoStatesPartiallyStoredIntVector;
import choco.kernel.memory.trailing.EnvironmentTrailing;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 19 juin 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class TwoStatesPartiallyStoredIntVectorTest {

    IEnvironment env;

    @Before
    public void b(){
        env = new EnvironmentTrailing();
    }

    @Test
    @Ignore
    public void test1(){
        TwoStatesPartiallyStoredIntVector t = new TwoStatesPartiallyStoredIntVector(env);
        t.add(1000004, true);
        t.add(1000006, false);
        t.add(1000008,false);

        t.set(1000008, true);

        DisposableIntIterator it = t.getIndexIterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(1000004, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(1000008, it.next());
        Assert.assertFalse(it.hasNext());

        t.set(1000006, true);

        it = t.getIndexIterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(1000004, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(1000008, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(1000006, it.next());
        Assert.assertFalse(it.hasNext());

        t.set(1000004, false);

        it = t.getIndexIterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(1000006, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(1000008, it.next());
        Assert.assertFalse(it.hasNext());

        t.set(1000006, false);

        it = t.getIndexIterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(1000008, it.next());
        Assert.assertFalse(it.hasNext());

        t.set(1000008, false);

        it = t.getIndexIterator();
        Assert.assertFalse(it.hasNext());

        t.set(1000008, true);
        it = t.getIndexIterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(1000008, it.next());
        Assert.assertFalse(it.hasNext());
    }

    @Test
    @Ignore
    public void test2(){
        TwoStatesPartiallyStoredIntVector t = new TwoStatesPartiallyStoredIntVector(env);
        t.staticAdd(4, true);
        t.staticAdd(6, false);
        t.staticAdd(8,false);

        t.set(8, true);

        DisposableIntIterator it = t.getIndexIterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(4, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(8, it.next());
        Assert.assertFalse(it.hasNext());

        t.set(6, true);

        it = t.getIndexIterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(4, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(8, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(6, it.next());
        Assert.assertFalse(it.hasNext());

        t.set(4, false);

        it = t.getIndexIterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(6, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(8, it.next());
        Assert.assertFalse(it.hasNext());

        t.set(6, false);

        it = t.getIndexIterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(8, it.next());
        Assert.assertFalse(it.hasNext());

        t.set(8, false);

        it = t.getIndexIterator();
        Assert.assertFalse(it.hasNext());

        t.set(8, true);
        it = t.getIndexIterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(8, it.next());
        Assert.assertFalse(it.hasNext());
        env.worldPush();

        t.set(8, false);
        t.set(6, true);
        t.set(4, true);

        env.worldPop();
        it = t.getIndexIterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(8, it.next());
        Assert.assertFalse(it.hasNext());
    }

}
