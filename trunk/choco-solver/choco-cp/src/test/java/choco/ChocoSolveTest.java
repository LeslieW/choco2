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
package choco;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.propagation.Propagator;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: Jul 20, 2003
 * Time: 5:23:39 PM
 * To change this template use Options | File Templates.
 */

public class ChocoSolveTest {
    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    private Model m;
    private Solver s;
    private IntegerVariable x;
    private IntegerVariable y;
    private IntegerVariable z;
    private Propagator Ap;
    private Constraint A;
    private Constraint B;


    @Before
    public void setUp() {
        m = new CPModel();
        s = new CPSolver();
        x = makeIntVar("X", 1, 5);
        y = makeIntVar("Y", 1, 5);
        z = makeIntVar("Z", 1, 5);
        m.addVariables(Options.V_BOUND, x, y, z);
        A = geq(x, plus(y, 1));
        B = geq(y, plus(z, 1));
    }

    @After
    public void tearDown() {
        A = null;
        B = null;
        x = null;
        y = null;
        z = null;
        m = null;
    }

    @Test
    public void arithmetic() {

        m.addConstraint(A);
        m.addConstraint(B);
        s.read(m);
        Ap = (Propagator)s.getCstr(A);
        Ap.setPassive();
        Ap.setActive();
        try {
            s.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
        LOGGER.info("X : " + s.getVar(x).getInf() + " - > " + s.getVar(x).getSup());
        LOGGER.info("Y : " + s.getVar(y).getInf() + " - > " + s.getVar(y).getSup());
        LOGGER.info("Z : " + s.getVar(z).getInf() + " - > " + s.getVar(z).getSup());
        assertEquals(3, s.getVar(x).getInf());
        assertEquals(5, s.getVar(x).getSup());
        assertEquals(2, s.getVar(y).getInf());
        assertEquals(4, s.getVar(y).getSup());
        assertEquals(1, s.getVar(z).getInf());
        assertEquals(3, s.getVar(z).getSup());

        try {
            s.getVar(z).setVal(2);
            s.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
        LOGGER.info("X : " + s.getVar(x).getInf() + " - > " + s.getVar(x).getSup());
        assertEquals(4, s.getVar(x).getInf());
        assertEquals(5, s.getVar(x).getSup());
        LOGGER.info("Y : " + s.getVar(y).getInf() + " - > " + s.getVar(y).getSup());
        assertEquals(3, s.getVar(y).getInf());
        assertEquals(4, s.getVar(y).getSup());
        LOGGER.info("Z : " + s.getVar(z).getInf() + " - > " + s.getVar(z).getSup());
        assertEquals(2, s.getVar(z).getInf());
        assertEquals(2, s.getVar(z).getSup());

        try {
            s.getVar(x).setSup(4);
            s.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
        LOGGER.info("X : " + s.getVar(x).getInf() + " - > " + s.getVar(x).getSup());
        assertEquals(4, s.getVar(x).getInf());
        assertEquals(4, s.getVar(x).getSup());
        LOGGER.info("Y : " + s.getVar(y).getInf() + " - > " + s.getVar(y).getSup());
        assertEquals(3, s.getVar(y).getInf());
        assertEquals(3, s.getVar(y).getSup());
        LOGGER.info("Z : " + s.getVar(z).getInf() + " - > " + s.getVar(z).getSup());
        assertEquals(2, s.getVar(z).getInf());
        assertEquals(2, s.getVar(z).getSup());
    }

    @Test
    public void arithmetic2() {
        m.addConstraint(A);
        m.addConstraint(B);
        m.addVariable(z);
        s.read(m);
        Ap = (Propagator) s.getCstr(A);
        Ap.setPassive();
        try {
            s.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
        LOGGER.info("X : " + s.getVar(x).getInf() + " - > " + s.getVar(x).getSup());
        assertEquals(1, s.getVar(x).getInf());
        assertEquals(5, s.getVar(x).getSup());
        LOGGER.info("Y : " + s.getVar(y).getInf() + " - > " + s.getVar(y).getSup());
        assertEquals(2, s.getVar(y).getInf());
        assertEquals(5, s.getVar(y).getSup());
        LOGGER.info("Z : " + s.getVar(z).getInf() + " - > " + s.getVar(z).getSup());
        assertEquals(1, s.getVar(z).getInf());
        assertEquals(4, s.getVar(z).getSup());
    }
}
