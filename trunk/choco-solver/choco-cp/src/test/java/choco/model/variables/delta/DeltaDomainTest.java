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
*                   N. Jussien    1999-2009      *
**************************************************/
package choco.model.variables.delta;

import choco.cp.solver.CPSolver;
import choco.cp.solver.variables.delta.BitSetDeltaDomain;
import choco.cp.solver.variables.delta.ChainDeltaDomain;
import choco.cp.solver.variables.integer.AbstractIntDomain;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.delta.IDeltaDomain;
import choco.kernel.solver.variables.integer.IntDomainVar;
import gnu.trove.TIntArrayList;
import gnu.trove.TIntHashSet;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 11 déc. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public class DeltaDomainTest {

    @Test
    public void test01(){
        Solver s = new CPSolver();
        IntDomainVar v = s.createEnumIntVar("v", 1, 10);
        IDeltaDomain dom = new BitSetDeltaDomain(10, 1);
        int[] rem_values = new int[]{1,2,8,9,10};
        for(int i : rem_values){
            dom.remove(i);
        }

        dom.freeze();
        TIntArrayList values = new TIntArrayList();
        DisposableIntIterator dit = dom.iterator();
        while(dit.hasNext()){
            values.add(dit.next());
        }

        Assert.assertEquals(5, values.size());
        values.sort();
        Assert.assertArrayEquals(rem_values, values.toNativeArray());

        dom.remove(4);
        Assert.assertFalse(dom.isReleased());
        dom.clear();
        Assert.assertTrue(dom.isReleased());
    }

    @Test
    public void test02(){
        Solver s = new CPSolver();
        IntDomainVar v = s.createEnumIntVar("v", 1, 10);
        IDeltaDomain dom = new ChainDeltaDomain(10, 1);
        int[] rem_values = new int[]{1,2,8,9,10};
        for(int i : rem_values){
            dom.remove(i);
        }

        dom.freeze();
        TIntArrayList values = new TIntArrayList();
        DisposableIntIterator dit = dom.iterator();
        while(dit.hasNext()){
            values.add(dit.next());
        }

        Assert.assertEquals(5, values.size());
        values.sort();
        Assert.assertArrayEquals(rem_values, values.toNativeArray());

        dom.remove(4);
        Assert.assertFalse(dom.isReleased());
        dom.clear();
        Assert.assertTrue(dom.isReleased());
    }

    @Test
    public void test1() {
        Random r;
        for (int i = 0; i < 20; i++) {
            r = new Random(i);
            Solver s = new CPSolver();
            IntDomainVar v = null;
            switch (r.nextInt(4)) {
                case 0:
                    v = s.createEnumIntVar("v", 1, 10);
                    break;
                case 1:
                    v = s.createIntVar("v", IntDomainVar.LINKEDLIST, 1, 10);
                    break;
                case 2:
                    v = s.createIntVar("v", IntDomainVar.BINARYTREE, 1, 10);
                    break;
                case 3:
                    v = s.createIntVar("v", IntDomainVar.BIPARTITELIST, 1, 10);
                    break;
            }

            AbstractIntDomain dom = (AbstractIntDomain) v.getDomain();

            dom.remove(5);
            dom.updateInf(3);
            dom.updateSup(7);

            dom.freezeDeltaDomain();
            TIntHashSet set1258910 = new TIntHashSet(new int[]{1, 2, 5, 8, 9, 10});
            TIntHashSet set = new TIntHashSet();
            DisposableIntIterator dit = dom.getDeltaIterator();
            while (dit.hasNext()) {
                set.add(dit.next());
            }
            Assert.assertEquals(set1258910, set);

            dom.remove(4);

            Assert.assertFalse(dom.releaseDeltaDomain());
        }
    }

    @Test
    public void test2(){
        Solver s = new CPSolver();
        IntDomainVar v = s.createBoundIntVar("v", 1, 10);
        v.getEvent().addPropagatedEvents(IntVarEvent.BOUNDSbitvector + IntVarEvent.REMVALbitvector);
        AbstractIntDomain dom = (AbstractIntDomain)v.getDomain();

        dom.updateInf(3);
        dom.updateSup(7);

        dom.freezeDeltaDomain();

        DisposableIntIterator dit = dom.getDeltaIterator();
        Assert.assertTrue(dit.hasNext());
        Assert.assertEquals(1, dit.next());
        Assert.assertTrue(dit.hasNext());
        Assert.assertEquals(2, dit.next());
        Assert.assertTrue(dit.hasNext());
        Assert.assertEquals(8, dit.next());
        Assert.assertTrue(dit.hasNext());
        Assert.assertEquals(9, dit.next());
        Assert.assertTrue(dit.hasNext());
        Assert.assertEquals(10, dit.next());
        Assert.assertFalse(dit.hasNext());

        dom.updateInf(4);

        Assert.assertFalse(dom.releaseDeltaDomain());
    }


    @Test
    public void test3(){
        Solver s = new CPSolver();
        IntDomainVar v = s.createBooleanVar("v");
        //v.getEvent().addPropagatedEvents(IntVarEvent.BOUNDSbitvector + IntVarEvent.REMVALbitvector);
        AbstractIntDomain dom = (AbstractIntDomain)v.getDomain();

        dom.restrict(1);

        dom.freezeDeltaDomain();

        DisposableIntIterator dit = dom.getDeltaIterator();
        Assert.assertTrue(dit.hasNext());
        Assert.assertEquals(0, dit.next());
        Assert.assertFalse(dit.hasNext());

        Assert.assertTrue(dom.releaseDeltaDomain());
    }


    @Test
    public void test4() {
        Set<Integer> expectedSet357 = new TreeSet<Integer>();
        expectedSet357.add(3);
        expectedSet357.add(5);
        expectedSet357.add(7);
        Set<Integer> expectedSet9 = new TreeSet<Integer>();
        expectedSet9.add(9);

        int[] domtype = new int[]{IntDomainVar.BIPARTITELIST, IntDomainVar.BINARYTREE,
                        IntDomainVar.LINKEDLIST, IntDomainVar.BITSET};
        Random r;
        for (int i = 0; i < 10; i++) {
            r = new Random(i);

            Solver s = new CPSolver();
            IntDomainVar v = s.createIntVar("v", domtype[r.nextInt(domtype.length)], 1, 10);
            AbstractIntDomain yDom = (AbstractIntDomain) v.getDomain();

            yDom.freezeDeltaDomain();
            DisposableIntIterator it = yDom.getDeltaIterator();
            assertFalse(it.hasNext());
            assertTrue(yDom.releaseDeltaDomain());
            it.dispose();

            yDom.remove(3);
            yDom.remove(5);
            yDom.remove(7);
            Set tmp357 = new TreeSet();
            yDom.freezeDeltaDomain();
            yDom.remove(9);
            for (it = yDom.getDeltaIterator(); it.hasNext();) {
                int val = it.next();
                tmp357.add(val);
            }
            it.dispose();
            assertEquals(expectedSet357, tmp357);
            assertFalse(yDom.releaseDeltaDomain());
            yDom.freezeDeltaDomain();
            Set tmp9 = new TreeSet();
            for (it = yDom.getDeltaIterator(); it.hasNext();) {
                int val = it.next();
                tmp9.add(val);
            }
            it.dispose();
            assertEquals(expectedSet9, tmp9);
            assertTrue(yDom.releaseDeltaDomain());
        }
    }

}
