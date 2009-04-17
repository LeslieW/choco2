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
package choco.model.constraints.reified;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import org.junit.After;
import org.junit.Assert;
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
public class NegationTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();
    private Model m;
    private Solver s;
    private IntegerVariable x;

@After
    public void tearDown() throws Exception {
        x = null;
        m = null;
        s = null;
    }

    @Before
    public void setUp() throws Exception {
        LOGGER.fine("choco.currentElement.reified.NegationTest Testing...");
        m = new CPModel();
        s = new CPSolver();
        x = makeIntVar("X", 1, 10);
        m.addVariable("cp:bound", x);
    }

    @Test
    public void test1() {
        LOGGER.info("NegationTest.test1");
        Constraint c0 = geq((x), (3));

        Constraint not = not(c0);
        LOGGER.info(not.pretty());
        m.addConstraint(not);
        s.read(m);
        LOGGER.info(s.getCstr(not).pretty());
        try {
            s.propagate();
        } catch (ContradictionException e) {
            LOGGER.severe("NegationTest() : Test1#propagate() "+e.getMessage());
            fail();
        }
         s.solve();
        if (s.isFeasible()) {
            do {
                assertTrue("x not instanciated", s.getVar(x).isInstantiated());
                assertFalse("value of x not excepted", s.getVar(x).getVal() > 3);
            } while (s.nextSolution() == Boolean.TRUE);
        }
        assertEquals("Nb solution unexcepted", s.getNbSolutions(), 2);
        LOGGER.info("OK");
    }

    @Test
    public void test1Decomp() {
        LOGGER.info("NegationTest.test1");
        Constraint c0 = geq((x), (3));

        m.setDefaultExpressionDecomposition(true);

        Constraint not = not(c0);
        LOGGER.info(not.pretty());
        m.addConstraint(not);
        s.read(m);
        LOGGER.info(s.getCstr(not).pretty());

        try {
            s.propagate();
        } catch (ContradictionException e) {
            LOGGER.severe("NegationTest() : Test1#propagate() "+e.getMessage());
            fail();
        }
         s.solve();
        if (s.isFeasible()) {
            do {
                assertTrue("x not instanciated", s.getVar(x).isInstantiated());
                assertFalse("value of x not excepted", s.getVar(x).getVal() > 3);
            } while (s.nextSolution() == Boolean.TRUE);
        }
        assertEquals("Nb solution unexcepted", s.getNbSolutions(), 2);
        LOGGER.info("OK");
    }

    @Test
    public void testNotTrue(){
        m = new CPModel();
        s = new CPSolver();
        m.addConstraint(not(TRUE));
        s.read(m);
        s.solve();
        Assert.assertEquals(Boolean.FALSE, s.isFeasible());
    }

    @Test
    public void testNotFalse(){
        m = new CPModel();
        s = new CPSolver();
        m.addConstraint(not(FALSE));
        s.read(m);
        s.solve();
        Assert.assertEquals(Boolean.TRUE, s.isFeasible());
    }
}