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
public class BinaryDisjunctionTest {

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
        x = makeIntVar("X", 1, 10);
    }

    @Test
    public void test1() {
        LOGGER.info("BinaryDisjunctionTest.test1");
        Constraint c0 = leq((x), (3));
        Constraint c1 = geq((x), (9));

        Constraint or = or(c0, c1);
        LOGGER.info(or.pretty());
        m.addConstraint(or);
        s.read(m);
        LOGGER.info(s.getCstr(or).pretty());

        try {
            s.propagate();
        } catch (ContradictionException e) {
            LOGGER.severe("BinaryDisjunctionTest() : Test1#propagate() " + e.getMessage());
            fail();
        }
        s.solve();
        if (s.isFeasible()) {
            do {
                assertTrue("x not instanciated", s.getVar(x).isInstantiated());
                assertFalse("value of x not excepted", s.getVar(x).getVal() > 3 && s.getVar(x).getVal() < 9);
            } while (s.nextSolution() == Boolean.TRUE);
        }
        assertEquals("Nb solution unexcepted", s.getNbSolutions(), 5);
        LOGGER.info("OK");

    }

    @Test
    public void test1Decomp() {
        LOGGER.info("BinaryDisjunctionTest.test1");
        Constraint c0 = leq((x), (3));
        Constraint c1 = geq((x), (9));

        m.setDefaultExpressionDecomposition(true);
        Constraint or = or(c0, c1);
        LOGGER.info(or.pretty());
        m.addConstraint(or);
        s.read(m);
        LOGGER.info(s.getCstr(or).pretty());
        try {
            s.propagate();
        } catch (ContradictionException e) {
            LOGGER.severe("BinaryDisjunctionTest() : Test1#propagate() " + e.getMessage());
            fail();
        }
        s.solve();
        if (s.isFeasible()) {
            do {
                assertTrue("x not instanciated", s.getVar(x).isInstantiated());
                assertFalse("value of x not excepted", s.getVar(x).getVal() > 3 && s.getVar(x).getVal() < 9);
            } while (s.nextSolution() == Boolean.TRUE);
        }
        assertEquals("Nb solution unexcepted", s.getNbSolutions(), 5);
        LOGGER.info("OK");

    }
}
