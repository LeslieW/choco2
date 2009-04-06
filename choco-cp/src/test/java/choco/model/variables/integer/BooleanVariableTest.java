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
package choco.model.variables.integer;

import choco.cp.solver.CPSolver;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 18 déc. 2008
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class BooleanVariableTest {


    @Test
    public void test1() throws ContradictionException {
        Solver s = new CPSolver();
        IntDomainVar v1 = ((CPSolver)s).createBooleanVar("v");
        Assert.assertEquals("lower bound",0,v1.getInf());
        Assert.assertEquals("upper bound",1,v1.getSup());
        Assert.assertEquals("domain size",2,v1.getDomainSize());

        Assert.assertTrue("next value -1", v1.getDomain().hasNextValue(-1));
        Assert.assertEquals("getNextValue -1", 0, v1.getDomain().getNextValue(-1));
        Assert.assertTrue("next value 0", v1.getDomain().hasNextValue(0));
        Assert.assertEquals("getNextValue 0", 1, v1.getDomain().getNextValue(0));
        Assert.assertFalse("next value 1", v1.getDomain().hasNextValue(1));
        Assert.assertFalse("next value 2", v1.getDomain().hasNextValue(2));

        Assert.assertFalse("prev value -1", v1.getDomain().hasPrevValue(-1));
        Assert.assertFalse("prev value 0", v1.getDomain().hasPrevValue(0));
        Assert.assertTrue("prev value 1", v1.getDomain().hasPrevValue(1));
        Assert.assertEquals("getNextValue 1", 0, v1.getDomain().getPrevValue(1));
        Assert.assertTrue("prev value 2", v1.getDomain().hasPrevValue(2));
        Assert.assertEquals("getNextValue 2", 1, v1.getDomain().getPrevValue(2));

        s.worldPush();

        Assert.assertFalse("update inf 0",v1.updateInf(0, -1));
        Assert.assertEquals("lower bound",0,v1.getInf());
        Assert.assertEquals("upper bound",1,v1.getSup());
        Assert.assertEquals("domain size",2,v1.getDomainSize());

        Assert.assertTrue("update inf 1",v1.updateInf(1, -1));
        Assert.assertEquals("lower bound",1,v1.getInf());
        Assert.assertEquals("upper bound",1,v1.getSup());
        Assert.assertEquals("domain size",1,v1.getDomainSize());
        Assert.assertTrue("instantiated", v1.isInstantiatedTo(1));

        try{
            v1.updateInf(2, -1);
            Assert.fail("update inf 2");
        }catch (ContradictionException e){

        }

        s.worldPop();
        s.worldPush();

        Assert.assertFalse("update sup 1",v1.updateSup(1, -1));
        Assert.assertEquals("lower bound",0,v1.getInf());
        Assert.assertEquals("upper bound",1,v1.getSup());
        Assert.assertEquals("domain size",2,v1.getDomainSize());

        Assert.assertTrue("update sup 0",v1.updateSup(0, -1));
        Assert.assertEquals("lower bound",0,v1.getInf());
        Assert.assertEquals("upper bound",0,v1.getSup());
        Assert.assertEquals("domain size",1,v1.getDomainSize());
        Assert.assertTrue("instantiated", v1.isInstantiatedTo(0));

        try{
            v1.updateInf(2, -1);
            Assert.fail("update sup -1");
        }catch (ContradictionException e){

        }


        s.worldPop();
        s.worldPush();

        Assert.assertFalse("remove 2",v1.removeVal(2, -1));
        Assert.assertEquals("lower bound",0,v1.getInf());
        Assert.assertEquals("upper bound",1,v1.getSup());
        Assert.assertEquals("domain size",2,v1.getDomainSize());

        Assert.assertTrue("remove 1",v1.removeVal(1, -1));
        Assert.assertEquals("lower bound",0,v1.getInf());
        Assert.assertEquals("upper bound",0,v1.getSup());
        Assert.assertEquals("domain size",1,v1.getDomainSize());
        Assert.assertTrue("instantiated", v1.isInstantiatedTo(0));

        try{
            v1.removeVal(0, -1);
            Assert.fail("remove 0");
        }catch (ContradictionException ex){
        }

        s.worldPop();
        s.worldPush();

        Assert.assertFalse("remove -1",v1.removeVal(-1, -1));
        Assert.assertEquals("lower bound",0,v1.getInf());
        Assert.assertEquals("upper bound",1,v1.getSup());
        Assert.assertEquals("domain size",2,v1.getDomainSize());

        Assert.assertTrue("remove 0",v1.removeVal(0, -1));
        Assert.assertEquals("lower bound",1,v1.getInf());
        Assert.assertEquals("upper bound",1,v1.getSup());
        Assert.assertEquals("domain size",1,v1.getDomainSize());
        Assert.assertTrue("instantiated", v1.isInstantiatedTo(1));

        try{
            v1.removeVal(1, -1);
            Assert.fail("remove 1");
        }catch (ContradictionException ex){
        }

        s.worldPop();
        s.worldPush();

        Assert.assertTrue("instantiate 0",v1.instantiate(0, -1));
        Assert.assertEquals("lower bound",0,v1.getInf());
        Assert.assertEquals("upper bound",0,v1.getSup());
        Assert.assertEquals("domain size",1,v1.getDomainSize());
        Assert.assertTrue("instantiated", v1.isInstantiatedTo(0));

        s.worldPop();
        s.worldPush();

        Assert.assertTrue("instantiate 1",v1.instantiate(1, -1));
        Assert.assertEquals("lower bound",1,v1.getInf());
        Assert.assertEquals("upper bound",1,v1.getSup());
        Assert.assertEquals("domain size",1,v1.getDomainSize());
        Assert.assertTrue("instantiated", v1.isInstantiatedTo(1));

        s.worldPop();
        s.worldPush();

        try{
            Assert.assertFalse("instantiate 2",v1.instantiate(2, -1));
            Assert.fail("unknown value");
        }catch (ContradictionException ex){
        }
        try{
            Assert.assertFalse("instantiate -1",v1.instantiate(-1, -1));
            Assert.fail("unknown value");
        }catch (ContradictionException ex){
        }
    }



    private static int solve1(int type, int SIZE) throws ContradictionException {
        int K = 5;
        int time = 0;
        IntDomainVar[] bool = null;
        CPSolver s = null;
        for(int k = 0; k < K; k++){
            s = new CPSolver();

            bool = new IntDomainVar[SIZE];

            for (int i = 0; i < SIZE; i++) {
                switch (type) {
                    case 0:
                        bool[i] = s.createBooleanVar("bool_" + i);
                        break;
                    case 1:
                        bool[i] = s.createEnumIntVar("bool_" + i, 0, 1);
                        break;
                }
            }

            for (int i = 0; i < SIZE - 1; i++) {
                s.post(s.eq(bool[i], bool[i + 1]));
            }
            s.post(s.eq(SIZE - 1, s.sum(bool)));
            s.solve();
            time += s.getTimeCount();
        }
        return time/K;
    }

    @Test
    @Ignore
    public void test2() throws ContradictionException {
        for(int val = 1024; val < 64*512*6; val*=2){
            System.gc();
            int enu = solve1(1, val);
            System.gc();
            int boo = solve1(0, val);
            System.out.println(enu +" > "+boo + " val:"+val);
            Assert.assertTrue("slower", enu>boo);
        }
    }

}
