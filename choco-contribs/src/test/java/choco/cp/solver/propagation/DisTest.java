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
package choco.cp.solver.propagation;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolverDis;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.solver.search.QueensTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 4 août 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public class DisTest {

    QueensTest qt;

    @Before
    public void b(){
        //ChocoLogging.setVerbosity(Verbosity.FINEST);
        qt = new QueensTest();
        qt.m = new CPModel();
        BlockingVarEventQueue._LOG = true;
        qt.s1 = new CPSolverDis();
    }

    @After
    public void a(){
        qt = null;
    }

    @Test
    public void test4(){
        qt.queen0(4);
    }

    @Test
    public void test5(){
        qt.queen0(5);
    }

    @Test
    public void test6(){
        qt.queen0(6);
    }

    @Test
    public void test7(){
        qt.queen0(7);
    }

    @Test
    public void test8(){
        qt.queen0(8);
    }

    @Test
    public void test9(){
        qt.queen0(9);
    }

    @Test
    public void test10(){
        qt.queen0(10);
    }
}
