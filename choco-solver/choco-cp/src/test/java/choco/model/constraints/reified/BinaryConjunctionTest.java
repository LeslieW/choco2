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

package choco.model.constraints.reified;

import static choco.Choco.*;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;


/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 24 janv. 2008
 * Time: 16:21:59
 */
public class BinaryConjunctionTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();
    private CPModel m;
    private Solver s;
    private IntegerVariable x;

    @After
    public void tearDown() throws Exception {
        x = null;
        s = null;
        m = null;
    }

    @Before
    public void setUp() throws Exception {
        LOGGER.fine("choco.currentElement.reified.BinaryDisjunctionTest Testing...");
        m = new CPModel();
        s = new CPSolver();

    }

    @Test
    public void testBound(){
        LOGGER.info("BinaryConjunctionTest.testEnum");
        x = makeIntVar("X", 1, 10);
        m.addVariable(Options.V_BOUND, x);
        test();
    }

    @Test
    public void testEnum(){
        LOGGER.info("BinaryConjunctionTest.testBound");
        x = makeIntVar("X", 1, 10);
        test();
    }

     @Test
    public void testBoundDecomp(){
        LOGGER.info("BinaryConjunctionTest.testEnum");
        x = makeIntVar("X", 1, 10);
         m.addVariable(Options.V_BOUND, x);
         m.setDefaultExpressionDecomposition(true);
        test();
    }

    @Test
    public void testEnumDecomp(){
        LOGGER.info("BinaryConjunctionTest.testBound");
        x = makeIntVar("X", 1, 10);
        m.setDefaultExpressionDecomposition(true);
        test();
    }

    public void test() {
        //Constraint and = and(c0, c1);
	    Constraint and = and(geq((x),(3)),leq((x),(9)));
        LOGGER.info(and.pretty());
        m.addConstraint(and);
        s.read(m);
        LOGGER.info(s.getCstr(and).pretty());
	    //s.post(and);

        try {
            s.propagate();
        } catch (ContradictionException e) {
            LOGGER.severe("BinaryDisjunctionTest() : Test1#propagate() " + e.getMessage());
            fail();
        }
        s.solve();
        StringBuffer st = new StringBuffer();
        if (s.isFeasible()) {
            do {
                assertTrue("x not instanciated", s.getVar(x).isInstantiated());
                assertTrue("value of x not excepted", s.getVar(x).getVal() > 2 && s.getVar(x).getVal() < 10);
            } while (s.nextSolution() == Boolean.TRUE);
        }
        assertEquals("Nb solution unexcepted", s.getNbSolutions(), 7);
        LOGGER.info("OK");

    }
}