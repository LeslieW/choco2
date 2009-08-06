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
package choco.memory;

import choco.cp.solver.CPSolver;
import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.structure.StoredBipartiteVarList;
import choco.kernel.memory.trailing.EnvironmentTrailing;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 17 juil. 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public class StoredBipartiteVarListTest {

    StoredBipartiteVarList<IntDomainVar> ar;
    IEnvironment env;
    CPSolver s;

    @Before
    public void b(){
        env = new EnvironmentTrailing();
        s = new CPSolver(env);
        ar = new StoredBipartiteVarList<IntDomainVar>(env);
    }

    @After
    public void a(){
        ar = null;
        s = null;
        env = null;
    }


    @Test
    public void test1() throws ContradictionException {
        int n = 4;
        IntDomainVar[] var = new IntDomainVar[n];
        for(int i = 0; i < n; i++){
            var[i] = new IntDomainVarImpl(s, i+"",IntDomainVar.BITSET, 0, 1);
            ar.add(var[i]);
        }

        Iterator it = ar.quickIterator();
        checkIterator(it, var);
        it = ar.getInstanciatedVariableIterator();
        checkIterator(it, new IntDomainVar[0]);
        it = ar.getNotInstanciatedVariableIterator();
        checkIterator(it, var);

        env.worldPush();
        var[1].instantiate(0, -1);

//        ar.isInstanciated(var[1]);

        it = ar.quickIterator();
        checkIterator(it, var);
        it = ar.getInstanciatedVariableIterator();
        checkIterator(it, new IntDomainVar[]{var[1]});
        it = ar.getNotInstanciatedVariableIterator();
        checkIterator(it, new IntDomainVar[]{var[0], var[3], var[2]});

        env.worldPush();
        var[0].instantiate(0, -1);
//        ar.isInstanciated(var[0]);

        it = ar.quickIterator();
        checkIterator(it, var);
        it = ar.getInstanciatedVariableIterator();
        checkIterator(it, new IntDomainVar[]{var[0], var[1]});
        it = ar.getNotInstanciatedVariableIterator();
        checkIterator(it, new IntDomainVar[]{var[2], var[3]});


    }

    @Test
    public void test2() throws ContradictionException {
        int n = 4;
        IntDomainVar[] var = new IntDomainVar[n];
        for(int i = 0; i < n; i++){
            var[i] = new IntDomainVarImpl(s, i+"",IntDomainVar.BITSET, 0, 1);
            ar.add(var[i]);
        }

        env.worldPush();
        var[0].instantiate(0,-1);

        env.worldPush();
        var[3].instantiate(0,-1);
        var[1].instantiate(0,-1);
        var[2].instantiate(0,-1);

        env.worldPop();
        env.worldPush();
        var[3].instantiate(0,-1);
        var[1].instantiate(0,-1);
        var[2].instantiate(0,-1);

        env.worldPop();
        env.worldPop();

        env.worldPush();
        var[0].instantiate(0,-1);
        var[1].instantiate(0,-1);

        env.worldPush();
        var[2].instantiate(0,-1);
        var[3].instantiate(0,-1);

        env.worldPop();
        Iterator it = ar.quickIterator();
        checkIterator(it, var);
        it = ar.getInstanciatedVariableIterator();
        checkIterator(it, new IntDomainVar[]{var[0], var[1]});
        it = ar.getNotInstanciatedVariableIterator();
        checkIterator(it, new IntDomainVar[]{var[3], var[2]});

        env.worldPop();
        it = ar.quickIterator();
        checkIterator(it, var);
        it = ar.getNotInstanciatedVariableIterator();
        checkIterator(it, new IntDomainVar[]{var[3], var[2], var[1], var[0]});
        it = ar.getInstanciatedVariableIterator();
        checkIterator(it, new IntDomainVar[]{});


    }

    private void checkIterator(Iterator it, IntDomainVar[] var){
        for (IntDomainVar aVar : var) {
            Assert.assertTrue(it.hasNext());
            Assert.assertEquals(aVar, it.next());
        }
        Assert.assertFalse(it.hasNext());
    }

}
