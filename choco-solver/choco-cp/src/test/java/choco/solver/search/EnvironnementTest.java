/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
