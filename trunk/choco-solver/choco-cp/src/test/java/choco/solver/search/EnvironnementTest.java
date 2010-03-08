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
package choco.solver.search;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.propagation.PropagationEngine;
import choco.kernel.solver.propagation.event.PropagationEvent;
import choco.kernel.solver.propagation.queue.AbstractConstraintEventQueue;
import choco.kernel.solver.propagation.queue.VarEventQueue;
import choco.kernel.solver.variables.integer.IntDomainVar;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.                                b
 * User: charles
 * Date: 16 juil. 2008
 * Time: 16:30:30
 * Test class for Environnement
 */
public class EnvironnementTest {

    private Model m;
    private Solver s;
    IntDomainVar v1, v2;
    PropagationEngine eng;
    VarEventQueue veq;
    VarEventQueue veqcop;
    AbstractConstraintEventQueue ceq;
    AbstractConstraintEventQueue ceqcop;

    @Before
    public final void before(){
        this.m = new CPModel();
        this.s = new CPSolver();
        IntegerVariable w1 = Choco.makeIntVar("v1", 0, 5);
        IntegerVariable w2 = Choco.makeIntVar("v2", 0, 5);
        m.addVariables(w1, w2);
        m.addConstraint(Choco.eq(w1, w2));
        s.read(m);
        v1 = s.getVar(w1);
        v2 = s.getVar(w2);
        eng = s.getPropagationEngine();
        veq = eng.getVarEventQueues()[0];
        ceq = eng.getConstraintEventQueues()[0];
    }
    @After
    public final void after(){
        m = null;
        s = null;
        eng = null;
        veq = null;
        v1 = null;
        v2 = null;
        veqcop = null;
    }

    @Test
    public final void TestWorldPushPop(){
        // Initial worldPush
        s.worldPush();

        // We post the fact that
        try {
            v1.updateInf(1, null, false);
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        PropagationEvent ive = veq.get(0);
        PropagationEvent ice = ceq.get(0);

        // simulate a worldPush during propagation
        s.worldPushDuringPropagation();
        veqcop = eng.getVarEventQueues()[0];
        ceqcop = eng.getConstraintEventQueues()[0];
        Assert.assertFalse("veq is empty", veq.isEmpty());
        Assert.assertTrue("veqcop is not empty", veqcop.isEmpty());
        Assert.assertFalse("ceq is empty", ceq.isEmpty());
        Assert.assertTrue("ceqcop is not empty", ceqcop.isEmpty());
        try {
            v2.updateInf(2, null, false);
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        Assert.assertFalse("veqcop is empty", veqcop.isEmpty());

        s.worldPopDuringPropagation();
        Assert.assertFalse("veq is empty", veq.isEmpty());
        Assert.assertEquals("Not the same event", ive, veq.get(0));
        Assert.assertFalse("ceq is empty", ceq.isEmpty());
        Assert.assertEquals("Not the same event", ice, ceq.get(0));
    }

}
