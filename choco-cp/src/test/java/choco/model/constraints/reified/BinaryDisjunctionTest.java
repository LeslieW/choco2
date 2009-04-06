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

    private Logger logger = Logger.getLogger("choco.currentElement");
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
        logger.fine("choco.currentElement.reified.BinaryDisjunctionTest Testing...");
        m = new CPModel();
        s = new CPSolver();
        x = makeIntVar("X", 1, 10);
    }

    @Test
    public void test1() {
        System.out.println("BinaryDisjunctionTest.test1");
        Constraint c0 = leq((x), (3));
        Constraint c1 = geq((x), (9));

        Constraint or = or(c0, c1);
        System.out.println(or.pretty());
        m.addConstraint(or);
        s.read(m);
        System.out.println(s.getCstr(or).pretty());

        try {
            s.propagate();
        } catch (ContradictionException e) {
            logger.severe("BinaryDisjunctionTest() : Test1#propagate() " + e.getMessage());
            fail();
        }
        s.solve();
        System.out.print(" Tests");
        if (s.isFeasible()) {
            do {
                System.out.print(".");                
                assertTrue("x not instanciated", s.getVar(x).isInstantiated());
                assertFalse("value of x not excepted", s.getVar(x).getVal() > 3 && s.getVar(x).getVal() < 9);
            } while (s.nextSolution() == Boolean.TRUE);
        }
        System.out.print(".");
        assertEquals("Nb solution unexcepted", s.getNbSolutions(), 5);
        System.out.println("OK");

    }

    @Test
    public void test1Decomp() {
        System.out.println("BinaryDisjunctionTest.test1");
        Constraint c0 = leq((x), (3));
        Constraint c1 = geq((x), (9));

        m.setDefaultExpressionDecomposition(true);
        Constraint or = or(c0, c1);
        System.out.println(or.pretty());
        m.addConstraint(or);
        s.read(m);
        System.out.println(s.getCstr(or).pretty());
        try {
            s.propagate();
        } catch (ContradictionException e) {
            logger.severe("BinaryDisjunctionTest() : Test1#propagate() " + e.getMessage());
            fail();
        }
        s.solve();
        System.out.print(" Tests");
        if (s.isFeasible()) {
            do {
                System.out.print(".");
                assertTrue("x not instanciated", s.getVar(x).isInstantiated());
                assertFalse("value of x not excepted", s.getVar(x).getVal() > 3 && s.getVar(x).getVal() < 9);
            } while (s.nextSolution() == Boolean.TRUE);
        }
        System.out.print(".");
        assertEquals("Nb solution unexcepted", s.getNbSolutions(), 5);
        System.out.println("OK");

    }
}
